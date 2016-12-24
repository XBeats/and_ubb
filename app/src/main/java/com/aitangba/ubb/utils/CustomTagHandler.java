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
import java.util.HashMap;

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
                String source = mStringHashMap.get(tag);

                Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);

                UrlImageSpan urlImageSpan = new UrlImageSpan(drawable, source);
                urlImageSpan.setTextView(textView);
                urlImageSpan.execute();

                int len = output.length();
                output.append("\uFFFC");
                output.setSpan(urlImageSpan, len, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }


}
