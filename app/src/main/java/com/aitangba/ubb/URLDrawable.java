package com.aitangba.ubb;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by fhf11991 on 2016/12/22.
 */

public class URLDrawable extends BitmapDrawable {

    private Drawable mDrawable;
    private WeakReference<TextView> mWeakReference;

    public void setTextView(TextView textView) {
        mWeakReference = new WeakReference(textView);
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public URLDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        mDrawable = drawable;
        setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
    }

    public void setDrawable(Drawable drawable) {
        if(mWeakReference != null && mWeakReference.get() != null) {
            TextView textView = mWeakReference.get();
            Context context = textView.getContext().getApplicationContext();
            int width = dp2px(context, drawable.getIntrinsicWidth());
            int height = dp2px(context, drawable.getIntrinsicHeight());
            drawable.setBounds(0, 0, width, height);
            mDrawable = drawable;
            setBounds(0, 0, width, height);
            textView.setText(textView.getText());
        }
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }
}
