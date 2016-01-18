package wsa.web;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class CrawlerC implements Crawler {

	public enum CrawlerState
	{
		RUNNABLE,
		RUNNING,
		SUSPENDED,
		CANCELLED
	}

	private CrawlerThread crawlerThread;
	
	public CrawlerC(AsyncLoader loader)
	{
		this(loader, null, null, null, null);
	}
	public CrawlerC(AsyncLoader loader,
			Collection<URI> loaded,
            Collection<URI> toLoad,
            Collection<URI> errs,
            Predicate<URI> pageLink)
	{
		this.crawlerThread = new CrawlerThread(loader, loaded, toLoad, errs, pageLink);
	}
	
	@Override
	public void add(URI uri){
		if(this.crawlerThread.getCrawlerState() == CrawlerState.CANCELLED) throw new IllegalStateException();
		
		this.crawlerThread.add(uri);
	}

	@Override
	public void start() {
		if(this.crawlerThread.getCrawlerState() == CrawlerState.CANCELLED) throw new IllegalStateException();
		if(this.crawlerThread.getCrawlerState() == CrawlerState.RUNNING) return;
		
		// If it is the first time we start the crawler
		if(this.crawlerThread.getCrawlerState() == CrawlerState.RUNNABLE) crawlerThread.start();
		else
		{
			this.crawlerThread.setCrawlerState(CrawlerState.RUNNING);
		}
	}

	@Override
	public void suspend() {
		if(this.crawlerThread.getCrawlerState() == CrawlerState.CANCELLED) throw new IllegalStateException();
		
		this.crawlerThread.setCrawlerState(CrawlerState.SUSPENDED);
	}

	@Override
	public void cancel() {
		System.out.println("CANCELLING THE CRAWLER");
		this.crawlerThread.setCrawlerState(CrawlerState.CANCELLED);
	}

	@Override
	public Optional<CrawlerResult> get() {
		if(this.crawlerThread.getCrawlerState() == CrawlerState.CANCELLED) throw new IllegalStateException();
		
		if(this.crawlerThread.getCrawlerState() != CrawlerState.RUNNING) return Optional.empty();
		
		if(this.crawlerThread.getResults().isEmpty()) return Optional.of(new CrawlerResult(null, false, null, null, null)); 
		return Optional.of(this.crawlerThread.getResults().remove(0));
	}
	

	@Override
	public Set<URI> getLoaded() {
		if(this.crawlerThread.getCrawlerState() == CrawlerState.CANCELLED) throw new IllegalStateException();
		
		return this.crawlerThread.getLoaded();
	}

	@Override
	public Set<URI> getToLoad() {
		if(this.crawlerThread.getCrawlerState() == CrawlerState.CANCELLED) throw new IllegalStateException();
		
		return this.crawlerThread.getToLoad();
	}

	@Override
	public Set<URI> getErrors() {
		if(this.crawlerThread.getCrawlerState() == CrawlerState.CANCELLED) throw new IllegalStateException();
		
		return this.crawlerThread.getErrors();
	}

	@Override
	public boolean isRunning() {
		return this.crawlerThread.getCrawlerState() == CrawlerState.RUNNING;
	}

	@Override
	public boolean isCancelled() {
		return this.crawlerThread.getCrawlerState() == CrawlerState.CANCELLED;
	}
}
