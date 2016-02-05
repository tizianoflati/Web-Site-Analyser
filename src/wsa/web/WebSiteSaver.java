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
	public static void save(SiteCrawler siteCrawler, File selectedDir, URI dominio){
		System.out.println("SAVING " + dominio + " ON DISK: " + selectedDir);
		
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( new File(selectedDir, dominio.getHost().toString()+".wsa") ) );
			
			String newLine = System.lineSeparator();
			writer.write(dominio.toString() + newLine);
			writer.write(siteCrawler.getToLoad().size() + newLine);
System.out.println("------ Writing getToLoad... + " + siteCrawler.getToLoad());
			for(URI u : siteCrawler.getToLoad())
				writer.write(u + newLine);
			Set<URI> scaricati = new HashSet<>();
			scaricati.addAll(siteCrawler.getLoaded());
			scaricati.addAll(siteCrawler.getErrors());
			// TODO aggiungo size di scaricati??
			writer.write(scaricati.size() + newLine);
System.out.println("------ Writing scaricati... + " + scaricati);
			for(URI u : scaricati){
				CrawlerResult r = siteCrawler.get(u);
				writer.write(u.toString() + newLine);
				writer.write(r.linkPage + newLine);
				
				if(r.links == null) writer.write(0 + newLine);
				else
				{
					writer.write(r.links.size() + newLine);
					for(URI l : r.links)
						writer.write(l + newLine);
				}
				
				if(r.errRawLinks == null) writer.write(0 + newLine);
				else
				{
					writer.write(r.errRawLinks.size() + newLine);
					for(String s : r.errRawLinks)
						writer.write(s + newLine);
				}
				
				if( r.exc != null) {
					writer.write(r.exc.toString() + newLine);
				}
				else {
					writer.write(newLine);
				}
			}			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
