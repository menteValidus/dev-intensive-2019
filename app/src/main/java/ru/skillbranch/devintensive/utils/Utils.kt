package ru.skillbranch.devintensive.utils

import android.content.Context
import android.graphics.*
import java.net.MalformedURLException
import java.net.URL
import android.graphics.Paint.Align
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.ColorInt


object Utils {

    private val transliterationMap: Map<Char, String> = hashMapOf(
        'а' to "a",
        'б' to "b",
        'в' to "v",
        'г' to "g",
        'д' to "d",
        'е' to "e",
        'ё' to "e",
        'ж' to "zh",
        'з' to "z",
        'и' to "i",
        'й' to "i",
        'к' to "k",
        'л' to "l",
        'м' to "m",
        'н' to "n",
        'о' to "o",
        'п' to "p",
        'р' to "r",
        'с' to "s",
        'т' to "t",
        'у' to "u",
        'ф' to "f",
        'х' to "h",
        'ц' to "c",
        'ч' to "ch",
        'ш' to "sh",
        'щ' to "sh'",
        'ъ' to "",
        'ы' to "i",
        'ь' to "",
        'э' to "e",
        'ю' to "yu",
        'я' to "ya"
    )
    private val excludePaths = listOf(
                "enterprise",
                "features",
                "topics",
                "collections",
                "trending",
                "events",
                "marketplace",
                "pricing",
                "nonprofit",
                "customer-stories",
                "security",
                "login",
                "join"
    )

    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val firstName: String?
        val lastName: String?

        val parts: List<String>? = fullName?.trim()?.replace("\\s+".toRegex(), " ")?.split(" ")

        firstName = if (parts?.getOrNull(0).isNullOrBlank()) null else parts?.getOrNull(0)
        lastName = if (parts?.getOrNull(1).isNullOrBlank()) null else parts?.getOrNull(1)

        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " "): String {
        var transFullName = String()


        for (character in payload.trim()) {

            if (character.isWhitespace()) {
                transFullName += divider
            } else {
                transFullName += convertLetter(character.toLowerCase(), character.isUpperCase())
            }

        }

        return transFullName
    }

    fun convertLetter(letter: Char, isUpperCase: Boolean): String? {

        if (letter.isLetter()) {
            val transliteratedLetter = transliterationMap[letter]

            if (transliteratedLetter != null) {

                if (isUpperCase) {
                    return transliterationMap[letter]?.capitalize()
                } else {
                    return transliterationMap[letter]
                }

            } else {

                if (isUpperCase) {
                    return letter.toString().capitalize()
                } else {
                    return letter.toString()
                }

            }

        } else {
            return letter.toString()
        }

    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val firstInitial = firstName?.firstOrNull()?.toString()
        val lastInitial = lastName?.firstOrNull()?.toString()

        if (firstInitial.isNullOrBlank()) {

            if (lastInitial.isNullOrBlank()) {
                return null
            } else {
                return "${lastInitial.toUpperCase()}"
            }

        } else {
            if (lastInitial.isNullOrBlank()) {
                return "${firstInitial.toUpperCase()}"
            } else {
                return "${firstInitial.toUpperCase()}${lastInitial.toUpperCase()}"
            }
        }

    }

    fun checkGithubUrl(sUrl: String): Boolean {

        if (sUrl.isEmpty()) {
            return true
        }

        var u: URL

        try {
            u = URL(sUrl)
        } catch (ex: MalformedURLException) {
            u = URL("https://$sUrl")
        }

        if (u.protocol == "https" && (u.host == "github.com" || u.host == "www.github.com") ) {
            val parts = u.file.split("/")

            if (parts.count() == 2 && !parts[1].isNullOrEmpty() && !excludePaths.contains(parts[1])) {

                for (c in parts[1]) {

                    if (c.isWhitespace() || c == '_') {
                        return false
                    }

                }

                return true
            } else if (parts.count() == 3 && !parts[1].isNullOrEmpty() && !excludePaths.contains(parts[1]) && parts[2].isNullOrEmpty()) {

                for (c in parts[1]) {

                    if (c.isWhitespace() || c == '_') {
                        return false
                    }

                }

                return true
            }

        }

        return false
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertSpToPixel(sp: Float, context: Context): Float {
        return sp * context.resources.displayMetrics.scaledDensity
    }

    /**
     * Метод, получающий Bitmap из Drawable.
     */


    fun textAsBitmap(width: Int,
                     height: Int,
                     text: String,
                     textSize: Float,
                     @ColorInt textColor: Int,
                     @ColorInt bgColor: Int): Bitmap {
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Align.CENTER

        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawColor(bgColor)

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val bgBounds = RectF()
        bgBounds.set(0f, 0f, width.toFloat(), height.toFloat())

        val textBottom = bgBounds.centerY() - textBounds.centerY()
        canvas.drawText(text, bgBounds.centerX(), textBottom, paint)

        return image
    }
}