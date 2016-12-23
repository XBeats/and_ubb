package com.aitangba.ubb;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.widget.TextView;

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

    public void setStringHashMap(HashMap<String, String> stringHashMap) {
        mStringHashMap = stringHashMap;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if(opening && mStringHashMap != null && mStringHashMap.containsKey(tag)) {
            if(mWeakReference != null && mWeakReference.get() != null) {
                TextView textView = mWeakReference.get();
                Context context = textView.getContext().getApplicationContext();
                String source = mStringHashMap.get(tag);
                Log.d("CustomTagHandler", "source = " + source);

                Drawable drawable = context.getResources().getDrawable(R.mipmap.ic_launcher);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                VerticalImageSpan verticalImageSpan = new VerticalImageSpan(drawable);
                verticalImageSpan.setTextView(textView);

                ImageGetterAsyncTask getterTask = new ImageGetterAsyncTask(verticalImageSpan, source);
                getterTask.execute();

                int len = output.length();
                output.append("\uFFFC");
                output.setSpan(verticalImageSpan, len, output.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }
}
