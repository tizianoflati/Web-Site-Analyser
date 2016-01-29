package wsa.web;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class AdvancedSiteStatistics {
	
	private Map<URI, List<URI>> graph = new HashMap<>();
	
	public AdvancedSiteStatistics(Collection<CrawlerResult> results) {
		
		for(CrawlerResult result : results)
			if(result.exc != null && result.links != null)
				graph.put(result.uri, result.links);
	}
	
	public int getDistance(URI r1, URI r2)
	{
		Map<URI, Integer> distances = new HashMap<URI, Integer>();
		
		Queue<URI> queue = new LinkedList<URI>();
		queue.add(r1);
		distances.put(r1, 0);
		
		while(!queue.isEmpty())
		{
			URI u = queue.remove();
			Integer dist = distances.get(u);
			
			List<URI> neighbors = graph.get(u);
			for(URI v : neighbors)
			{
				if(distances.get(v) == null)
				{
					distances.put(v, dist + 1);
					queue.add(v);
				}
			}
		}
		
		return distances.get(r2);
	}
	
	public int getDiameter()
	{
		Map<URI, Map<URI, Integer>> distances = new HashMap<URI, Map<URI, Integer>>();
		for(URI u : graph.keySet())
		{
			Map<URI, Integer> d = distances.get(u);
			if(d == null){d = new HashMap<URI, Integer>(); distances.put(u, d);}
			
			for(URI v : graph.keySet())
			{
				if(u == v) d.put(v, 0);
				else if(graph.get(u).contains(v)) d.put(v, 1);
				else d.put(v, Integer.MAX_VALUE);
			}
		}
		
		for(URI z : graph.keySet())
			for(URI u : graph.keySet())
				for(URI v : graph.keySet())
				{
					Integer uv = distances.get(u).get(v);
					Integer uz = distances.get(u).get(z);
					Integer zv = distances.get(z).get(v);
					
					if(uv > uz + zv)
						distances.get(u).put(v, uz + zv);
				}
		
		Integer max = 0;
		for(URI u : distances.keySet())
			for(URI v : distances.keySet())
				max = Math.max(max, distances.get(u).get(v));
		
		return max;
	}
}
