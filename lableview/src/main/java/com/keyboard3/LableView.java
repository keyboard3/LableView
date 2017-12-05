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
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author keyboard3 on 2017/12/5
 */

@SuppressLint("AppCompatCustomView")
public class LableView extends TextView {

    private int mY;
    private int mContentWidth;
    private String mEndText;
    private int mCharNum = 0;
    private int mEndWidth = 0;

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
        mCharNum = custom.getInt(R.styleable.LableView_charNum, 0);
        mEndText = custom.getString(R.styleable.LableView_endText);
        Log.d("gcy", "mEndText:" + mEndText);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!TextUtils.isEmpty(mEndText)) {
            mEndWidth = (int) getPaint().measureText(mEndText);
        }
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            int width = 0;
            if (mCharNum != 0) {
                width = (int) getPaint().measureText("测") * mCharNum;
            } else {
                width = (int) getPaint().measureText(getText() + mEndText);
            }
            width += mEndWidth;
            setMeasuredDimension(width, getMeasuredHeight());
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
        int lineStart = layout.getLineStart(0);
        int lineEnd = layout.getLineEnd(0);
        float width = StaticLayout.getDesiredWidth(text, lineStart,
                lineEnd, getPaint());
        String line = text.substring(lineStart, lineEnd);
        if (needScale(line)) {
            drawScaledText(canvas, lineStart, line, width);
        } else {
            canvas.drawText(line, 0, mY, paint);
        }
        mY += textHeight;
        mY -= textHeight;
        if (!TextUtils.isEmpty(mEndText)) {
            canvas.drawText(mEndText, getMeasuredWidth() - paint.measureText(mEndText), mY, paint);
        }
    }

    private void drawScaledText(Canvas canvas, int lineStart, String line,
                                float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineStart, line)) {
            String blanks = "  ";
            canvas.drawText(blanks, x, mY, getPaint());
            float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
            x += bw;

            line = line.substring(3);
        }

        int gapCount = line.length() - 1;
        int i = 0;
        if (line.length() > 2 && line.charAt(0) == 12288
                && line.charAt(1) == 12288) {
            String substring = line.substring(0, 2);
            float cw = StaticLayout.getDesiredWidth(substring, getPaint());
            canvas.drawText(substring, x, mY, getPaint());
            x += cw;
            i += 2;
        }

        float d = (mContentWidth - lineWidth) / gapCount;
        for (; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mY, getPaint());
            x += cw + d;
        }
    }

    private boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.length() > 3 && line.charAt(0) == ' '
                && line.charAt(1) == ' ';
    }

    private boolean needScale(String line) {
        if (line == null || line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }
}
