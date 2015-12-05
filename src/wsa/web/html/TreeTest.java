package wsa.web.html;

import java.util.HashMap;
import java.util.Map;

import wsa.web.html.Parsed.Node;

public class TreeTest {
	public static void main(String[] args) {
		
		Map<String, String> attributeMap = new HashMap<String, String>();
		attributeMap.put("href", "http://www.google.it");
		attributeMap.put("height", "80");
		Tree a = new Tree(new Node("a", attributeMap, null));
		Tree span = new Tree(new Node("span", null, null));
		Tree body = new Tree(new Node("body", null, null));
		body.addChild(a);
		body.addChild(span);
		Tree html = new Tree(new Node("html", null, null));
		html.addChild(body);
	}
}
