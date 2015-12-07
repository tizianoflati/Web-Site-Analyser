package wsa.web.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class ParsedC implements Parsed{
	private Tree tree;
	private Map<String, List<Node>> map = new HashMap<>();
	
	public ParsedC( Document document ) {
		tree = translate(document);
		this.visit( (n) -> {
			String tag = n.tag;
			if( map.get(tag) == null ) {
				List<Node> nodeList = new ArrayList<>();
				map.put(tag, nodeList);
			}
			map.get(tag).add(n);
		});
	}
	
	private static Tree translate ( org.w3c.dom.Node node ) {
		Parsed.Node treeRoot = extractNode(node);
		Tree tree = new Tree(treeRoot);
		
		NodeList list = node.getChildNodes();
		
		 for (int i=0; i<list.getLength(); i++) {
			 org.w3c.dom.Node childNode = list.item(i);
			 Tree childTree = translate(childNode);
			 tree.addChild(childTree);
		 }	 
		 return tree;
	}
	
	// quando non uso nessun campo della classe meglio fare metodi statici
	// meglio ancora se creare una classe di utilità con all'interno
	// i metodi statici
	private static Node extractNode(org.w3c.dom.Node node) {
		NamedNodeMap nodeMap = node.getAttributes();
		Map<String, String> map = new HashMap<>();
		for(int i = 0; i < nodeMap.getLength(); i++) {
			map.put(nodeMap.item(i).getNodeName(), nodeMap.item(i).getNodeValue());
		}
		String tag = node.getNodeName();
		String content = node.getTextContent();
		Node treeNode = new Parsed.Node(tag, map, content);
		return treeNode;
	}
	
	/**
	public ParsedC(Tree tree) {
		this.tree = tree;
		this.visit( (n) -> {
			String tag = n.tag;
			if( map.get(tag) == null ) {
				List<Node> nodeList = new ArrayList<>();
				map.put(tag, nodeList);
			}
			map.get(tag).add(n);
		});
	}
	**/
	
	@Override
	public void visit(Consumer<Node> visitor) {
		visit(visitor, tree);
	}
	
	private void visit(Consumer<Node> visitor, Tree tree) {
		visitor.accept(tree.getRoot());
		for(Tree child : tree.getChildren())
			visit(visitor, child);
	}

	@Override
	public List<String> getLinks() {
		if( !map.containsKey("a")) 
			return new ArrayList<String>();
		
		List<String> linksList = new ArrayList<>();
		for( Node node : map.get("a") ) {
			if( node.attr.containsKey("href"))
				linksList.add(node.attr.get("href"));
		}
		return linksList;
	}

	@Override
	public List<Node> getByTag(String tag) {
		return ( map.containsKey(tag)? map.get(tag) : new ArrayList<>());
	}

}
