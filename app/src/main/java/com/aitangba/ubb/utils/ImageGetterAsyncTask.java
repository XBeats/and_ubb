package com.aitangba.ubb.utils;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by fhf11991 on 2016/12/22.
 */
public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {

    private VerticalImageSpan mVerticalImageSpan;
    private String mUrl;

    public ImageGetterAsyncTask(VerticalImageSpan verticalImageSpan, String url) {
        mVerticalImageSpan = verticalImageSpan;
        mUrl = url;
    }

    @Override
    protected Drawable doInBackground(String... params) {
        try {
            URL url = new URL(mUrl);
            return Drawable.createFromStream(url.openStream(), "");
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Drawable result) {
        if (result != null) {
            mVerticalImageSpan.setDrawable(result);
        }
    }
}