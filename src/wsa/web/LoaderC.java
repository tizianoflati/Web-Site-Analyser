package wsa.web;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import wsa.web.html.Parsed;
import wsa.web.html.Parsed.Node;
import wsa.web.html.Tree;

public class LoaderC implements Loader{
	private WebEngine webEngine = new WebEngine();
	private Worker.State state= null;
	private AtomicBoolean atomicBoolean = new AtomicBoolean();
	private Tree tree;

	public LoaderC() {
		atomicBoolean.set(false);
		webEngine.getLoadWorker().stateProperty().addListener( (observable, oldValue, newValue) -> {
			if( newValue == Worker.State.SUCCEEDED) {
				atomicBoolean.set(true);
			}
		});
	}
	
	@Override
	public LoadResult load(URL url) {
		Platform.runLater( () -> webEngine.load(url.toString()) );
		while( atomicBoolean.get() == false ) {
			try {
				Thread.currentThread().sleep(10);
				} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Document document = webEngine.getDocument();
		//NodeList nodes = document.getElementsByTagName("*");
		
		translate(document);
		return null;
	}
	
	public void translate(Document document){
		NamedNodeMap nodeMap = document.getAttributes();
		Map<String, String> map = new HashMap<>();
		for(int i = 0; i < nodeMap.getLength(); i++) {
			map.put(nodeMap.item(i).getNodeName(), nodeMap.item(i).getNodeValue());
		}
		Parsed.Node rootNode;
		//tree = new Tree(rootNode);
		//translate()
	}
	


	@Override
	public Exception check(URL url) {
		
		return null;
	}

}
