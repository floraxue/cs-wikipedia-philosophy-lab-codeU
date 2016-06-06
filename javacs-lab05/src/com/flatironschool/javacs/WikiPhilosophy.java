package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

import javax.lang.model.util.ElementScanner6;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String targetUrl = "https://en.wikipedia.org/wiki/Philosophy";

		Deque<Elements> stack = new ArrayDeque<Elements>();
		List<String> results = new ArrayList<String>();

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		Elements root = wf.fetchWikipedia(url);

		stack.push(root);
		results.add(url);

		while (!stack.isEmpty()) {
			Elements paragraphs = stack.pop();
			url = processDOM(paragraphs);
			if (url.length() == 0) {
                System.out.println(results.toString());
				throw new IOException("This page has no link");
			} else if (url.equals(targetUrl)) {
				System.out.println("Reached: " + url);
                System.out.println(results.toString());
				return;
			} else if (results.contains(url)) {
                System.out.println(results.toString());
                throw new IOException("A loop in links");
            }

			Elements firstChildLink = wf.fetchWikipedia(url);
			stack.push(firstChildLink);
			results.add(url);
		}
        System.out.println(results.toString());
		throw new IOException("Stack is empty");

		


        // the following throws an exception so the test fails
        // until you update the code
//        String msg = "Complete this lab by adding your code and removing this statement.";
//        throw new UnsupportedOperationException(msg);
	}

	private static String processDOM(Elements paragraphs) {
		for (Element para : paragraphs) {
			Iterable<Node> iter = new WikiNodeIterable(para);
            int parenthesisBalance = 0;
			for (Node node: iter) {

                if (node instanceof TextNode) {
                    parenthesisBalance = checkParen(parenthesisBalance, (TextNode) node);
//                    if (((TextNode) node).text().charAt('(') != -1) {
//
//                    }
                }
				if (node instanceof Element && node.hasAttr("href")) {
                    if (!((Element) node.parent()).tagName().equals("i") &&
                            !((Element) node.parent()).tagName().equals("em") &&
                            !((Element) node.parent()).tagName().equals("sup") &&
                            parenthesisBalance == 0) {
                        System.out.println(node.attr("abs:href"));
                        return node.attr("abs:href");
                    }
				}
			}
		}
		return "";
	}

    private static int checkParen(int currBalance, TextNode node) {
        String text = node.text();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '(': {
                    currBalance++;
                    break;
                }
                case ')': {
                    currBalance--;
                    break;
                }
            }
        }
        return currBalance;
    }
}
