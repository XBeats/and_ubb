package com.aitangba.ubb.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.widget.TextView;

import com.aitangba.ubb.R;

import org.xml.sax.XMLReader;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fhf11991 on 2016/12/23.
 */

public class CustomTagHandler implements Html.TagHandler {

    private WeakReference<TextView> mWeakReference;

    public void setTextView(TextView textView) {
        mWeakReference = new WeakReference(textView);
    }

    private HashMap<String, String> mStringHashMap = new HashMap<>();

    public HashMap<String, String> getStringHashMap() {
        return mStringHashMap;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if(opening && mStringHashMap != null && mStringHashMap.containsKey(tag)) {
            if(mWeakReference != null && mWeakReference.get() != null) {
                TextView textView = mWeakReference.get();
                Context context = textView.getContext().getApplicationContext();
                final String source = mStringHashMap.get(tag);
                String url = getValue(source, "src");
                int width = getSize(context.getApplicationContext(), getValue(source, "width"));
                int height = getSize(context.getApplicationContext(), getValue(source, "height"));

                Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);

                UrlImageSpan urlImageSpan = new UrlImageSpan(drawable, url);
                urlImageSpan.setTextView(textView);
                urlImageSpan.setWidth(width);
                urlImageSpan.setHeight(height);
                urlImageSpan.execute();

                int len = output.length();
                output.append("\uFFFC");
                output.setSpan(urlImageSpan, len, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private static final String MODE_IMAGE_ATTR = "{0}=\"(.*?)\"";
    private final String getValue(String uri, String localName) {

        String reg = MessageFormat.format(MODE_IMAGE_ATTR, localName);
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(uri);

        String value = null;
        if(matcher.find()) {
            value = matcher.group(1);
        }
        return value;
    }

    private final int getSize(Context context, String character) {
        if(character == null) return 0;

        int number;
        try {
            number = Integer.valueOf(character.replaceAll("[^(0-9)]", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }

        if(character.toLowerCase().contains("dp")) {
            return  (int) (number * context.getResources().getDisplayMetrics().density + 0.5F);
        }
        return number;
    }
}
