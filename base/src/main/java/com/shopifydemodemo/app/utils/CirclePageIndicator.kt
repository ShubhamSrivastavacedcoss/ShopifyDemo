package com.shopifydemodemo.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager

import com.shopifydemodemo.app.R

import java.util.Objects

import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.widget.GridLayout.HORIZONTAL
import androidx.viewpager.widget.PagerAdapter

class CirclePageIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle), PageIndicator {
    private val mPaintPageFill = Paint(ANTI_ALIAS_FLAG)
    private val mPaintStroke = Paint(ANTI_ALIAS_FLAG)
    private val mPaintFill = Paint(ANTI_ALIAS_FLAG)
    private val mRadius: Float
    private var mViewPager: ViewPager? = null
    private var mListener: ViewPager.OnPageChangeListener? = null
    private var mCurrentPage: Int = 0
    private var mSnapPage: Int = 0
    private var mPageOffset: Float = 0.toFloat()
    private var mScrollState: Int = 0
    private val mOrientation: Int
    private val mCentered: Boolean
    private val mSnap: Boolean
    private val mTouchSlop: Int
    private var mLastMotionX = -1f
    private var mActivePointerId = INVALID_POINTER
    private var mIsDragging: Boolean = false

    init {

        //Load defaults from resources
        val res = resources
        val defaultPageColor = res.getColor(R.color.black)
        //default_circle_indicator_fill_color
        val defaultFillColor = res.getColor(R.color.main_color_gray_lt)
        val defaultOrientation = res.getInteger(R.integer.default_circle_indicator_orientation)
        val defaultStrokeColor = res.getColor(R.color.black)
        val defaultStrokeWidth = res.getDimension(R.dimen.default_circle_indicator_stroke_width)
        val defaultRadius = res.getDimension(R.dimen.default_circle_indicator_radius)
        val defaultCentered = res.getBoolean(R.bool.default_circle_indicator_centered)
        val defaultSnap = res.getBoolean(R.bool.default_circle_indicator_snap)
        //Retrieve styles attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.circlepageindicator, defStyle, 0)
        mCentered = a.getBoolean(R.styleable.circlepageindicator_centered, defaultCentered)
        mOrientation = a.getInt(R.styleable.circlepageindicator_android_orientation, defaultOrientation)
        mPaintPageFill.style = Paint.Style.FILL
        mPaintPageFill.color = a.getColor(R.styleable.circlepageindicator_pageColor, defaultPageColor)
        //mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.color = a.getColor(R.styleable.circlepageindicator_strokeColor, defaultStrokeColor)
        mPaintStroke.strokeWidth = a.getDimension(R.styleable.circlepageindicator_strokeWidth, defaultStrokeWidth)
        mPaintFill.style = Paint.Style.FILL
        mPaintFill.color = a.getColor(R.styleable.circlepageindicator_fillColor, defaultFillColor)
        mRadius = a.getDimension(R.styleable.circlepageindicator_radius, defaultRadius)
        mSnap = a.getBoolean(R.styleable.circlepageindicator_snap, defaultSnap)
        val background = a.getDrawable(R.styleable.circlepageindicator_android_background)
        if (background != null) {
            setBackground(background)
        }
        a.recycle()
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledPagingTouchSlop
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mViewPager == null) {
            return
        }
        val count = Objects.requireNonNull<PagerAdapter>(mViewPager!!.adapter).getCount()
        if (count == 0) {
            return
        }
        if (mCurrentPage >= count) {
            setCurrentItem(count - 1)
            return
        }
        val longSize: Int
        val longPaddingBefore: Int
        val longPaddingAfter: Int
        val shortPaddingBefore: Int
        if (mOrientation == HORIZONTAL) {
            longSize = width
            longPaddingBefore = paddingLeft
            longPaddingAfter = paddingRight
            shortPaddingBefore = paddingTop
        } else {
            longSize = height
            longPaddingBefore = paddingTop
            longPaddingAfter = paddingBottom
            shortPaddingBefore = paddingLeft
        }
        val threeRadius = mRadius * 3
        val shortOffset = shortPaddingBefore + mRadius
        var longOffset = longPaddingBefore + mRadius
        if (mCentered) {
            longOffset += (longSize - longPaddingBefore - longPaddingAfter) / 2.0f - count * threeRadius / 2.0f
        }
        var dX: Float
        var dY: Float
        var pageFillRadius = mRadius
        if (mPaintStroke.strokeWidth > 0) {
            pageFillRadius -= mPaintStroke.strokeWidth / 2.0f
        }
        //Draw stroked circles
        for (iLoop in 0 until count) {
            val drawLong = longOffset + iLoop * threeRadius
            if (mOrientation == HORIZONTAL) {
                dX = drawLong
                dY = shortOffset
            } else {
                dX = shortOffset
                dY = drawLong
            }
            // Only paint fill if not completely transparent
            if (mPaintPageFill.alpha > 0) {
                canvas.drawCircle(dX, dY, pageFillRadius, mPaintPageFill)
            }

            // Only paint stroke if a stroke width was non-zero
            if (pageFillRadius != mRadius) {
                canvas.drawCircle(dX, dY, mRadius, mPaintStroke)
            }
        }
        //Draw the filled circle according to the current scroll
        var cx = (if (mSnap) mSnapPage else mCurrentPage) * threeRadius
        if (!mSnap) {
            cx += mPageOffset * threeRadius
        }
        if (mOrientation == HORIZONTAL) {
            dX = longOffset + cx
            dY = shortOffset
        } else {
            dX = shortOffset
            dY = longOffset + cx
        }
        canvas.drawCircle(dX, dY, mRadius, mPaintFill)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (super.onTouchEvent(ev)) {
            return true
        }
        if (mViewPager == null || Objects.requireNonNull<PagerAdapter>(mViewPager!!.adapter).getCount() == 0) {
            return false
        }

