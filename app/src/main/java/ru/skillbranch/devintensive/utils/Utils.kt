package ru.skillbranch.devintensive.utils

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
        /*val transLetter: String

        when (letter) {
            "а" -> transLetter = "a"
            "б" -> transLetter = "b"
            "в" -> transLetter = "v"
            "г" -> transLetter = "g"
            "д" -> transLetter = "d"
            "е" -> transLetter = "e"
            "ё" -> transLetter = "e"
            "ж" -> transLetter = "zh"
            "з" -> transLetter = "z"
            "и" -> transLetter = "i"
            "й" -> transLetter = "i"
            "к" -> transLetter = "k"
            "л" -> transLetter = "l"
            "м" -> transLetter = "m"
            "н" -> transLetter = "n"
            "о" -> transLetter = "o"
            "п" -> transLetter = "p"
            "р" -> transLetter = "r"
            "с" -> transLetter = "s"
            "т" -> transLetter = "t"
            "у" -> transLetter = "u"
            "ф" -> transLetter = "f"
            "х" -> transLetter = "h"
            "ц" -> transLetter = "c"
            "ч" -> transLetter = "ch"
            "ш" -> transLetter = "sh"
            "щ" -> transLetter = "sh'"
            "ъ" -> transLetter = ""
            "ы" -> transLetter = "i"
            "ь" -> transLetter = ""
            "э" -> transLetter = "e"
            "ю" -> transLetter = "yu"
            "я" -> transLetter = "ya"
            else -> transLetter = letter
        }*/

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
}