package com.aitangba.ubb;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author fenghaifeng
 * @date 2016年12月22日
 *
 */
public class UbbUtils {

	private static final String MODE = "\\[{0}\\](.*?)\\[/{0}\\]";
	
	private static List<Element> getDefaultElements () {
		List<Element> list = new ArrayList<>();
		list.add(new CommonElement("b", "b"));
		list.add(new ColorElement());
		list.add(new CommonElement("strong", "strong"));
		list.add(new CommonElement("em", "em"));
		list.add(new CommonElement("up", "up"));
		list.add(new ImageElement("img", "img"));
		list.add(new ImageElement("u", "u"));
		return list;
	}
	
	public static String ubb2Html(String str) {
		return toHtml(str, getDefaultElements());
	}
	
	public static String ubb2Html(String str, List<Element> elements) {
		List<Element> list = getDefaultElements();
		if(elements != null && elements.size() > 0) {
			list.addAll(elements);
		}
		return toHtml(str, list);
	}
	
	/**
	 * 禁止ubb标签嵌套使用
	 * @param str
	 * @param elements
	 * @return
	 */
	private static String toHtml(String str, List<Element> elements) {
		if(str == null) return null;

		String input = str.replace("\n", "<br/>");
		for(Element element : elements) {
			String key = element.originLabel;
			String regex = MessageFormat.format(MODE, key);
			
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  //忽略大小写
			Matcher matcher = pattern.matcher(input); 
			
			StringBuffer sb = new StringBuffer(); 
		    while(matcher.find()) {
		    	String content = matcher.group(1).trim();
		    	String rightStr = element.getReplacement(content);
		    	matcher.appendReplacement(sb, rightStr); 
		    } 
		    matcher.appendTail(sb);
		    input = sb.toString(); 
		}
		
		return input;
	}
	
	public abstract static class Element {
		
		public String originLabel;
		public String replaceLabel;
		
		public Element(String originLabel, String replaceLabel) {
			super();
			this.originLabel = originLabel;
			this.replaceLabel = replaceLabel;
		}

		public abstract String getReplacement(String content);
		
	}
	
    public static class CommonElement extends Element {
		
		public CommonElement(String originLabel, String replaceLabel) {
			super(originLabel, replaceLabel);
		}

		@Override
		public String getReplacement(String content) {
			return MessageFormat.format("<{0}>" + content + "</{0}>", replaceLabel);
		}
		
	}
    
   public static class ColorElement extends Element {
		
		public ColorElement() {
			super("color", "font");
		}

		@Override
		public String getReplacement(String content) {
			return MessageFormat.format("<{0} color=\"#FFFF66\">" + content + "</{0}>", replaceLabel);
		}
		
	}
	
    public static class ImageElement extends Element {
		
		public ImageElement(String originLabel, String replaceLabel) {
			super(originLabel, replaceLabel);
		}

		@Override
		public String getReplacement(String content) {
			return MessageFormat.format("<{0} src=\"" +content + "\"/>", replaceLabel);
		}
		
	}
}
