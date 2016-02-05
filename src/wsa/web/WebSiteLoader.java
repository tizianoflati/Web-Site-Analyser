package wsa.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebSiteLoader {

	public static URI load(List<CrawlerResult> crawlerResultsList, Set<URI> loaded, Set<URI> toLoad, Set<URI> errs, File dir) throws IOException {
		File[] files = dir.listFiles(
    			new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".wsa");
			}
		});
		if(files.length == 0) throw new IOException();
		File file = files[0];
		
		URI dom = null;
		BufferedReader bufferedReader = null;

		try {
			bufferedReader = new BufferedReader( new FileReader(file));
			dom = new URI(bufferedReader.readLine());

			int toLoadSize = Integer.parseInt(bufferedReader.readLine());
			for(int i = 0; i < toLoadSize; i++)
				toLoad.add(new URI(bufferedReader.readLine()));
			int scaricatiSize = Integer.parseInt(bufferedReader.readLine());
			for(int i = 0; i < scaricatiSize; i++) {
				
				List<URI> links = new ArrayList<>();
				List<String> errRawlinks = new ArrayList<>();
				
				URI uriCrawler = new URI(bufferedReader.readLine());

				boolean linkPage = Boolean.parseBoolean(bufferedReader.readLine());

				int linksSize = Integer.parseInt(bufferedReader.readLine());
				for(int j = 0; j < linksSize; j++){
					links.add(new URI(bufferedReader.readLine()));					
				}

				int errRawLinksSize = Integer.parseInt(bufferedReader.readLine());
				for(int j = 0; j < errRawLinksSize; j++) {
					errRawlinks.add(bufferedReader.readLine());
				}

				Exception exc = null;
				String exceptionLine = bufferedReader.readLine();
				if( exceptionLine.isEmpty() ) {
					loaded.add(uriCrawler);
				}
				else {
					errs.add(uriCrawler);
					
					int colonIndex = exceptionLine.indexOf(':');
					String exceptionName = colonIndex != -1 ? exceptionLine.substring(0, colonIndex) : exceptionLine;
					String exceptionMessage = colonIndex != -1 ? exceptionLine.substring(colonIndex+2) : "";
					
					Constructor<?> constructor = Class.forName(exceptionName).getConstructor(String.class);
					exc = (Exception)(constructor.newInstance(exceptionMessage));
				}
				CrawlerResult crawlerResult = new CrawlerResult(uriCrawler, linkPage, links, errRawlinks, exc);
				crawlerResultsList.add(crawlerResult);
			}

			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new IOException();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			throw new IOException();
		}
		finally {
			if(bufferedReader != null)
				bufferedReader.close();
		}
		
		return dom;
	}
	
	public static void main(String[] args) throws URISyntaxException, IOException {
		List<CrawlerResult> crawlerResultsList = new ArrayList<>();
		Set<URI> loaded = new HashSet<>();
		Set<URI> toLoad = new HashSet<>();
		Set<URI> errs = new HashSet<>();
		File dir = new File("C:\\Users\\Pan\\Desktop\\test gui wsa");
		
		WebSiteLoader.load(crawlerResultsList, loaded, toLoad, errs, dir);
		
		System.out.println(crawlerResultsList.size());
		System.out.println(loaded.size() + " " + loaded);
		System.out.println(toLoad.size() + " " + toLoad);
		System.out.println(errs.size() + " " + errs);
	}


}
