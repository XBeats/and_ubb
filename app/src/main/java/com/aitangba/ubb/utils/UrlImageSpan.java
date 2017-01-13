package com.aitangba.ubb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by fhf11991 on 2016/12/23.
 * 自定义ImageSpan 主要用于解决TextView显示内容不能居中的问题
 */

public class UrlImageSpan extends ImageSpan {

    private Drawable mDrawable;

    private WeakReference<TextView> mWeakReference;

    public void setTextView(TextView textView) {
        mWeakReference = new WeakReference(textView);
    }

    private int mWidth;
    private int mHeight;

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public void setHeight(int height) {
        this.mHeight = height;
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

            int width = mWidth;
            int height = mHeight;
            if(width == 0 || height == 0) {
                width = dp2px(context, drawable.getIntrinsicWidth());
                height = dp2px(context, drawable.getIntrinsicHeight());
            }
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
                URLConnection urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);

                InputStream inputStream = urlConnection.getInputStream();

                String filename = mUrl.substring(mUrl.lastIndexOf("/") + 1);
                final int sdkVersion = Build.VERSION.SDK_INT;
                if ((sdkVersion == Build.VERSION_CODES.KITKAT
                        || sdkVersion == Build.VERSION_CODES.KITKAT_WATCH
                        || sdkVersion == Build.VERSION_CODES.LOLLIPOP )
                    && (filename.toLowerCase().contains(".png")
                        || (filename.toLowerCase().contains(".gif")))) {
                    //cache the inputStream into ByteArrayOutputStream
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > -1 ) {
                        byteArrayOutputStream.write(buffer, 0, len);
                    }
                    byteArrayOutputStream.flush();

                    //first use the cache
                    InputStream stream1 = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    GifDecoder mGifDecoder = new GifDecoder();
                    mGifDecoder.read(stream1);
                    final int n = mGifDecoder.getFrameCount();

                    Drawable drawable;
                    if(n == 1) {
                        Bitmap bitmap = mGifDecoder.getFrame(1);
                        drawable = new BitmapDrawable(bitmap);
                    } else if(n == 0) {
                        //second use the cache
                        InputStream stream2 = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                        drawable = Drawable.createFromStream(stream2, "");
                    } else {
                        drawable = null;
                    }

                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException ioExc) {}
                    return drawable;
                }
                return Drawable.createFromStream(inputStream, "");
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
