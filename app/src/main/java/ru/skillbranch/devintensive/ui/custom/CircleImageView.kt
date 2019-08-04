package ru.skillbranch.devintensive.ui.custom

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.R
import android.util.DisplayMetrics

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private var borderWidth = DEFAULT_BORDER_WIDTH
    private var borderColor = DEFAULT_BORDER_COLOR

    private lateinit var bitmapShader: Shader
    private var shaderMatrix: Matrix

    private var bitmapDrawBounds: RectF
    private var strokeBounds: RectF

    private var bitmap: Bitmap? = null

    private var bitmapPaint: Paint
    private var strokePaint: Paint

    private var initialised = false

    init {

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)

            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, -1)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)

            a.recycle()
        }

        shaderMatrix = Matrix()
        bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokeBounds = RectF()
        bitmapDrawBounds = RectF()
        strokePaint.color = borderColor
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = borderWidth.toFloat()

        initialised = true

        setupBitmap()
    }

    private fun setupBitmap() {

        if (!initialised) {
            return
        }

        bitmap = getBitmapFromDrawable(drawable)

        if (bitmap == null) {
            return
        }

        bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        bitmapPaint.shader = bitmapShader

        updateBitmapSize()
    }

    private fun updateBitmapSize() {
        val dx: Float
        val dy: Float

        val scale: Float

        if (bitmap!!.width < bitmap!!.height) {
            scale = bitmapDrawBounds.width() / bitmap!!.width
            dx = bitmapDrawBounds.left
            dy = bitmapDrawBounds.top - (bitmap!!.height * scale / 2f) + (bitmapDrawBounds.width() / 2f)
        } else {
            scale = bitmapDrawBounds.height() / bitmap!!.height
            dx = bitmapDrawBounds.left - (bitmap!!.width * scale / 2f) + (bitmapDrawBounds.width() / 2f)
            dy = bitmapDrawBounds.top
        }

        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(dx, dy)
        bitmapShader.setLocalMatrix(shaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {

        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val halfStrokeWidth = strokePaint.strokeWidth / 2f
        updateCircleDrawBounds(bitmapDrawBounds)
        strokeBounds.set(bitmapDrawBounds)
        strokeBounds.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = CircleImageViewOutlineProvider(strokeBounds)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        drawBitmap(canvas)
        drawBorder(canvas)
    }


    private fun drawBorder(canvas: Canvas?) {
        if (strokePaint.strokeWidth > 0f) {
            canvas?.drawOval(strokeBounds, strokePaint)
        }
    }


    private fun drawBitmap(canvas: Canvas?) {
        canvas?.drawOval(bitmapDrawBounds, bitmapPaint)
    }

    protected fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingBottom - paddingTop).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()

        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = Math.min(contentWidth, contentHeight);
        bounds.set(left, top, left + diameter, top + diameter)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class CircleImageViewOutlineProvider constructor(rectF: RectF) : ViewOutlineProvider() {

        private lateinit var rect: Rect

        init {
            rect = Rect(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        }

        override fun getOutline(view: View?, outline: Outline?) {
            outline?.setOval(rect)
        }

    }

    @Dimension
    fun getBorderWidth(): Int {
        return Math.round(convertPixelsToDp(strokePaint.strokeWidth, context))
    }

    fun setBorderWidth(@Dimension dp: Int) {
        strokePaint.strokeWidth = convertDpToPixel(dp.toFloat(), context)
        invalidate()
    }

    fun getBorderColor(): Int {
        return strokePaint.color;
    }

    fun setBorderColor(hex: String) {
        strokePaint.color = Color.parseColor(hex)
        invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        val color = ContextCompat.getColor(context, colorId)
        strokePaint.color = color
        invalidate()
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

}
