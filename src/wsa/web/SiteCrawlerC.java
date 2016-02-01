package wsa.web;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SiteCrawlerC implements SiteCrawler{
	
	private URI dom;
	private Crawler crawler;
	private ScheduledThreadPoolExecutor saver;
	private Runnable saverRunnable;
	//private Map<URI, CrawlerResult> crawlerResultMap = new HashMap<URI, CrawlerResult>();
	private List<CrawlerResult> crawlerResultList = new ArrayList<>();
	private int times = 0;
	
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
	
    public SiteCrawlerC(Crawler crawler, URI dom) {
    	this(crawler, dom, null);
	}
    public SiteCrawlerC(Crawler crawler, URI dom, Path dir) {
    	this(crawler, dom, dir, null);
	}
	public SiteCrawlerC(Crawler crawler, URI dom, Path dir, List<CrawlerResult> crawlerResultsList) {
		this.crawler = crawler;
		this.dom = dom;
		
		if(dir != null)
		{
			this.saver = new ScheduledThreadPoolExecutor(1);
			this.saverRunnable = () -> {
				WebSiteSaver.save(this, dir.toFile(), dom);
			};
		}
		
		if(crawlerResultsList != null) this.crawlerResultList  = crawlerResultsList;
	}

	@Override
	public void addSeed(URI uri) {
		crawler.add(uri);
	}

	@Override
	public void start() {
		if(isRunning()) return;
		
		crawler.start();
		if(saver != null)
		{
			System.out.println("SCHEDULING SAVER");
			saver.scheduleAtFixedRate(saverRunnable, 0, 10, TimeUnit.SECONDS);
		}
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
		int crawlerResultSize = crawlerResultList.size();
		if(times < crawlerResultSize){
			CrawlerResult cr = crawlerResultList.get(times);
			times++;
			return Optional.of(cr);
		}
		Optional<CrawlerResult> optional = crawler.get();
		if( optional.isPresent() && optional.get().uri != null) {
			crawlerResultList.add(optional.get());
			times++;
		}
		return optional;
	}

	@Override
	public CrawlerResult get(URI uri) {
		if( !this.getLoaded().contains(uri) && !this.getErrors().contains(uri)) throw new IllegalArgumentException();		
		
		for(CrawlerResult cr : crawlerResultList)
			if( cr.uri.equals(uri))
				return cr;

		CrawlerResult last = null;
		do {
			Optional<CrawlerResult> optional = crawler.get();
			last = optional.get();
			
			crawlerResultList.add(last);
		} while( !last.uri.equals(uri) );
		
		return last; 
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
