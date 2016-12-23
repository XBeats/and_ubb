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
 * Created by XBeats on 2016/12/23.
 */

public class HtmlUtils {
    private static final String MODE_UBB = "<img src=\"(.*?)\"/>";

    private static List<Element> getDefaultElements () {
        List<Element> list = new ArrayList<>();
        list.add(new ImageElement("img", "image"));
        return list;
    }

    public static Spanned html2Html(TextView textView, String str) {
        CustomTagHandler customTagHandler = new CustomTagHandler();
        customTagHandler.setTextView(textView);

        String html = toHtml(str, getDefaultElements(), customTagHandler);

        return Html.fromHtml(html, null, customTagHandler);
    }

    /**
     * 禁止ubb标签嵌套使用
     * @param ubbStr
     * @param elements
     * @param customTagHandler
     * @return
     */
    private static String toHtml(String ubbStr, List<Element> elements, CustomTagHandler customTagHandler) {
        if(ubbStr == null) return null;

        String input = ubbStr.replace("\n", "<br/>");
        HashMap<String, String> map = customTagHandler == null ?
                new HashMap<String, String>() : customTagHandler.getStringHashMap();

        for(Element element : elements) {
            String key = element.originLabel;
            String regex = MessageFormat.format(MODE_UBB, key);

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

    public static class ImageElement extends Element {

        public ImageElement(String originLabel, String replaceLabel) {
            super(originLabel, replaceLabel);
        }

        @Override
        public String getReplacement(HashMap<String, String> stringHashMap, String content) {
            String tag = replaceLabel + stringHashMap.size();
            stringHashMap.put(tag, content);
            return MessageFormat.format("<{0} src=\"{1}\"/>", tag, content);
        }
    }
}
