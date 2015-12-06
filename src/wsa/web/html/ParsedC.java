package wsa.web.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ParsedC implements Parsed{
	private Tree tree;
	private Map<String, List<Node>> map = new HashMap<>();
	
	
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
		return ( map.containsKey(tag)? new ArrayList<>() : map.get(tag) );
	}

}
