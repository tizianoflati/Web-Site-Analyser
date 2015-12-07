package wsa.web;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncLoaderC implements AsyncLoader{
	private ExecutorService executorService;

	@Override
	public Future<LoadResult> submit(URL url) {
		executorService = Executors.newSingleThreadExecutor();
		return executorService.submit( () -> { 
			return new LoaderC().load(url);
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
