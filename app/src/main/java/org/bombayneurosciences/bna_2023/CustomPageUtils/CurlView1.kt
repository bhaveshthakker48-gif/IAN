package org.bombayneurosciences.bna_2023.CustomPageUtils
import android.R.attr.radius
import android.content.Context
import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.sqrt


/**
 * OpenGL ES View.
 *
 * @author harism
 */
 class CurlView1 : GLSurfaceView, OnTouchListener, CurlRenderer1.Observer {
    private val mAllowLastPageCurl = true

    private var mAnimate = false
    private val mAnimationDurationTime: Long = 300
    private val mAnimationSource = PointF()
    private var mAnimationStartTime: Long = 0
    private val mAnimationTarget = PointF()
    private var mAnimationTargetEvent = 0

    private val mCurlDir = PointF()

    val radius: Double = 5.0

    private val mCurlPos = PointF()
    private var mCurlState = CURL_NONE

    /**
     * Get current page index. Page indices are zero based values presenting
     * page being shown on right side of the book.
     */
    // Current bitmap index. This is always showed as front of right page.
    var currentIndex: Int = 0
        private set

    // Start position for dragging.
    private val mDragStartPos = PointF()

    private val mEnableTouchPressure = false

    // Bitmap size. These are updated from renderer once it's initialized.
    private var mPageBitmapHeight = -1

    private var mPageBitmapWidth = -1

    // Page meshes. Left and right meshes are 'static' while curl is used to
    // show page flipping.
    private var mPageCurl: CurlMesh1? = null

    private var mPageLeft: CurlMesh1? = null
    private val mPageProvider: PageProvider? = null
    private var mPageRight: CurlMesh1? = null

    private val mPointerPos: PointerPosition = PointerPosition()

    private var mRenderer: CurlRenderer1? = null
    private val mRenderLeftPage = true
    private val mSizeChangedObserver: SizeChangedObserver? = null

    // One page is the default.
    private var mViewMode = SHOW_ONE_PAGE

    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private val mScaleFactor = 1.0f
    private var minScaleFactor = 1.0f
    private var maxScaleFactor = 5.0f

    /**
     * Default constructor.
     */
    constructor(ctx: Context) : super(ctx) {
        init(ctx)
    }

    /**
     * Default constructor.
     */
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        init(ctx)
    }

    /**
     * Default constructor.
     */
    constructor(ctx: Context, attrs: AttributeSet?, defStyle: Int) : this(ctx, attrs)

    /**
     * Initialize method.
     */
    private fun init(ctx: Context) {
        mRenderer = CurlRenderer1(this)
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        setOnTouchListener(this)

        // Even though left and right pages are static we have to allocate room
        // for curl on them too as we are switching meshes. Another way would be
        // to swap texture ids only.
        mPageLeft = CurlMesh1(10)
        mPageRight = CurlMesh1(10)
        mPageCurl = CurlMesh1(10)
        mPageLeft!!.setFlipTexture(true)
        mPageRight!!.setFlipTexture(false)

        mScaleGestureDetector = ScaleGestureDetector(ctx, ScaleListener())
    }

    fun addPage(curlPage: CurlPage, pageSide: Int) {
        when (pageSide) {
            CurlRenderer1.PAGE_LEFT -> {
                //mPageLeft?.setTexture(curlPage.getTexture())
                requestRender()
            }
            CurlRenderer1.PAGE_RIGHT -> {
                //mPageRight?.setTexture(curlPage.getTexture())
                requestRender()
            }
            else -> throw IllegalArgumentException("Unsupported page side")
        }
    }

    fun setCurrentIndex(index: Int) {
        currentIndex = index
        requestRender()
    }

    override fun onDrawFrame() {
        // If we are not animating, exit early.
        if (!mAnimate) {
            return
        }

        val currentTime = System.currentTimeMillis()
        // Check if the animation duration has elapsed.
        if (currentTime >= mAnimationStartTime + mAnimationDurationTime) {
            // Animation has finished.
            if (mAnimationTargetEvent == SET_CURL_TO_RIGHT) {
                // Switch curled page to right.
                val right = mPageCurl
                val curl = mPageRight
                right!!.setRect(mRenderer!!.getPageRect(CurlRenderer1.PAGE_RIGHT))
                right.setFlipTexture(false)
                right.reset()
                mRenderer!!.removeCurlMesh(curl)
                mPageCurl = curl
                mPageRight = right
                // Update current index if we were curling left page.
                if (mCurlState == CURL_LEFT) {
                    --currentIndex
                }
            } else if (mAnimationTargetEvent == SET_CURL_TO_LEFT) {
                // Switch curled page to left.
                val left = mPageCurl
                val curl = mPageLeft
                left!!.setRect(mRenderer!!.getPageRect(CurlRenderer1.PAGE_LEFT))
                left.setFlipTexture(true)
                left.reset()
                mRenderer!!.removeCurlMesh(curl)
                if (!mRenderLeftPage) {
                    mRenderer!!.removeCurlMesh(left)
                }
                mPageCurl = curl
                mPageLeft = left
                // Update current index if we were curling right page.
                if (mCurlState == CURL_RIGHT) {
                    ++currentIndex
                }
            }
            // Reset curl state and animation flag.
            mCurlState = CURL_NONE
            mAnimate = false
            requestRender()
        } else {
            // Animation is still in progress.
            // Interpolate pointer position between source and target based on time.
            val t = 1f - ((currentTime - mAnimationStartTime).toFloat() / mAnimationDurationTime)
            val cubicEase = 1f - (t * t * t * (3 - 2 * t))
            mPointerPos.currentPosition.x = mAnimationSource.x + (mAnimationTarget.x - mAnimationSource.x) * cubicEase
            mPointerPos.currentPosition.y = mAnimationSource.y + (mAnimationTarget.y - mAnimationSource.y) * cubicEase

            // Update curl position based on the interpolated pointer position.
            updateCurlPos(mPointerPos)
        }
    }


    override fun onPageSizeChanged(width: Int, height: Int) {
        mPageBitmapWidth = width
        mPageBitmapHeight = height
        updatePages()
        requestRender()
    }

    public override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) {
        super.onSizeChanged(w, h, ow, oh)
        requestRender()
        if (mSizeChangedObserver != null) {
            mSizeChangedObserver.onSizeChanged(w, h)
        }
    }

    override fun onSurfaceCreated() {
        // In case surface is recreated, let page meshes drop allocated texture
        // ids and ask for new ones. There's no need to set textures here as
        // onPageSizeChanged should be called later on.
        mPageLeft!!.resetTexture()
        mPageRight!!.resetTexture()
        mPageCurl!!.resetTexture()
    }

    override fun onTouch(view: View, me: MotionEvent): Boolean {
        // No dragging during animation at the moment.
        if (mAnimate || mPageProvider == null) {
            return false
        }

        // We need page rects quite extensively so get them for later use.
        val rightRect = mRenderer!!.getPageRect(CurlRenderer1.PAGE_RIGHT)
        val leftRect = mRenderer!!.getPageRect(CurlRenderer1.PAGE_LEFT)

        // Handle scale gestures
        mScaleGestureDetector!!.onTouchEvent(me)

        // Store pointer position.
        mPointerPos.mPos.set(me.x, me.y)
        mRenderer!!.translate(mPointerPos.mPos)
        mPointerPos.mPressure = if (mEnableTouchPressure) me.pressure else 0.8f

        when (me.action) {
            MotionEvent.ACTION_DOWN -> {
                // Once we receive pointer down event its position is mapped to
                // right or left edge of page and that'll be the position from where
                // user is holding the paper to make curl happen.
                mDragStartPos.set(mPointerPos.mPos)

                // Clamp the drag start position within the page boundaries.
                if (mDragStartPos.y > rightRect.top) {
                    mDragStartPos.y = rightRect.top
                } else if (mDragStartPos.y < rightRect.bottom) {
                    mDragStartPos.y = rightRect.bottom
                }

                // Determine the curl state based on touch position and view mode.
                if (mViewMode == SHOW_ONE_PAGE) {
                    if (mDragStartPos.x < width / 2) {
                        if (currentIndex > 0) {
                            updatePage(mPageLeft!!.texturePage, currentIndex - 1)
                            mPageLeft!!.setRect(leftRect)
                        } else {
                            return false
                        }
                        mCurlState = CURL_LEFT
                        if (!mRenderLeftPage) {
                            mPageLeft!!.reset()
                            mRenderer!!.addCurlMesh(mPageLeft)
                        }
                    } else {
                        if (currentIndex < mPageProvider.pageCount - 1) {
                            updatePage(mPageRight!!.texturePage, currentIndex + 1)
                            mPageRight!!.setRect(rightRect)
                        } else {
                            if (!mAllowLastPageCurl) {
                                return false
                            }
                        }
                        mCurlState = CURL_RIGHT
                    }
                } else if (mViewMode == SHOW_TWO_PAGES) {
                    if (mDragStartPos.x < width / 2) {
                        if (currentIndex > 0) {
                            updatePage(mPageLeft!!.texturePage, currentIndex - 1)
                            mPageLeft!!.setRect(leftRect)
                        } else {
                            return false
                        }
                        mCurlState = CURL_LEFT
                        if (!mRenderLeftPage) {
                            mPageLeft!!.reset()
                            mRenderer!!.addCurlMesh(mPageLeft)
                        }
                    } else {
                        if (currentIndex < mPageProvider.pageCount - 1) {
                            updatePage(mPageRight!!.texturePage, currentIndex + 1)
                            mPageRight!!.setRect(rightRect)
                        } else {
                            if (!mAllowLastPageCurl) {
                                return false
                            }
                        }
                        mCurlState = CURL_RIGHT
                    }
                }

                // Update the pointer position and curl position.
                mPointerPos.mPos.set(mDragStartPos)
                updateCurlPos(mPointerPos)
                requestRender()
            }

            MotionEvent.ACTION_MOVE -> {
                // Update the pointer position and curl position during movement.
                mPointerPos.mPos.set(me.x, me.y)
                mRenderer!!.translate(mPointerPos.mPos)
                updateCurlPos(mPointerPos)
                requestRender()
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                // Start animation to curl the page based on the current curl state.
                mAnimate = true
                mAnimationStartTime = System.currentTimeMillis()
                mAnimationSource.set(mPointerPos.mPos)
                if (mCurlState == CURL_LEFT) {
                    mAnimationTarget.x = if (mPointerPos.mPos.x > width / 2) {
                        width * 1.5f
                    } else {
                        width * 0.1f
                    }
                    mAnimationTargetEvent = if (mAnimationTarget.x > 0) SET_CURL_TO_RIGHT else SET_CURL_TO_LEFT
                } else if (mCurlState == CURL_RIGHT) {
                    mAnimationTarget.x = if (mPointerPos.mPos.x < width / 2) {
                        -width * 1.5f
                    } else {
                        width * 0.9f
                    }
                    mAnimationTargetEvent = if (mAnimationTarget.x < 0) SET_CURL_TO_LEFT else SET_CURL_TO_RIGHT
                }
                mAnimationTarget.y = mPointerPos.mPos.y + height * 0.2f
            }
        }
        return true
    }



    /**
     * Set maximum scale factor.
     */
    fun setMaxScaleFactor(maxScaleFactor: Float) {
        this.maxScaleFactor = maxScaleFactor
    }

    /**
     * Set minimum scale factor.
     */
    fun setMinScaleFactor(minScaleFactor: Float) {
        this.minScaleFactor = minScaleFactor
    }

    /**
     * Set view mode.
     */
    fun setViewMode(viewMode: Int) {
        if (viewMode == SHOW_ONE_PAGE || viewMode == SHOW_TWO_PAGES) {
            mViewMode = viewMode
        } else {
            throw IllegalArgumentException("Invalid view mode")
        }
    }

    /**
     * Updates curl position.
     */
    private fun updateCurlPos(pointerPos: PointerPosition) {
        // Common calculations for both curl states.
        mCurlDir.x = pointerPos.currentPosition.x - mDragStartPos.x
        mCurlDir.y = pointerPos.currentPosition.y - mDragStartPos.y
        val dist = sqrt((mCurlDir.x * mCurlDir.x + mCurlDir.y * mCurlDir.y).toDouble()).toFloat()

        // Ensure curling within a maximum radius.
        if (dist > radius) {
            val translate = (radius / dist).toDouble()
            mCurlPos.x -= (mCurlDir.x * (1f - translate)).toFloat()
            mCurlPos.y -= (mCurlDir.y * (1f - translate)).toFloat()
            mCurlDir.x *= translate.toFloat()
            mCurlDir.y *= translate.toFloat()
        }

        // Set curl position and direction based on curl state.
        if (mCurlState == CURL_LEFT) {
            val pageRect = mRenderer!!.getPageRect(CurlRenderer1.PAGE_LEFT)
            setCurlPos(mCurlPos, mCurlDir, radius)
        } else if (mCurlState == CURL_RIGHT) {
            val pageRect = mRenderer!!.getPageRect(CurlRenderer1.PAGE_RIGHT)
            setCurlPos(mCurlPos, mCurlDir, radius)
        }
    }



    /**
     * Updates given CurlPage via PageProvider for page located at index.
     */
    private fun updatePage(page: CurlPage, index: Int) {
        page.reset()
        mPageProvider!!.updatePage(page, index)
    }

    /**
     * Updates bitmaps for page meshes.
     */
    private fun updatePages() {
        if (mPageProvider == null || mPageBitmapWidth <= 0 || mPageBitmapHeight <= 0) {
            return
        }
        mRenderer!!.removeCurlMesh(mPageLeft)
        mRenderer!!.removeCurlMesh(mPageRight)
        mRenderer!!.removeCurlMesh(mPageCurl)
        val leftIdx = currentIndex - 1
        val rightIdx = currentIndex
        if (leftIdx >= 0) {
            updatePage(mPageLeft!!.texturePage, leftIdx)
            mPageLeft!!.setFlipTexture(true)
            mPageLeft!!.setRect(mRenderer!!.getPageRect(CurlRenderer1.PAGE_LEFT))
            mPageLeft!!.reset()
            if (mRenderLeftPage) {
                mRenderer!!.addCurlMesh(mPageLeft)
            }
        }
        if (rightIdx < mPageProvider?.pageCount ?: 0) {
            updatePage(mPageRight!!.texturePage, rightIdx)
            mPageRight!!.setFlipTexture(false)
            mPageRight!!.setRect(mRenderer!!.getPageRect(CurlRenderer1.PAGE_RIGHT))
            mPageRight!!.reset()
            mRenderer!!.addCurlMesh(mPageRight)
        }
        mPageCurl!!.reset()
        mRenderer!!.addCurlMesh(mPageCurl)
    }


    companion object {
        // Curl state. We are flipping none, left or right page.
        private const val CURL_LEFT = 1
        private const val CURL_NONE = 0
        private const val CURL_RIGHT = 2

        // Constants for mAnimationTargetEvent.
        private const val SET_CURL_TO_LEFT = 1
        private const val SET_CURL_TO_RIGHT = 2

        // Shows one page at the center of view.
        const val SHOW_ONE_PAGE: Int = 1

        // Shows two pages side by side.
        const val SHOW_TWO_PAGES: Int = 2
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // Access outer class members directly if needed
            return true
        }
    }

    private fun setCurlPos(curlPos: PointF, curlDir: PointF, radius: Double) {
        mPageCurl?.curl(curlPos, curlDir, radius.toDouble())
    }
}
