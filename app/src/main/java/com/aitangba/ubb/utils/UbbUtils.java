package com.aitangba.ubb.utils;

import android.text.Html;
import android.text.Spanned;
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

	private static final String MODE_UNUSUAL_UBB = "\\[{0}\\](.*?)\\[/{0}\\]";
	private static final String MODE_COMMON_UBB = "\\[(.*?)\\](.*?)\\[/\\1\\]";
	private static final String MODE_IMAGE = "<img src=\"(.*?)\"/?>";

	private static final int PATTERN_FLAG = Pattern.CASE_INSENSITIVE; //matched case insensitively

	/**
	 * 集合所有需要特殊处理的表情，ubb标签和html标签一致的会自动处理
	 * strong（加粗），u（下划线），b(加粗)
	 * @return
     */
	private static List<Element> getDefaultElements () {
		List<Element> list = new ArrayList<>();
		list.add(new CommonElement("em", "em"));
		list.add(new CommonElement("up", "sup"));
		list.add(new CommonElement("ub", "sub"));
//		list.add(new CommonElement("u", "u"));
		list.add(new ColorElement("color", "font"));
		list.add(new ImgElement("img", "img"));
		return list;
	}

	/**
	 * tips: 禁止ubb标签嵌套使用
	 * @param textView
	 * @param str
     * @return
     */
	public static Spanned ubb2Html(TextView textView, String str) {
		if(str == null) return null;

		CustomTagHandler customTagHandler = new CustomTagHandler();
		customTagHandler.setTextView(textView);

		String input = str.replace("\n", "<br/>");

		String ubb2html = ubb2Html(input, getDefaultElements(), customTagHandler);

		String html2format = formatImageToHtml(ubb2html, customTagHandler);

		return Html.fromHtml(html2format, null, customTagHandler);
	}

	private static String ubb2Html(String ubbStr, List<Element> elements, CustomTagHandler customTagHandler) {
		if(ubbStr == null) return null;

		HashMap<String, String> map = customTagHandler == null ?
				new HashMap<String, String>() : customTagHandler.getStringHashMap();

		String input = ubbStr;

		//replace unusual and spacial element
		for(Element element : elements) {
			String key = element.originLabel;
			String regex = MessageFormat.format(MODE_UNUSUAL_UBB, key);

			Pattern pattern = Pattern.compile(regex, PATTERN_FLAG);
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

		//replace common element automatically
		Pattern pattern = Pattern.compile(MODE_COMMON_UBB, PATTERN_FLAG);
		Matcher matcher = pattern.matcher(input);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			String tag = matcher.group(1).trim();
			String content = matcher.group(2).trim();
			String replacement = MessageFormat.format("<{0}>{1}</{0}>", tag, content);
			matcher.appendReplacement(sb, replacement);
		}
		matcher.appendTail(sb);
		input = sb.toString();

		return input;
	}

	private static String formatImageToHtml(String ubbStr, CustomTagHandler customTagHandler) {
		if(ubbStr == null) return null;

		HashMap<String, String> map = customTagHandler == null ?
				new HashMap<String, String>() : customTagHandler.getStringHashMap();

		String input = ubbStr;
		Element element = new ImageElement("img", "image");

		Pattern pattern = Pattern.compile(MODE_IMAGE, PATTERN_FLAG);
		Matcher matcher = pattern.matcher(input);

		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			String content = matcher.group(1).trim();
			String rightStr = element.getReplacement(map, content);
			matcher.appendReplacement(sb, rightStr);
		}
		matcher.appendTail(sb);
		input = sb.toString();

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
			return MessageFormat.format("<{0}>{1}</{0}>", replaceLabel, content);
		}
		
	}
    
   public static class ColorElement extends Element {

	    private static final String COLOR = "#FFFF66";

		public ColorElement(String originLabel, String replaceLabel) {
		   super(originLabel, replaceLabel);
	   }

		@Override
		public String getReplacement(HashMap<String, String> stringHashMap, String content) {
			return MessageFormat.format("<{0} color=\"{1}\">{2}</{0}>", replaceLabel, COLOR, content);
		}
		
	}

	public static class ImgElement extends Element {

		public ImgElement(String originLabel, String replaceLabel) {
			super(originLabel, replaceLabel);
		}

		@Override
		public String getReplacement(HashMap<String, String> stringHashMap, String content) {
			return MessageFormat.format("<{0} src=\"{1}\"/>", replaceLabel, content);
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

