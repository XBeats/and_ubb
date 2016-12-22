package com.aitangba.ubb;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by fhf11991 on 2016/12/22.
 */
public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {

    private URLDrawable mURLDrawable;
    private String mUrl;

    public ImageGetterAsyncTask(URLDrawable urlDrawable, String url) {
        mURLDrawable = urlDrawable;
        mUrl = url;
    }

    @Override
    protected Drawable doInBackground(String... params) {
        Drawable drawable = null;
        try {
            URL url = new URL(mUrl);
            drawable = Drawable.createFromStream(url.openStream(), "");
        } catch (Exception e) {
            return null;
        }
        return drawable;
    }

    @Override
    protected void onPostExecute(Drawable result) {
        if (result != null) {
            mURLDrawable.setDrawable(result);
        }
    }
}