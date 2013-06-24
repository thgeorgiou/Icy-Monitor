/*
 * Copyright 2013 Pascal Welsch
 * Copyright 2013 Thanasis Georgiou
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 *
 */
package com.sakisds.icymonitor.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import com.sakisds.icymonitor.R;

/**
 * The Class HoloCircularProgressBar.
 *
 * @author Pascal.Welsch
 * @since 05.03.2013
 */
public class HoloCircularProgressBar extends View {
    /**
     * used to save the super state on configuration change
     */
    private static final String INSTNACE_STATE_SAVEDSTATE = "saved_state";

    /**
     * used to save the progress on configuration changes
     */
    private static final String INSTNACE_STATE_PROGRESS = "progress";

    /**
     * true if not all properties are set. then the view isn't drawn and there
     * are no errors in the LayoutEditor
     */
    private boolean mIsInitializing = true;

    /**
     * the paint for the background.
     */
    private Paint mBackgroundColorPaint = new Paint();

    /**
     * The stroke width used to paint the circle.
     */
    private int mCircleStrokeWidth = 30;

    /**
     * The rectangle enclosing the circle.
     */
    private final RectF mCircleBounds = new RectF();

    /**
     * the color of the progress.
     */
    private int mProgressColor;

    /**
     * paint for the progress.
     */
    private final Paint mProgressColorPaint;

    /**
     * The color of the progress background.
     */
    private int mProgressBackgroundColor;

    /**
     * The current progress.
     */
    private float mProgress = 0.3f;

    /**
     * The gravity of the view. Where should the Circle be drawn within the
     * given bounds
     * <p/>
     * {@link #computeInsets(int, int)}
     */
    private final int mGravity;

    /**
     * The Horizontal inset calcualted in {@link #computeInsets(int, int)}
     * depends on {@link #mGravity}.
     */
    private int mHorizontalInset = 0;

    /**
     * The Vertical inset calcualted in {@link #computeInsets(int, int)} depends
     * on {@link #mGravity}..
     */
    private int mVerticalInset = 0;

    /**
     * The Translation offset x which gives us the ability to use our own
     * coordinates system.
     */
    private float mTranslationOffsetX;

    /**
     * The Translation offset y which gives us the ability to use our own
     * coordinates system.
     */
    private float mTranslationOffsetY;

    /**
     * the overdraw is true if the progress is over 1.0.
     */
    private boolean mOverrdraw = false;

