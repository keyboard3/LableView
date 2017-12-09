package com.keyboard3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * @author keyboard3 on 2017/12/5
 */

@SuppressLint("AppCompatCustomView")
public class LableView extends TextView {

    private int mY;
    private float mContentWidth;
    private String mEndText;
    private int mCNNum = 0;
    private float mEndWidth = 0;

    public LableView(Context context) {
        this(context, null);
    }

    public LableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray custom = context.obtainStyledAttributes(
                attrs, R.styleable.LableView, defStyleAttr, 0);
        mCNNum = custom.getInt(R.styleable.LableView_cnNum, 0);
        mEndText = custom.getString(R.styleable.LableView_endText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!TextUtils.isEmpty(mEndText)) {
            mEndWidth = getPaint().measureText(mEndText);
        }
        float width = 0;
        if (mCNNum != 0 && WordUtil.isChinese(getText().toString())) {
            width = getPaint().measureText("测") * mCNNum;
            width += mEndWidth;
            setMeasuredDimension((int) width, getMeasuredHeight());
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = (int) getPaint().measureText(getText() + mEndText);
            width += mEndWidth;
            setMeasuredDimension((int) width, getMeasuredHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        mContentWidth = getMeasuredWidth() - mEndWidth;
        String text = getText().toString();
        mY = 0;
        mY += getTextSize();
        Layout layout = getLayout();

        // layout.getLayout()在4.4.3出现NullPointerException
        if (layout == null) {
            return;
        }

        Paint.FontMetrics fm = paint.getFontMetrics();

        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout
                .getSpacingAdd());
        drawScaledText(canvas, 0, WordUtil.getWords(getText().toString()), mContentWidth);
        mY += textHeight;
        mY -= textHeight;
        if (!TextUtils.isEmpty(mEndText)) {
            canvas.drawText(mEndText, getMeasuredWidth() - paint.measureText(mEndText), mY, paint);
        }
    }

    private void drawScaledText(Canvas canvas, int start, List<String> words,
                                float width) {
        float used = 0, space = 0;
        float[] wordWidths = new float[words.size()];
        for (int i = 0; i < words.size(); i++) {
            wordWidths[i] = StaticLayout.getDesiredWidth(words.get(i), getPaint());
            used += wordWidths[i];
        }
        space = (width - used) / (words.size() - 1);
        float x = 0;
        for (int i = 0; i < words.size(); i++) {
            canvas.drawText(words.get(i), x, mY, getPaint());
            x += space + wordWidths[i];
        }
    }
}
