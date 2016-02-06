package wsa.web;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncLoaderC implements AsyncLoader{
	private ExecutorService executorService;
	private Loader loader;

	public AsyncLoaderC(Loader loader) {
		this.loader = loader;
		
		executorService = Executors.newFixedThreadPool(30);
	}
	
	@Override
	public Future<LoadResult> submit(URL url) {
		return executorService.submit( () -> {
			return loader.load(url);
		});
	}

	@Override
	public void shutdown() {
		executorService.shutdownNow();
	}

	@Override
	public boolean isShutdown() {
		return executorService.isShutdown();
	}

}