    /**
     * Instantiates a new holo circular progress bar.
     *
     * @param context the context
     */
    public HoloCircularProgressBar(final Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new holo circular progress bar.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public HoloCircularProgressBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.circularProgressBarStyle);
    }

    /**
     * Instantiates a new holo circular progress bar.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public HoloCircularProgressBar(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        // load the styled attributes and set their properties
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HoloCircularProgressBar,
                defStyle, 0);

        setProgressColor(attributes.getColor(R.styleable.HoloCircularProgressBar_progress_color, Color.CYAN));
        setProgressBackgroundColor(attributes.getColor(R.styleable.HoloCircularProgressBar_progress_background_color,
                Color.MAGENTA));
        setProgress(attributes.getFloat(R.styleable.HoloCircularProgressBar_progress, 0.0f));
        setWheelSize((int) attributes.getDimension(R.styleable.HoloCircularProgressBar_stroke_width, 10));
        mGravity = attributes.getInt(R.styleable.HoloCircularProgressBar_layout_gravity, Gravity.CENTER);

        attributes.recycle();

        mBackgroundColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundColorPaint.setColor(mProgressBackgroundColor);
        mBackgroundColorPaint.setStyle(Paint.Style.STROKE);
        mBackgroundColorPaint.setStrokeWidth(mCircleStrokeWidth);

        mProgressColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressColorPaint.setColor(mProgressColor);
        mProgressColorPaint.setStyle(Paint.Style.STROKE);
        mProgressColorPaint.setStrokeWidth(mCircleStrokeWidth);

        // the view has now all properties and can be drawn
        mIsInitializing = false;

    }

    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(final Canvas canvas) {
        // All of our positions are using our internal coordinate system.
        // Instead of translating
        // them we let Canvas do the work for us.
        canvas.translate(mTranslationOffsetX, mTranslationOffsetY);

        final float progressRotation = getCurrentRotation();

        // draw the background
        if (!mOverrdraw) {
            canvas.drawArc(mCircleBounds, 270, -(360 - progressRotation), false, mBackgroundColorPaint);
        }

        // draw the progress or a full circle if overdraw is true
        canvas.drawArc(mCircleBounds, 270, mOverrdraw ? 360 : progressRotation, false, mProgressColorPaint);
    }

    /* (non-Javadoc)
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, height);

        final float halfWidth = min * 0.5f;
        int drawOffset = 10;

        mCircleBounds.set(-halfWidth + drawOffset, -halfWidth + drawOffset, halfWidth - drawOffset, halfWidth - drawOffset);

        computeInsets(width - min, height - min);

        mTranslationOffsetX = halfWidth + mHorizontalInset;
        mTranslationOffsetY = halfWidth + mVerticalInset;

    }

    /* (non-Javadoc)
     * @see android.view.View#onRestoreInstanceState(android.os.Parcelable)
     */
    @Override
    protected void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            setProgress(bundle.getFloat(INSTNACE_STATE_PROGRESS));
            super.onRestoreInstanceState(bundle.getParcelable(INSTNACE_STATE_SAVEDSTATE));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    /* (non-Javadoc)
     * @see android.view.View#onSaveInstanceState()
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTNACE_STATE_SAVEDSTATE, super.onSaveInstanceState());
        bundle.putFloat(INSTNACE_STATE_PROGRESS, mProgress);
        return bundle;
    }

    /**
     * Compute insets.
     * _______________________
     * |_________dx/2_________|
     * |......|.''''`.|......|
     * |-dx/2-|| View ||-dx/2-|
     * |______|`.____.______|
     * |________ dx/2_________|
     *
     * @param dx the dx the horizontal unfilled space
     * @param dy the dy the horizontal unfilled space
     */
    private void computeInsets(final int dx, final int dy) {
        //final int layoutDirection;
        int absoluteGravity = mGravity;
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        //	layoutDirection = getLayoutDirection();
        //	absoluteGravity = Gravity.getAbsoluteGravity(mGravity, layoutDirection);
        //}

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                mHorizontalInset = 0;
                break;
            case Gravity.RIGHT:
                mHorizontalInset = dx;
                break;
            case Gravity.CENTER_HORIZONTAL:
            default:
                mHorizontalInset = dx / 2;
                break;
        }
        switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                mVerticalInset = 0;
                break;
            case Gravity.BOTTOM:
                mVerticalInset = dy;
                break;
            case Gravity.CENTER_VERTICAL:
            default:
                mVerticalInset = dy / 2;
                break;
        }
    }

    /**
     * Gets the current rotation.
     *
     * @return the current rotation
     */
    private float getCurrentRotation() {
        return 360 * mProgress;
    }

    /**
     * Sets the progress background color.
     *
     * @param color the new progress background color
     */
    private void setProgressBackgroundColor(final int color) {
        mProgressBackgroundColor = color;
    }

    /**
     * Sets the progress color.
     *
     * @param color the new progress color
     */
    private void setProgressColor(final int color) {
        mProgressColor = color;
    }

    /**
     * Sets the wheel size.
     *
     * @param dimension the new wheel size
     */
    private void setWheelSize(final int dimension) {
        mCircleStrokeWidth = dimension;
    }

    public float getProgress() {
        return mProgress;
    }

    /**
     * Gets the progress color.
     *
     * @return the progress color
     */
    public int getProgressColor() {
        return mProgressColor;
    }

    /**
     * Sets the progress.
     *
     * @param progress the new progress
     */
    public void setProgress(final float progress) {
        if (progress == mProgress) {
            return;
        }

        mProgress = progress % 1.0f;

        mOverrdraw = progress >= 1;

        if (!mIsInitializing) {
            invalidate();
        }
    }

}
