package com.simplemobiletools.commons.views

import android.content.Context
import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.simplemobiletools.commons.R

// drag selection is based on https://github.com/afollestad/drag-select-recyclerview
class MyScalableRecyclerView : RecyclerView {
    private val AUTO_SCROLL_DELAY = 25L
    var isZoomingEnabled = false
    var isDragSelectionEnabled = false
    var listener: MyScalableRecyclerViewListener? = null

    private var mScaleDetector: ScaleGestureDetector

    private var dragSelectActive = false
    private var lastDraggedIndex = -1
    private var minReached = 0
    private var maxReached = 0
    private var initialSelection = 0

    private var hotspotHeight = 0
    private var hotspotOffsetTop = 0
    private var hotspotOffsetBottom = 0

    private var hotspotTopBoundStart = 0
    private var hotspotTopBoundEnd = 0
    private var hotspotBottomBoundStart = 0
    private var hotspotBottomBoundEnd = 0
    private var autoScrollVelocity = 0

    private var inTopHotspot = false
    private var inBottomHotspot = false

    private var currScaleFactor = 1.0f
    private var lastUp = 0L    // allow only pinch zoom, not double tap

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        hotspotHeight = context.resources.getDimensionPixelSize(R.dimen.dragselect_hotspot_height)

        val gestureListener = object : MyGestureListenerInterface {
            override fun getLastUp() = lastUp

            override fun getScaleFactor() = currScaleFactor

            override fun setScaleFactor(value: Float) {
                currScaleFactor = value
            }

            override fun getMainListener() = listener
        }

        mScaleDetector = ScaleGestureDetector(context, GestureListener(gestureListener))
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (hotspotHeight > -1) {
            hotspotTopBoundStart = hotspotOffsetTop
            hotspotTopBoundEnd = hotspotOffsetTop + hotspotHeight
            hotspotBottomBoundStart = measuredHeight - hotspotHeight - hotspotOffsetBottom
            hotspotBottomBoundEnd = measuredHeight - hotspotOffsetBottom
        }
    }

    private var autoScrollHandler = Handler()
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            if (inTopHotspot) {
                scrollBy(0, -autoScrollVelocity)
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY)
            } else if (inBottomHotspot) {
                scrollBy(0, autoScrollVelocity)
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!dragSelectActive) {
            try {
                super.dispatchTouchEvent(ev)
            } catch (ignored: Exception) {

            }
        }

        when (ev.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                dragSelectActive = false
                inTopHotspot = false
                inBottomHotspot = false
                autoScrollHandler.removeCallbacks(autoScrollRunnable)
                currScaleFactor = 1.0f
                lastUp = System.currentTimeMillis()
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (dragSelectActive) {
                    val itemPosition = getItemPosition(ev)
                    if (hotspotHeight > -1) {
                        if (ev.y in hotspotTopBoundStart..hotspotTopBoundEnd) {
                            inBottomHotspot = false
                            if (!inTopHotspot) {
                                inTopHotspot = true
                                autoScrollHandler.removeCallbacks(autoScrollRunnable)
                                autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY)
                            }

                            val simulatedFactor = (hotspotTopBoundEnd - hotspotTopBoundStart).toFloat()
                            val simulatedY = ev.y - hotspotTopBoundStart
                            autoScrollVelocity = (simulatedFactor - simulatedY).toInt() / 2
                        } else if (ev.y in hotspotBottomBoundStart..hotspotBottomBoundEnd) {
                            inTopHotspot = false
                            if (!inBottomHotspot) {
                                inBottomHotspot = true
                                autoScrollHandler.removeCallbacks(autoScrollRunnable)
                                autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY)
                            }

                            val simulatedY = ev.y + hotspotBottomBoundEnd
                            val simulatedFactor = (hotspotBottomBoundStart + hotspotBottomBoundEnd).toFloat()
                            autoScrollVelocity = (simulatedY - simulatedFactor).toInt() / 2
                        } else if (inTopHotspot || inBottomHotspot) {
                            autoScrollHandler.removeCallbacks(autoScrollRunnable)
                            inTopHotspot = false
                            inBottomHotspot = false
                        }
                    }

                    if (itemPosition != RecyclerView.NO_POSITION && lastDraggedIndex != itemPosition) {
                        lastDraggedIndex = itemPosition
                        if (minReached == -1) {
                            minReached = lastDraggedIndex
                        }

                        if (maxReached == -1) {
                            maxReached = lastDraggedIndex
                        }

                        if (lastDraggedIndex > maxReached) {
                            maxReached = lastDraggedIndex
                        }

                        if (lastDraggedIndex < minReached) {
                            minReached = lastDraggedIndex
                        }

                        listener?.selectRange(initialSelection, lastDraggedIndex, minReached, maxReached)

                        if (initialSelection == lastDraggedIndex) {
                            minReached = lastDraggedIndex
                            maxReached = lastDraggedIndex
                        }
                    }

                    return true
                }
            }
        }
        return if (isZoomingEnabled)
            mScaleDetector.onTouchEvent(ev)
        else
            true
    }

    fun setDragSelectActive(initialSelection: Int) {
        if (dragSelectActive || !isDragSelectionEnabled)
            return

        lastDraggedIndex = -1
        minReached = -1
        maxReached = -1
        this.initialSelection = initialSelection
        dragSelectActive = true
        listener?.selectItem(initialSelection)
    }

    private fun getItemPosition(e: MotionEvent): Int {
        val v = findChildViewUnder(e.x, e.y) ?: return RecyclerView.NO_POSITION

        if (v.tag == null || v.tag !is RecyclerView.ViewHolder) {
            throw IllegalStateException("Make sure your adapter makes a call to super.onBindViewHolder(), and doesn't override itemView tags.")
        }

        val holder = v.tag as RecyclerView.ViewHolder
        return holder.adapterPosition
    }


    class GestureListener(val gestureListener: MyGestureListenerInterface) : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private val ZOOM_IN_THRESHOLD = -0.4f
        private val ZOOM_OUT_THRESHOLD = 0.15f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            gestureListener.apply {
                if (System.currentTimeMillis() - getLastUp() < 1000)
                    return false

                val diff = getScaleFactor() - detector.scaleFactor
                if (diff < ZOOM_IN_THRESHOLD && getScaleFactor() == 1.0f) {
                    getMainListener()?.zoomIn()
                    setScaleFactor(detector.scaleFactor)
                } else if (diff > ZOOM_OUT_THRESHOLD && getScaleFactor() == 1.0f) {
                    getMainListener()?.zoomOut()
                    setScaleFactor(detector.scaleFactor)
                }
            }
            return false
        }
    }

    interface MyScalableRecyclerViewListener {
        fun zoomOut()

        fun zoomIn()

        fun selectItem(position: Int)

        fun selectRange(initialSelection: Int, lastDraggedIndex: Int, minReached: Int, maxReached: Int)
    }

    interface MyGestureListenerInterface {
        fun getLastUp(): Long

        fun getScaleFactor(): Float

        fun setScaleFactor(value: Float)

        fun getMainListener(): MyScalableRecyclerViewListener?
    }
}
