package ru.skillbranch.devintensive.extensions

fun String.truncate(truncatingNumber: Int = 16): String {
    val truncatingLength: Int

    if (this.length > truncatingNumber) {
        truncatingLength = truncatingNumber
    } else {
        truncatingLength = this.length
    }

    val truncatedString: String = this.substring(0, truncatingLength)
    var truncatedLength = truncatingLength

    for (i in truncatingLength - 1 downTo 0) {

        if (truncatedString[i].isWhitespace()) {
            truncatedLength--
        } else {
            break
        }

    }

    return "${truncatedString.substring(0, truncatedLength) +
            if (isLetterAfter(truncatingLength)) "..." else ""}"
}

private fun String.isLetterAfter(numberOfLetter: Int): Boolean {
    val thisString: String = this.substring(numberOfLetter - 1, this.length - 1)

    for (character in thisString) {

        if (!character.isWhitespace()) {
            return true
        }

    }

    return false
}

fun String.stripHtml(): String {
    return Regex("(\\<.*?>)|&amp|&lt|&gt|&quot|&apos|\\?").replace(this, "")
        .replace("\\s+".toRegex(), " ")
}