package nicestring

fun String.isNice(): Boolean {

    val noForbiddenString = setOf("bu", "ba", "be").none { this.contains(it) }
    val threeVowels = this.count { it in "aeiou" } >= 3
    val doubleLetter = this.zipWithNext().any { it.first == it.second }

    return listOf(noForbiddenString, threeVowels, doubleLetter).count { it } >= 2
}