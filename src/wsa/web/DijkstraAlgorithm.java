package wsa.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DijkstraAlgorithm {
	
	private List<CrawlerNode> nodes = new ArrayList<CrawlerNode>();
	
	public DijkstraAlgorithm(Collection<CrawlerResult> results) {
		for(CrawlerResult result : results)
			nodes.add(new CrawlerNode(result));
	}
	public int getDistance(CrawlerResult r1, CrawlerResult r2)
	{
		int distance = Integer.MAX_VALUE;
		
		Queue<CrawlerResult> queue = new LinkedList<CrawlerResult>();
		queue.add(r1);
		
		while(!queue.isEmpty())
		{
			CrawlerResult r = queue.remove();
			if(r.exc != null) continue;
		}
		
		return distance;
	}
	
	class CrawlerNode
	{
		public CrawlerNode(CrawlerResult r) {
			
		}
	}
}
