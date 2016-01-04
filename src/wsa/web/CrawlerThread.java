package wsa.web;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import wsa.web.CrawlerC.CrawlerState;
import wsa.web.html.Parsed;

public class CrawlerThread extends Thread {

	private ExecutorService executor;
	public CrawlerThread(AsyncLoader loader)
	{
		this(loader, null, null, null, null);
	}
	public CrawlerThread(AsyncLoader loader,
			Collection<URI> loaded,
			Collection<URI> toLoad,
			Collection<URI> errs,
			Predicate<URI> pageLink) {

		setCrawlerState(CrawlerState.RUNNABLE);

		this.loader = loader;

		if(loaded != null) loaded.addAll(loaded);
		if(toLoad != null) for(URI uri : toLoad) add(uri);
		if(errs != null) this.errors.addAll(errs);
		if(pageLink != null) this.predicate = pageLink;
		
		this.executor = Executors.newCachedThreadPool();
	}

	synchronized public CrawlerState getCrawlerState()
	{
		return state;
	}

	synchronized public void setCrawlerState(CrawlerState newState)
	{
		System.out.println("SETTING STATE TO: " + newState);
		this.state = newState;

		if(newState != CrawlerState.SUSPENDED)
			synchronized (lock) {
				lock.notify();
			}
	}

	private Set<URI> loaded = Collections.synchronizedSet(new HashSet<URI>());
	private Set<URI> toLoad = Collections.synchronizedSet(new HashSet<URI>());
	private Set<URI> errors = Collections.synchronizedSet(new HashSet<URI>());

	public Boolean lock = false;

	private CrawlerState state;
	private Predicate<URI> predicate = (u) -> {return false;};
	private AsyncLoader loader;

	private List<CrawlerResult> results = Collections.synchronizedList(new ArrayList<CrawlerResult>());
	public void run()
	{
		System.out.println("NEW THREAD: " + Thread.currentThread().getName());

		setCrawlerState(CrawlerState.RUNNING);

		while(true)
		{
			try
			{
				if(getCrawlerState() == CrawlerState.CANCELLED)
				{
					System.out.println("CRAWLER HAS BEEN CANCELLED. EXITING.");
					loader.shutdown();
					return;
				}

				if(toLoad.isEmpty())
				{
					System.out.println("QUEUE IS EMPTY. FORCING SUSPENDED STATE.");
					setCrawlerState(CrawlerState.SUSPENDED);
				}

				if(state == CrawlerState.SUSPENDED)
					synchronized (lock) {
						try {
							System.out.println("CRAWLER IS GOING TO SUSPEND.");
							lock.wait();
							System.out.println("WAKENED UP!");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				if(toLoad.isEmpty()) continue;

				System.out.println("|QUEUE| = " + toLoad.size());
				System.out.println("EXTRACTING NEXT URI...");
				URI uri = toLoad.iterator().next();
				toLoad.remove(uri);
				System.out.println("|QUEUE| = " + toLoad.size());

				if(!uri.isAbsolute()) continue;

				System.out.println("DOWNLOADING:" + uri);

				Future<LoadResult> future = loader.submit(uri.toURL());

				executor.submit(() ->{
					try{
						LoadResult loadResult = future.get();
						
						System.out.println("RISULTATO: " + (loadResult.exc == null ? "OK" : "FAILED"));
						System.out.println("# IMMAGINI: " + loadResult.parsed.getByTag("img").size());
						for(Parsed.Node img : loadResult.parsed.getByTag("img")) System.out.println("\t" + img.attr.get("src"));
	
						if(loadResult.exc != null) errors.add(uri);
						else loaded.add(uri);
	
						boolean linkPage = false;
						List<URI> links = null;
						List<String> linksErrors = null;
						if(predicate != null && predicate.test(uri))
						{
							linkPage = true;
							links = new ArrayList<>();
							for(String link : loadResult.parsed.getLinks())
							{
								System.out.println("ADDING NEW LINK TO CRAWLER RESULT: " + link);
	
								try
								{
									URI uriLink = new URI(link);
									add(uriLink);
	
									links.add(uriLink);
								}
								catch(URISyntaxException e)
								{
									if(linksErrors == null) linksErrors = new ArrayList<>();
									linksErrors.add(link);
								}
							}
						}
	
						results.add(new CrawlerResult(uri, linkPage, links, linksErrors, loadResult.exc));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				});
			}
			catch (MalformedURLException e)
			{
				results.add(new CrawlerResult(null, false, null, null, e));
			}
		}
	}

	public void add(URI uriLink) {

		if(this.errors.contains(uriLink)) return;
		if(this.loaded.contains(uriLink)) return;

		this.toLoad.add(uriLink);
		synchronized (toLoad) {toLoad.notify();}
	}

	public Set<URI> getLoaded() {
		return loaded;
	}

	public Set<URI> getToLoad() {
		return toLoad;
	}

	public Set<URI> getErrors() {
		return errors;
	}

	public List<CrawlerResult> getResults() {
		return results;
	}
}
