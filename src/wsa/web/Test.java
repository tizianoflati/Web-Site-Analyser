package wsa.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.util.concurrent.ExecutionException;

public class Test{
	
	public static void main(String[] args) throws MalformedURLException, InterruptedException, ExecutionException
	{
		try
		{
			String seed = "http://www.repubblica.it";
			String seed2 = "https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html";
			String seed3 = "http://www.repubblica.it/esteri/2015/12/08/news/migranti_naufragio_al_largo_della_turchia_6_bambini_morti-129024370/?ref=HRER1-1";
			
			SiteCrawler crawler = WebFactoryWSA.getSiteCrawler(new URI("http://www.repubblica.it"), FileSystems.getDefault().getPath("C:\\Users\\Pan\\Desktop\\test gui wsa"));
			
//			crawler.addSeed(new URI(seed));
//			crawler.addSeed(new URI(seed2));
			crawler.addSeed(new URI(seed3));
			
			crawler.start();
			
			for(int i=0; i<60; i++)
			{
				Thread.sleep(10000);
//				if(System.currentTimeMillis()%2==0)
//					crawler.start();
//				
				System.out.println("=================================================================");
				System.out.println("=================================================================");
				System.out.println("=================================================================");
				System.out.println("TEST "+i+":");
				System.out.println("NUMERO DI THREAD ATTIVI: " + Thread.activeCount());
				System.out.println("CANCELLED? " + crawler.isCancelled());
				System.out.println("IS RUNNING? " + crawler.isRunning());
				System.out.println("|TO LOAD| = " + crawler.getToLoad().size());
				System.out.println("|LOADED| = " + crawler.getLoaded().size());
				System.out.println("|ERRORS| = " + crawler.getErrors().size());
				System.out.println("=================================================================");
				System.out.println("=================================================================");
				System.out.println("=================================================================");
			}
			
//			crawler.cancel();
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
