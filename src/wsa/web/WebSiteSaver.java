package wsa.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebSiteSaver {
	public static void save(SiteCrawler siteCrawler, File selectedDir, URI dominio, List<URI> seeds){
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( new File(selectedDir, dominio.toString()+".txt") ) );
			
			String newLine = System.lineSeparator();
			writer.write(dominio.toString() + newLine);
			writer.write(seeds.size() + newLine);
			for( URI u : seeds)
				writer.write(u + newLine);
			writer.write(siteCrawler.getToLoad().size() + newLine);
			for(URI u : siteCrawler.getToLoad())
				writer.write(u + newLine);
			Set<URI> scaricati = new HashSet<>();
			scaricati.addAll(siteCrawler.getLoaded());
			scaricati.addAll(siteCrawler.getErrors());
			// TODO aggiungo size di scaricati??
			writer.write(scaricati.size() + newLine);			
			for(URI u : scaricati){
				CrawlerResult r = siteCrawler.get(u);
				writer.write(u.toString() + newLine);
				writer.write(r.linkPage + newLine);
				writer.write(r.links.size() + newLine);
				for(URI l : r.links)
					writer.write(l + newLine);
				writer.write(r.errRawLinks.size() + newLine);
				for(String s : r.errRawLinks)
					writer.write(s + newLine);
				
				if( r.exc != null) {
					writer.write(r.exc.toString() + newLine);
					writer.write(r.exc.getMessage() + newLine);
				}
				else {
					writer.write(newLine);
				}
			}			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
