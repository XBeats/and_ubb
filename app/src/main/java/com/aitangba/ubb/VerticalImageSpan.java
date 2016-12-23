package com.aitangba.ubb;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by fhf11991 on 2016/12/23.
 */
public class VerticalImageSpan extends ImageSpan {

    private WeakReference<TextView> mWeakReference;
    private Drawable mDrawable;

    public void setTextView(TextView textView) {
        mWeakReference = new WeakReference(textView);
    }

    public VerticalImageSpan(Drawable drawable) {
        super(drawable);
        mDrawable = drawable;
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
        return mDrawable;
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

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }
}
