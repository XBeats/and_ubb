package com.aitangba.ubb.utils;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fhf11991 on 2016/12/22.
 */

public class UbbUtils {

	private static final String MODE = "\\[{0}\\](.*?)\\[/{0}\\]";
	
	private static List<Element> getDefaultElements () {
		List<Element> list = new ArrayList<>();
		list.add(new CommonElement("b", "b"));
		list.add(new CommonElement("strong", "strong"));
		list.add(new CommonElement("em", "em"));
		list.add(new CommonElement("up", "sup"));
		list.add(new CommonElement("ub", "sub"));
		list.add(new CommonElement("u", "u"));
		list.add(new ColorElement());
		list.add(new ImageElement("img", "image"));
		return list;
	}
	
	public static Spanned ubb2Html(TextView textView, String str) {
		CustomTagHandler customTagHandler = new CustomTagHandler();
		customTagHandler.setTextView(textView);

		String html = toHtml(str, getDefaultElements(), customTagHandler);
		Log.d("UbbUtils", "html = " + html);

		return Html.fromHtml(html, null, customTagHandler);
	}
	
	/**
	 * 禁止ubb标签嵌套使用
	 * @param str
	 * @param elements
	 * @return
	 */
	private static String toHtml(String str, List<Element> elements, CustomTagHandler customTagHandler) {
		if(str == null) return null;

		String input = str.replace("\n", "<br/>");
		HashMap<String, String> map = customTagHandler == null ?
				new HashMap<String, String>() : customTagHandler.getStringHashMap();

		for(Element element : elements) {
			String key = element.originLabel;
			String regex = MessageFormat.format(MODE, key);
			
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  //忽略大小写
			Matcher matcher = pattern.matcher(input); 
			
			StringBuffer sb = new StringBuffer(); 
		    while(matcher.find()) {
		    	String content = matcher.group(1).trim();
		    	String rightStr = element.getReplacement(map, content);
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

		public abstract String getReplacement(HashMap<String, String> stringHashMap, String content);

	}
	
    public static class CommonElement extends Element {
		
		public CommonElement(String originLabel, String replaceLabel) {
			super(originLabel, replaceLabel);
		}

		@Override
		public String getReplacement(HashMap<String, String> stringHashMap, String content) {
			return MessageFormat.format("<{0}>" + content + "</{0}>", replaceLabel);
		}
		
	}
    
   public static class ColorElement extends Element {
		
		public ColorElement() {
			super("color", "font");
		}

		@Override
		public String getReplacement(HashMap<String, String> stringHashMap, String content) {
			return MessageFormat.format("<{0} color=\"#FFFF66\">" + content + "</{0}>", replaceLabel);
		}
		
	}
	
    public static class ImageElement extends Element {

		public ImageElement(String originLabel, String replaceLabel) {
			super(originLabel, replaceLabel);
		}

		@Override
		public String getReplacement(HashMap<String, String> stringHashMap, String content) {
			String tag = replaceLabel + stringHashMap.size();
			stringHashMap.put(tag, content);
			return MessageFormat.format("<{0} />", tag);
		}
	}
}

