package wsa.web;

import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import wsa.web.CrawlerC.CrawlerState;

public class SiteCrawlerC implements SiteCrawler{
	
	private Crawler crawler;
	private ScheduledThreadPoolExecutor saver;
	private Runnable saverRunnable;
	private Map<URI, CrawlerResult> crawlerResultMap = new HashMap<URI, CrawlerResult>();
	
    static boolean checkDomain(URI dom) {
    	if( !dom.isAbsolute() ) return false;
    	if( !dom.getSchemeSpecificPart().startsWith("/") ) return false;
    	if( dom.getAuthority().equals(dom.getHost()) ) return true;
        return false;
    }
    
    static boolean checkSeed(URI dom, URI seed) {
    	if(dom.equals(seed) || !dom.relativize(seed).equals(seed) )
    		return true;
    	return false;
    }
	
    public SiteCrawlerC(Crawler crawler) {
    	this(crawler, null, null);
	}
    public SiteCrawlerC(Crawler crawler, Path dir) {
    	this(crawler, dir, null);
	}
	public SiteCrawlerC(Crawler crawler, Path dir, Map<URI, CrawlerResult> crawlerResultsMap) {
		this.crawler = crawler;
		
		if(dir != null) this.saver = new ScheduledThreadPoolExecutor(1);
		
		if(crawlerResultsMap != null) this.crawlerResultMap  = crawlerResultsMap;
	}

	@Override
	public void addSeed(URI uri) {
		crawler.add(uri);
	}

	@Override
	public void start() {
		if(isRunning()) return;
		
		crawler.start();
		if(saver != null) saver.scheduleAtFixedRate(saverRunnable, 0, 1, TimeUnit.MINUTES);
	}

	@Override
	public void suspend() {
		crawler.suspend();
		if(saver != null) saver.shutdownNow();
		
		// To force writing site state on disk
		if(saver != null) Executors.newFixedThreadPool(1).submit(saverRunnable);
	}

	@Override
	public void cancel() {
		crawler.cancel();
		
		if(saver != null) saver.shutdownNow();
	}

	@Override
	public Optional<CrawlerResult> get() {
		return crawler.get();
	}

	@Override
	public CrawlerResult get(URI uri) {
		if( !this.getLoaded().contains(uri) && !this.getErrors().contains(uri)) throw new IllegalArgumentException();		
		// TODO
		return null; 
	}

	@Override
	public Set<URI> getLoaded() {
		return crawler.getLoaded();
	}

	@Override
	public Set<URI> getToLoad() {
		return crawler.getToLoad();
	}

	@Override
	public Set<URI> getErrors() {
		return crawler.getErrors();
	}

	@Override
	public boolean isRunning() {
		return crawler.isRunning();
	}

	@Override
	public boolean isCancelled() {
		return crawler.isCancelled();
	}

}
