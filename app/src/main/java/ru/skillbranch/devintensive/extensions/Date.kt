package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

const val JUST_NOW = 0L
const val FEW_SECONDS_AGO = SECOND
const val MINUTE_AGO = 45 * SECOND
const val MINUTES_AGO = 75 * SECOND
const val HOUR_AGO = 45 * MINUTE
const val HOURS_AGO = 75 * MINUTE
const val DAY_AGO = 22 * HOUR
const val DAYS_AGO = 26 * HOUR
const val MORE_THAN_YEAR_AGO = 360 * DAY

fun Date.format(pattern: String="HH:mm:ss dd.MM.yy"):String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.add(value:Int, units: TimeUnits = TimeUnits.SECOND) : Date{
    var time = this.time

    time += when(units){
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.humanizeDiff(date:Date = Date()): String {
    val diff = date.time - this.time

    if (diff >= 0) {

        when (diff) {
            in JUST_NOW..FEW_SECONDS_AGO ->
                return "только что"
            in FEW_SECONDS_AGO..MINUTE_AGO ->
                return "несколько секунд назад"
            in MINUTE_AGO..MINUTES_AGO ->
                return "минуту назад"
            in MINUTES_AGO..HOUR_AGO ->{
                val digitForm: String
                val lastNumber = diff / MINUTE % 10

                when {
                    diff in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "минут"
                    lastNumber in 2..4 -> digitForm = "минуты"
                    else -> digitForm = "минута"
                }

                return "${diff / MINUTE} $digitForm назад"
            }
            in HOUR_AGO..HOURS_AGO ->
                return "час назад"
            in HOURS_AGO..DAY_AGO -> {
                val digitForm: String
                val lastNumber = diff / HOUR % 10

                when {
                    diff in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "часов"
                    lastNumber in 2..4 -> digitForm = "часа"
                    else -> digitForm = "час"
                }

                return "${diff / HOUR} $digitForm назад"
            }
            in DAY_AGO..DAYS_AGO ->
                return "день назад"
            in DAYS_AGO..MORE_THAN_YEAR_AGO -> {
                val digitForm: String
                val lastNumber = diff / DAY % 10

                when {
                    diff in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "дней"
                    lastNumber in 2..4 -> digitForm = "дня"
                    else -> digitForm = "день"
                }

                return "${diff / DAY} $digitForm назад"
            }
            else ->
                return "более года назад"
        }

    } else {

        when (-diff) {
            in JUST_NOW..FEW_SECONDS_AGO ->
                return "только что"
            in FEW_SECONDS_AGO..MINUTE_AGO ->
                return "через несколько секунд"
            in MINUTE_AGO..MINUTES_AGO ->
                return "через минуту"
            in MINUTES_AGO..HOUR_AGO -> {
                val digitForm: String
                val lastNumber = -diff / MINUTE % 10

                when {
                    -diff in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "минут"
                    lastNumber in 2..4 -> digitForm = "минуты"
                    else -> digitForm = "минута"
                }

                return "через ${-diff / MINUTE} " + digitForm
            }
            in HOUR_AGO..HOURS_AGO ->
                return "через час"
            in HOURS_AGO..DAY_AGO -> {
                val digitForm: String
                val lastNumber = -diff / HOUR % 10

                when {
                    -diff in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "часов"
                    lastNumber in 2..4 -> digitForm = "часа"
                    else -> digitForm = "час"
                }

                return "через ${-diff / HOUR} " + digitForm
            }
            in DAY_AGO..DAYS_AGO ->
                return "через день"
            in DAYS_AGO..MORE_THAN_YEAR_AGO -> {
                val digitForm: String
                val lastNumber = -diff / DAY % 10

                when {
                    -diff in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "дней"
                    lastNumber in 2..4 -> digitForm = "дня"
                    else -> digitForm = "день"
                }

                return "через ${-diff / DAY} " + digitForm
            }
            else ->
                return "более чем через год"
        }
    }

}

enum class TimeUnits{
    SECOND {
        override fun plural(number: Long): String {
            val digitForm: String
            val lastNumber = number % 10

            when {
                number in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "секунд"
                lastNumber in 2..4 -> digitForm = "секунды"
                else -> digitForm = "секунда"
            }

            return "$number $digitForm"
        }
    },
    MINUTE {
        override fun plural(number: Long): String {
            val digitForm: String
            val lastNumber = number % 10

            when {
                number in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "минут"
                lastNumber in 2..4 -> digitForm = "минуты"
                else -> digitForm = "минута"
            }

            return "$number $digitForm"
        }
    },
    HOUR {
        override fun plural(number: Long): String {
            val digitForm: String
            val lastNumber = number % 10

            when {
                number in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "часов"
                lastNumber in 2..4 -> digitForm = "часа"
                else -> digitForm = "час"
            }

            return "$number $digitForm"        }
    },
    DAY {
        override fun plural(number: Long): String {
            val digitForm: String
            val lastNumber = number % 10

            when {
                number in 11..19 || lastNumber==0L || lastNumber in 5..9 -> digitForm = "дней"
                lastNumber in 2..4 -> digitForm = "дня"
                else -> digitForm = "день"
            }

            return "$number $digitForm"        }
    };

    abstract fun plural(number: Long): String
}