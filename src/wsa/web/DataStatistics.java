package wsa.web;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataStatistics {
	private SiteCrawler siteCrawler;

	
	public DataStatistics( SiteCrawler siteCrawler ) {
		this.siteCrawler = siteCrawler;
	}
	
	public int getVisitedURI() {
		return siteCrawler.getLoaded().size();
	}
	
	public int getInnerDomURI(URI dom) {
		int n = 0;
		
		for( URI u : siteCrawler.getLoaded() ) {
			if(SiteCrawler.checkSeed(dom, u)) n++;
		}
		
		return n;
	}
	
	public int uriErrsNum() {
		return siteCrawler.getErrors().size();
	}
	
	public int getMaxOutgoingLinks() {
		Set<URI> loaded = siteCrawler.getLoaded();
		int max = 0;
		
		for(URI u : loaded) {
			CrawlerResult crawlerResult = siteCrawler.get(u);
			
			if(crawlerResult.links != null) {
				int n = crawlerResult.links.size();
				if( n > max) max = n;
			}			
		}		
		return max;
	}
	
	public int getMaxIncomingLinks() {
		Set<URI> loaded = siteCrawler.getLoaded();
		int n = 0;
		Map<URI, Integer> map = new HashMap<>();
		
		for(URI u : loaded) {
			CrawlerResult crawlerResult = siteCrawler.get(u);
			if(crawlerResult.links != null) {
				for(URI v : crawlerResult.links) {
					Integer temp = map.get(v);
					if(temp == null) temp = 0;
					temp = temp + 1;
					map.put(v, temp);	
				}
			}			
		}
		for( Integer i : map.values() )
			if( i > n ) n = i;
		
		return n;
	}
	
	public int getCrossSiteLinksNum( List<SiteCrawler> crawlers ) {		
		if( crawlers.size() <= 1) return 0;
		int n = 0;
		for(int i = 0; i < crawlers.size(); i++) {
			for( int j = 0; j < crawlers.size(); j++ ) {
				if( i == j) continue;
				SiteCrawler sc1 = crawlers.get(i);
				SiteCrawler sc2 = crawlers.get(j);
				Set<URI> loaded1 = sc1.getLoaded();
				Set<URI> loaded2 = sc2.getLoaded();
				for( URI u : loaded1 ) {
					CrawlerResult cr = sc1.get(u);
					for( URI neighbour : cr.links) {
						if( loaded2.contains(neighbour) ) {
							n++;
						}
					}
				}
			}
		}
		return n;
	}
}