        val action = ev.action and ev.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)// MotionEventCompat.getPointerId(ev, 0);
                mLastMotionX = ev.x
            }

            MotionEvent.ACTION_MOVE -> {
                val activePointerIndex = ev.findPointerIndex(mActivePointerId)//MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                val x = ev.getX(activePointerIndex)//MotionEventCompat.getX(ev, activePointerIndex);
                val deltaX = x - mLastMotionX

                if (!mIsDragging) {
                    if (Math.abs(deltaX) > mTouchSlop) {
                        mIsDragging = true
                    }
                }

                if (mIsDragging) {
                    mLastMotionX = x
                    if (mViewPager!!.isFakeDragging || mViewPager!!.beginFakeDrag()) {
                        mViewPager!!.fakeDragBy(deltaX)
                    }
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (!mIsDragging) {
                    val count = mViewPager!!.adapter!!.count
                    val width = width
                    val halfWidth = width / 2f
                    val sixthWidth = width / 6f

                    if (mCurrentPage > 0 && ev.x < halfWidth - sixthWidth) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            mViewPager!!.currentItem = mCurrentPage - 1
                        }
                        return true
                    } else if (mCurrentPage < count - 1 && ev.x > halfWidth + sixthWidth) {
                        if (action != MotionEvent.ACTION_CANCEL) {
                            mViewPager!!.currentItem = mCurrentPage + 1
                        }
                        return true
                    }
                }

                mIsDragging = false
                mActivePointerId = INVALID_POINTER
                if (mViewPager!!.isFakeDragging) mViewPager!!.endFakeDrag()
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = ev.actionIndex//MotionEventCompat.getActionIndex(ev);
                mLastMotionX = ev.getX(index)//MotionEventCompat.getX(ev, index);
                mActivePointerId = ev.getPointerId(index)//MotionEventCompat.getPointerId(ev, index);
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = ev.actionIndex//MotionEventCompat.getActionIndex(ev);
                val pointerId = ev.getPointerId(pointerIndex)//MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = ev.getPointerId(newPointerIndex)//MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                mLastMotionX = ev.getX(mActivePointerId)//MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, mActivePointerId));
            }
        }

        return true
    }

    override fun setViewPager(view: ViewPager) {
        if (mViewPager === view) {
            return
        }
        if (mViewPager != null) {

            mViewPager!!.addOnPageChangeListener(null!!)
        }
        checkNotNull(view.adapter) { "ViewPager does not have adapter instance." }
        mViewPager = view
        mViewPager!!.addOnPageChangeListener(this)
        invalidate()
    }

    override fun setViewPager(view: ViewPager, initialPosition: Int) {
        setViewPager(view)
        setCurrentItem(initialPosition)
    }

    override fun setCurrentItem(item: Int) {
        checkNotNull(mViewPager) { "ViewPager has not been bound." }
        mViewPager!!.currentItem = item
        mCurrentPage = item
        invalidate()
    }

    override fun notifyDataSetChanged() {
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {
        mScrollState = state

        if (mListener != null) {
            mListener!!.onPageScrollStateChanged(state)
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mCurrentPage = position
        mPageOffset = positionOffset
        invalidate()

        if (mListener != null) {
            mListener!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }
    }

    override fun onPageSelected(position: Int) {
        if (mSnap || mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mCurrentPage = position
            mSnapPage = position
            invalidate()
        }

        if (mListener != null) {
            mListener!!.onPageSelected(position)
        }
    }

    override fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        mListener = listener
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View#onMeasure(int, int)
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mOrientation == HORIZONTAL) {
            setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec))
        } else {
            setMeasuredDimension(measureShort(widthMeasureSpec), measureLong(heightMeasureSpec))
        }
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private fun measureLong(measureSpec: Int): Int {
        var result: Int
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY || mViewPager == null) {
            //We were told how big to be
            result = specSize
        } else {
            //Calculate the width according the views count
            val count = Objects.requireNonNull<PagerAdapter>(mViewPager!!.adapter).getCount()
            result = (paddingLeft.toFloat() + paddingRight.toFloat()
                    + count.toFloat() * 2f * mRadius + (count - 1) * mRadius + 1f).toInt()
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private fun measureShort(measureSpec: Int): Int {
        var result: Int
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        if (specMode == View.MeasureSpec.EXACTLY) {
            //We were told how big to be
            result = specSize
        } else {
            //Measure the height
            result = (2 * mRadius + paddingTop.toFloat() + paddingBottom.toFloat() + 1f).toInt()
            //Respect AT_MOST value if that was what is called for by measureSpec
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        mCurrentPage = savedState.currentPage
        mSnapPage = savedState.currentPage
        requestLayout()
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState!!)
        savedState.currentPage = mCurrentPage
        return savedState
    }

    internal class SavedState : View.BaseSavedState {
        var currentPage: Int = 0

        constructor(superState: Parcelable) : super(superState) {}

        private constructor(`in`: Parcel) : super(`in`) {
            currentPage = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(currentPage)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        override fun describeContents(): Int {
            return 0
        }

         object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        private val INVALID_POINTER = -1
    }
}