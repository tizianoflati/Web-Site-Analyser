package wsa.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Test{
	
	public static void main(String[] args) throws MalformedURLException, InterruptedException, ExecutionException {
		
		System.out.println("Scaricando: ");

		try {
			AsyncLoaderC loader = new AsyncLoaderC();
			String url = "http://www.repubblica.it/";
//			String url = "https://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html";
//			String url = "http://www.repubblica.it/esteri/2015/12/08/news/migranti_naufragio_al_largo_della_turchia_6_bambini_morti-129024370/?ref=HRER1-1";
			Future<LoadResult> result = loader.submit(new URL(url));
    		for (String link : result.get().parsed.getLinks()) {
    			System.out.println(link);
    		}
    		
    		loader.shutdown();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
}
