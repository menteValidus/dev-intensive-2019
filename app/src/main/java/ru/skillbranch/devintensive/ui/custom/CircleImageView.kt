package ru.skillbranch.devintensive.ui.custom

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.R


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
    private lateinit var shaderMatrix: Matrix

    private lateinit var bitmapDrawBounds: RectF
    private lateinit var strokeBounds: RectF

    private var bitmap: Bitmap? = null

    private lateinit var bitmapPaint: Paint
    private lateinit var strokePaint: Paint

    private var initialised = false

    init {

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, 0, 0)

            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
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
        var dx: Float
        var dy: Float

        var scale: Float

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
        var canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        updateCircleDrawBounds(bitmapDrawBounds)
    }

    override fun onDraw(canvas: Canvas?) {
        drawBitmap(canvas)
        drawBorder(canvas)
        super.onDraw(canvas)

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

    @Dimension
    fun getBorderWidth(): Int {
        return strokePaint.strokeWidth.toInt()
    }

    fun setBorderWidth(@Dimension dp: Int) {
        strokePaint.strokeWidth = dp.toFloat()
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
}
