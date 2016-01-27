package wsa.web;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class Test{
	
	public static void main(String[] args) throws MalformedURLException, InterruptedException, ExecutionException
	{
//		try
//		{
			String seed = "http://www.repubblica.it/";
			String seed2 = "https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html";
			String seed3 = "http://www.repubblica.it/esteri/2015/12/08/news/migranti_naufragio_al_largo_della_turchia_6_bambini_morti-129024370/?ref=HRER1-1";
			
			CrawlerC crawler = new CrawlerC(WebFactoryWSA.getAsyncLoader(), null, null, null, (URI uri) -> {
				return uri.toString().equals(seed);
			});
			
//			crawler.add(new URI(seed));
//			crawler.add(new URI(seed2));
//			crawler.add(new URI(seed3));
			
			crawler.start();
			crawler.suspend();
			
			System.out.println("SLEEPING FOR 5 SECONDS");
			for(int i=0; i<60; i++)
			{
				Thread.sleep(1000);
//				if(System.currentTimeMillis()%2==0)
//					crawler.start();
//				
				System.out.println("TEST "+i+":");
				System.out.println("NUMERO DI THREAD ATTIVI: " + Thread.activeCount());
				System.out.println("CANCELLED? " + crawler.isCancelled());
				System.out.println("IS RUNNING? " + crawler.isRunning());
			}
			System.out.println("FINISHED SLEEPING");
			
//			crawler.cancel();
//		}
//		catch (URISyntaxException e)
//		{
//			e.printStackTrace();
//		}
	}
}
