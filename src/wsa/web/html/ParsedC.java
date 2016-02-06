package wsa.web.html;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;

/**
 * Impementazione dell'interfaccia Parsed
 */
public class ParsedC implements Parsed{
	private Tree tree;
	private Map<String, List<Node>> map = new HashMap<>();
	
	public ParsedC( Document document ) {
		tree = translate(document.getDefaultRootElement());
		this.visit( (n) -> {
			String tag = n.tag;
			if( map.get(tag) == null ) {
				List<Node> nodeList = new ArrayList<>();
				map.put(tag, nodeList);
			}
			map.get(tag).add(n);
		});
	}
	
	/**
	 * Converte gli elementi estratti nel formato Tree implementato
	 * @param il nodo esaminato
	 * @return l'albero Tree
	 */
	private static Tree translate ( Element element ) {
		Parsed.Node treeRoot = extractNode(element);
		if(treeRoot == null) return null;
		
		Tree tree = new Tree(treeRoot);

		for(int i=0; i<element.getElementCount(); i++)
		{
			Element childNode = element.getElement(i);
			Tree childTree = translate(childNode);
			if(childTree != null) tree.addChild(childTree);
		}
		
		 return tree;
	}
	/**
	 * Estrae informazioni da un nodo, ricava la mappa degli attributi, tag e content
	 * @param un nodo da esaminare
	 * @return un nuovo nodo con le informazioni estratte
	 */
	private static Node extractNode(Element element) {
		if(element.getName().equals(HTML.Tag.COMMENT.toString())) return null;
//		if(element.getName().equals(HTML.Tag.IMPLIED.toString())) return null;
		
		AttributeSet nodeMap = element.getAttributes();
		Map<String, String> map = new HashMap<>();
		
		Enumeration<?> attributeNames = nodeMap.getAttributeNames();
		while(attributeNames.hasMoreElements())
		{
			Object attributeName = attributeNames.nextElement();
			Object value = nodeMap.getAttribute(attributeName);
			if(attributeName.toString().equals(HTML.Attribute.NAME.toString())) continue;
//			System.out.println("\t\t" + attributeName.toString() + "\t" + value.toString());
			map.put(attributeName.toString(), value.toString());
		}
		
		String tag = element.getName();
		if(element.getName().equals(HTML.Tag.CONTENT.toString()))
		{
			tag = null;
			if(map.containsKey("a"))
			{
				tag = "a";
				String link = map.get("a");
				int startIndex = link.indexOf("href=");
				if(startIndex >= 0)
				{
					link = link.substring(startIndex + "href=".length());
					link = link.trim();
					if(link.contains(" ")) link = link.split(" ")[0];
					map.put("href", link);
					map.remove("a");
				}
			}
		}
		
		String content = null;
		if(element.isLeaf())
		{
			try
			{
				content = element.getDocument().getText(element.getStartOffset(), (element.getEndOffset()-element.getStartOffset()));
			}
			catch (BadLocationException e)
			{
				e.printStackTrace();
			}
		}
//		System.out.println(tag + "\t" + map + "\t" + content);
		return new Parsed.Node(tag, map, content);
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
	/**
	 * 
	 */
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
