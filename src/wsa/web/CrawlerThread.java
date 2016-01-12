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
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import wsa.web.CrawlerC.CrawlerState;
import wsa.web.html.Parsed;

public class CrawlerThread extends Thread {

	private final Set<URI> loaded = Collections.synchronizedSet(new HashSet<URI>());
	private final Set<URI> toLoad = Collections.synchronizedSet(new HashSet<URI>());
	private final Set<URI> errors = Collections.synchronizedSet(new HashSet<URI>());
	private final List<CrawlerResult> results = Collections.synchronizedList(new ArrayList<CrawlerResult>());
	
	private final Boolean lock = false;

	private CrawlerState state;
	private Predicate<URI> predicate = (u) -> {return false;};
	private AsyncLoader loader;

	private ExecutorService resultThreadsExecutor;
	
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
		
		this.resultThreadsExecutor = Executors.newCachedThreadPool();
	}

	synchronized public CrawlerState getCrawlerState()
	{
		return state;
	}

	synchronized public void setCrawlerState(CrawlerState newState)
	{
		setCrawlerState(newState, false);
	}
	synchronized public void setCrawlerState(CrawlerState newState, boolean wait)
	{
		System.out.println("SETTING STATE TO: " + newState);
		this.state = newState;

		if(newState == CrawlerState.SUSPENDED)
		{
			synchronized (lock) {
				try {
					System.out.println("CRAWLER IS GOING TO SUSPEND.");
					lock.wait();
					System.out.println("WAKENED UP!");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		// In case the crawler has been suspended
		if(newState == CrawlerState.SUSPENDED || newState == CrawlerState.CANCELLED)
		{
			// All the sub-threads which are trying to download the URLs are cancelled
			// TODO: check that the sub-thread gets notified and really terminated.
			
			
			resultThreadsExecutor.shutdownNow();
			
			// And the main crawler thread waits for the sub-threads to finish
			try
			{
				resultThreadsExecutor.awaitTermination(15, TimeUnit.SECONDS);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			// If the crawler has also been cancelled, also the thread waiting for results should be stoppped
			if(newState == CrawlerState.CANCELLED)
			{
				this.loader.shutdown();
			}
		}
		
		if(newState == CrawlerState.CANCELLED)
		{
			System.out.println("CRAWLER HAS BEEN CANCELLED. EXITING.");
			loader.shutdown();
		}
		
		if(newState != CrawlerState.SUSPENDED)
			synchronized (lock) {
				lock.notify();
			}
	}
	
	public void run()
	{
		System.out.println("NEW THREAD: " + Thread.currentThread().getName() + ". " + "NUMERO DI THREAD ATTIVI: " + Thread.activeCount());

		setCrawlerState(CrawlerState.RUNNING);

		while(true)
		{
			try
			{
				if(getCrawlerState() == CrawlerState.CANCELLED) break;

				if(toLoad.isEmpty())
				{
					System.out.println("QUEUE IS EMPTY. FORCING SUSPENDED STATE.");
					setCrawlerState(CrawlerState.SUSPENDED, true);
				}

				if(toLoad.isEmpty()) continue;

				System.out.println("|QUEUE| = " + toLoad.size());
				System.out.println("EXTRACTING NEXT URI...");
				URI uri = toLoad.iterator().next();
				System.out.println("|QUEUE| = " + toLoad.size());

				if(!uri.isAbsolute())
				{
					toLoad.remove(uri);
					continue;
				}

				System.out.println("DOWNLOADING:" + uri);

				Future<LoadResult> future = loader.submit(uri.toURL());

				resultThreadsExecutor.submit(() ->{
					try{
						LoadResult loadResult = future.get();
						
						System.out.println("RISULTATO: " + (loadResult.exc == null ? "OK" : "FAILED"));
						System.out.println("# IMMAGINI: " + loadResult.parsed.getByTag("img").size());
						for(Parsed.Node img : loadResult.parsed.getByTag("img")) System.out.println("\t" + img.attr.get("src"));
	
						if(loadResult.exc != null) errors.add(uri);
						else loaded.add(uri);
						
						toLoad.remove(uri);
	
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
