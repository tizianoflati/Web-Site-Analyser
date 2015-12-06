package wsa.web.html;

import java.util.ArrayList;
import java.util.List;

import wsa.web.html.Parsed.Node;

public class Tree {
	private Node root;
	private List<Tree> children = new ArrayList<>();
	
	public Tree(Node root) {
		this.root = root;
	}
	
	public void addChild(Tree child){
		children.add(child);
	}
	
	public List<Tree> getChildren() {
		return children;
	}
	
	public Node getRoot() {
		return root;
	}
}
