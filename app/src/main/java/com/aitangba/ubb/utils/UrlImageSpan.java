package com.aitangba.ubb.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by fhf11991 on 2016/12/23.
 */
public class UrlImageSpan extends ImageSpan {

    private WeakReference<TextView> mWeakReference;
    private Drawable mDrawable;

    public void setTextView(TextView textView) {
        mWeakReference = new WeakReference(textView);
    }

    public UrlImageSpan(Drawable drawable, String source) {
        super(drawable, source);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public void execute() {
        new ImageGetterAsyncTask(this, getSource()).execute();
    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fontMetricsInt) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if (fontMetricsInt != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fontMetricsInt.ascent = -bottom;
            fontMetricsInt.top = -bottom;
            fontMetricsInt.bottom = top;
            fontMetricsInt.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        int transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public Drawable getDrawable() {
        return mDrawable == null ? super.getDrawable() : mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        if(mWeakReference != null && mWeakReference.get() != null) {
            TextView textView = mWeakReference.get();
            Context context = textView.getContext().getApplicationContext();
            int width = dp2px(context, drawable.getIntrinsicWidth());
            int height = dp2px(context, drawable.getIntrinsicHeight());
            drawable.setBounds(0, 0, width, height);
            mDrawable = drawable;
            textView.setText(textView.getText());
        }
    }

    private final int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }

    private static class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {

        private WeakReference<UrlImageSpan> mVerticalImageSpan;
        private String mUrl;

        public ImageGetterAsyncTask(UrlImageSpan urlImageSpan, String url) {
            mVerticalImageSpan = new WeakReference<>(urlImageSpan);
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
            if (result != null && mVerticalImageSpan != null && mVerticalImageSpan.get() != null) {
                mVerticalImageSpan.get().setDrawable(result);
            }
        }
    }
}
