package mm.pndaza.tipitakamyanmar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import mm.pndaza.tipitakamyanmar.R;

public class IOSActivityIndicatorView extends View {

    private static final int DEFAULT_SPINNER_COLOR = Color.WHITE;
    private static final int DEFAULT_SPINNER_COUNT = 12;
    private static final int DEFAULT_SPINNER_ALPHA_MIN = 25; // 10% opacity

    private Paint mPaint;
    private RotateAnimation mAnimation;
    private int mSpinnerColor;
    private int mSpinnerCount;
    private int mSpinnerAlphaMin;
    private RectF mOval;

    public IOSActivityIndicatorView(Context context) {
        super(context);
        init(null);
    }

    public IOSActivityIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public IOSActivityIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mSpinnerColor = DEFAULT_SPINNER_COLOR;
        mSpinnerCount = DEFAULT_SPINNER_COUNT;
        mSpinnerAlphaMin = DEFAULT_SPINNER_ALPHA_MIN;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.IOSActivityIndicatorView);
            mSpinnerColor = a.getColor(R.styleable.IOSActivityIndicatorView_spinnerColor, DEFAULT_SPINNER_COLOR);
            mSpinnerCount = a.getInt(R.styleable.IOSActivityIndicatorView_spinnerCount, DEFAULT_SPINNER_COUNT);
            mSpinnerAlphaMin = a.getInt(R.styleable.IOSActivityIndicatorView_spinnerMinAlpha, DEFAULT_SPINNER_ALPHA_MIN);
            a.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSpinnerColor);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mOval = new RectF();

        // Setup animation
        mAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setDuration(1000);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation(mAnimation);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Update oval bounds
        int padding = Math.max(getPaddingLeft(), getPaddingRight());
        int size = Math.min(w, h) - (padding * 2);
        int left = (w - size) / 2;
        int top = (h - size) / 2;
        mOval.set(left, top, left + size, top + size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float radius = (mOval.width() / 2f) * 0.8f;
        float centerX = mOval.centerX();
        float centerY = mOval.centerY();
        float spinnerWidth = radius * 0.1f;
        float spinnerLength = radius * 0.3f;

        mPaint.setStrokeWidth(spinnerWidth);

        for (int i = 0; i < mSpinnerCount; i++) {
            float angle = (i * 360f / mSpinnerCount);
            float alpha = (255 - mSpinnerAlphaMin) * (i / (float)mSpinnerCount) + mSpinnerAlphaMin;

            mPaint.setAlpha((int)alpha);

            float startX = centerX + radius * (float)Math.cos(Math.toRadians(angle));
            float startY = centerY + radius * (float)Math.sin(Math.toRadians(angle));
            float stopX = centerX + (radius - spinnerLength) * (float)Math.cos(Math.toRadians(angle));
            float stopY = centerY + (radius - spinnerLength) * (float)Math.sin(Math.toRadians(angle));

            canvas.drawLine(startX, startY, stopX, stopY, mPaint);
        }
    }

    public void setSpinnerColor(int color) {
        mSpinnerColor = color;
        mPaint.setColor(color);
        invalidate();
    }
}
