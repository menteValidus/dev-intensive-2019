package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Utils.convertDpToPixel
import ru.skillbranch.devintensive.utils.Utils.convertPixelsToDp
import kotlin.math.min
import kotlin.math.roundToInt

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    companion object {
        /**
         * Стандартный цвет рамки.
         */
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

    private var borderWidth = convertDpToPixel(2F, context).toInt()
    private var borderColor = DEFAULT_BORDER_COLOR
    private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintBitmap = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var bitmap: Bitmap


    init {

        if (attrs != null) {
            val a =
                context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)

            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, -1)
            borderColor =
                a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)

            a.recycle()
        }

    }


    /**
     * Метод, вызываемый при отрисовке.
     */
    override fun onDraw(canvas: Canvas) {
        pathDrawMethod(canvas)
    }


    /**
     * Метод рисования. Отображение обрезанного Drawable с границей.
     */
    private fun  pathDrawMethod(canvas: Canvas) {
        if (width == 0 || height == 0) return

        bitmap = getBitmapFromDrawable(drawable)!!

        val halfWidth = width / 2F
        val halfHeight = height / 2F
        val radius = min(halfHeight, halfWidth)

        val path = Path()

        path.addCircle(halfWidth, halfHeight, radius, Path.Direction.CCW)
        canvas.clipPath(path)

        canvas.drawBitmap(bitmap, 0F, 0F, paintBitmap)

        if (borderWidth > 0) {
            paintBorder.color = borderColor
            paintBorder.style = Paint.Style.STROKE
            paintBorder.strokeWidth = borderWidth.toFloat()
            canvas.drawCircle(halfWidth, halfHeight, radius - borderWidth / 2, paintBorder)
        }
    }

    /**
     * Метод извлечения Bitmap из Drawable.
     */
    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {

        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }


    @Dimension(unit = DP)
    fun getBorderWidth() = convertPixelsToDp(borderWidth.toFloat(), context).roundToInt()

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = convertDpToPixel(dp.toFloat(), context).toInt()
        invalidate()
    }

    fun getBorderColor(): Int {
        return borderColor;
    }

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        val color = ContextCompat.getColor(App.applicationContext(), colorId)
        borderColor = color
        this.invalidate()
    }


}
