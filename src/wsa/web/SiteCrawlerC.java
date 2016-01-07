package wsa.web;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

public class SiteCrawlerC implements SiteCrawler{
	private Crawler crawler;
	
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
		this.crawler = crawler; 
	}

	@Override
	public void addSeed(URI uri) {
		crawler.add(uri);
	}

	@Override
	public void start() {
		crawler.start();
	}

	@Override
	public void suspend() {
		crawler.suspend();
	}

	@Override
	public void cancel() {
		crawler.cancel();
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
