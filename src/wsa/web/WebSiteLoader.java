package wsa.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

	// CrawlerResults ritornato?? invece di void?
	public static void load(List<CrawlerResult> crawlerResultsList, Set<URI> loaded, Set<URI> toLoad, Set<URI> errs, File dir, URI name) {
		File file = new File(dir, name.toString() + ".wsa" );
		BufferedReader bufferedReader;
		Set<URI> seeds = new HashSet<>();

		URI uriCrawler;
		boolean linkPage;
		List<URI> links = new ArrayList<>();
		List<String> errRawlinks = new ArrayList<>();
		Exception exc = null;

		try {
			bufferedReader = new BufferedReader( new FileReader(file));
			String dom = bufferedReader.readLine();


			int seedSize = Integer.parseInt(bufferedReader.readLine());
			for(int i = 0; i < seedSize; i++)
				seeds.add(new URI(bufferedReader.readLine()));					
			int toLoadSize = Integer.parseInt(bufferedReader.readLine());
			for(int i = 0; i < toLoadSize; i++)
				toLoad.add(new URI(bufferedReader.readLine()));
			int scaricatiSize = Integer.parseInt(bufferedReader.readLine());
			for(int i = 0; i < scaricatiSize; i++) {
				uriCrawler = new URI(bufferedReader.readLine());

				linkPage = Boolean.parseBoolean(bufferedReader.readLine());

				int linksSize = Integer.parseInt(bufferedReader.readLine());
				for(int j = 0; j < linksSize; j++){
					links.add(new URI(bufferedReader.readLine()));					
				}

				int errRawLinksSize = Integer.parseInt(bufferedReader.readLine());
				for(int j = 0; j < errRawLinksSize; j++) {
					errRawlinks.add(bufferedReader.readLine());
				}

				String exceptionName = bufferedReader.readLine();
				if( exceptionName.isEmpty() ) {
					loaded.add(uriCrawler);
				}
				else {
					errs.add(uriCrawler);
					String exceptionMessage = bufferedReader.readLine();
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
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



	}


}
