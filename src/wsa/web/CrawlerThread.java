package wsa.web;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

		if(loaded != null) this.loaded.addAll(loaded);
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
		System.out.println("SETTING STATE TO: " + newState);
		this.state = newState;

//		if(newState == CrawlerState.SUSPENDED)
//		{
//			synchronized (lock) {
//				try {
//					System.out.println("CRAWLER IS GOING TO SUSPEND.");
//					lock.wait();
//					System.out.println("WAKENED UP!");
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		
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
		super.run();
		
		setCrawlerState(CrawlerState.RUNNING);
		System.out.println("NEW THREAD: " + Thread.currentThread().getName() + ". " + "NUMERO DI THREAD ATTIVI: " + Thread.activeCount());

		while(true)
		{
//			try
//			{
				if(getCrawlerState() == CrawlerState.CANCELLED) break;

				if(getToLoad().isEmpty() || getCrawlerState() == CrawlerState.SUSPENDED)
				{
					System.out.println("QUEUE IS EMPTY. FORCING SUSPENDED STATE.");
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

				if(getToLoad().isEmpty()) continue;

				// System.out.println("|QUEUE| = " + toLoad.size());
				// System.out.println("EXTRACTING NEXT URI...");
				URI uri = next();
				// System.out.println("|QUEUE| = " + toLoad.size());

				if(!uri.isAbsolute()) continue;

				System.out.println("DOWNLOADING:" + uri);

				resultThreadsExecutor.submit(() ->{
					
					URL url = null;
					try
					{
						url = uri.toURL();
					}
					catch (MalformedURLException e)
					{
						getResults().add(new CrawlerResult(null, false, null, null, e));
						getErrors().add(uri);
						return;
					}
					
					Future<LoadResult> future = loader.submit(url);
					
					try{
						LoadResult loadResult = future.get();
						if(loadResult == null) return;
						System.out.println("RISULTATO PER "+loadResult.url+" : " + (loadResult.exc == null ? "OK" : "FAILED: " + loadResult.exc.toString()));
						if(loadResult.parsed != null)
						{
							System.out.println("=============================");
							System.out.println("URL:" + loadResult.url);
							System.out.println("# IMMAGINI: " + loadResult.parsed.getByTag("img").size());
							System.out.println("# LINKS: " + loadResult.parsed.getLinks().size());
							// for(Parsed.Node img : loadResult.parsed.getByTag("img")) System.out.println("\t" + img.attr.get("src"));
						}

						boolean linkPage = false;
						List<URI> links = null;
						List<String> linksErrors = null;
						if(predicate != null && predicate.test(uri))
						{
							linkPage = true;
							links = new ArrayList<>();
							linksErrors = new ArrayList<>();
							
							if(loadResult.parsed != null)
							for(String link : loadResult.parsed.getLinks())
							{
//								 System.out.println("ADDING NEW LINK TO CRAWLER RESULT: " + link);
	
								try
								{
									URI uriLink = new URI(link);
									
									// ADD THE NEW LINK TO THE QUEUE
									if(uriLink.getScheme() == null)
									{
										System.out.println("RELATIVE URI: " + uriLink);
										uriLink = new URI(uri.getScheme() + "://" +  uri.getHost() + uriLink);
										System.out.println("NEW ABSOLUTE URI: " + uriLink);
									}
									this.add(uriLink);
	
									links.add(uriLink);
								}
								catch(URISyntaxException e)
								{
									linksErrors.add(link);
								}
							}
						}
	
						getResults().add(new CrawlerResult(uri, linkPage, links, linksErrors, loadResult.exc));
						
						if(loadResult.exc != null)
						{
//							System.out.println("ADDING AN ERROR " + uri + "("+loadResult.exc+")");
//							System.out.println("|ERRORS("+uri+")| = " + getErrors().size());
							getErrors().add(uri);
//							System.out.println("|ERRORS("+uri+")++| = " + getErrors().size());
						}
						else
						{
//							System.out.println("ADDING A SUCCESS " + uri + "("+loadResult.exc+")");
							getLoaded().add(uri);
						}
				} catch (InterruptedException e) {
					System.out.println("=========== INTERROTTO ===========");
					// e.printStackTrace();
					future.cancel(true);
					System.out.println("DOWNLOAD INTERRUPTED? " + future.isCancelled());
					
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				});
//			}
		}
	}

	synchronized private URI next() {
		URI uri = getToLoad().iterator().next();
		getToLoad().remove(uri);
		return uri;
	}
	synchronized public void add(URI uriLink) {

		if(getErrors().contains(uriLink)) return;
		if(getLoaded().contains(uriLink)) return;

		getToLoad().add(uriLink);
		
		synchronized (lock) {lock.notify();}
	}

	public synchronized Set<URI> getLoaded() {
		return loaded;
	}

	public synchronized Set<URI> getToLoad() {
		return toLoad;
	}

	public synchronized Set<URI> getErrors() {
		return errors;
	}

	public synchronized List<CrawlerResult> getResults() {
		return results;
	}
}
