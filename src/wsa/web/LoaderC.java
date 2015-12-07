package wsa.web;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import wsa.web.html.Parsed;
import wsa.web.html.Tree;
import wsa.web.html.Parsed.Node;
import wsa.web.html.ParsedC;

public class LoaderC implements Loader {
	private WebEngine webEngine = new WebEngine();
	private boolean done = false;
	private Exception exception = null;
	

	public LoaderC() {
		webEngine.getLoadWorker().stateProperty().addListener( (observable, oldValue, newValue) -> {
			if( newValue == Worker.State.SUCCEEDED) {
				done = true;
			}
			else if( newValue == Worker.State.FAILED ) {
				exception = new Exception( webEngine.getLoadWorker().getException() );
			}
		});
	}
	
	@Override
	public LoadResult load(URL url) {
		
		Platform.runLater( () -> webEngine.load(url.toString()) );
		while( done == false ) {
			try {
				Thread.currentThread().sleep(10);
				} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Document document = webEngine.getDocument();
		
		return new LoadResult(url, new ParsedC(document), exception);
	}
	

	@Override
	public Exception check(URL url) {
		return null;
	}

}
