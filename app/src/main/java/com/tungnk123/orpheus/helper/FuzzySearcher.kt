package com.tungnk123.orpheus.helper

import com.tungnk123.orpheus.utils.extensions.subListNonStrict
import me.xdrop.fuzzywuzzy.FuzzySearch

class FuzzySearcher<T>(private val options: List<FuzzySearchOption<T>>) {
    fun search(
        terms: String,
        entities: List<T>,
        maxLength: Int? = null,
    ): List<FuzzyResultEntity<T>> {
        return entities.map { compare(terms, it) }
            .sortedByDescending { it.score }
            .let { results -> maxLength?.let { results.subListNonStrict(maxLength) } ?: results }
    }

    private fun compare(terms: String, entity: T): FuzzyResultEntity<T> {
        val comparator by lazy { FuzzySearchComparator(terms) }
        val score = options.mapNotNull { it.match(comparator, entity)?.let { s -> s * it.weight } }
            .maxOrNull() ?: 0

        return FuzzyResultEntity(score, entity)
    }
}

object FuzzySearchHelper {
    fun compare(input: String, against: String): Int =
        FuzzySearch.tokenSetPartialRatio(normalizeTerms(input), normalizeTerms(against))

    private val symbolsRegex = Regex("""[~${'$'}&+,:;=?@#|'"<>.^*()\[\]%!\-_/\\]+""")
    private val whitespaceRegex = Regex("""\s+""")

    private fun normalizeTerms(terms: String): String = terms.lowercase()
        .replace(symbolsRegex, "")
        .replace(whitespaceRegex, " ")
}

class FuzzySearchComparator(private val input: String) {
    fun compareString(value: String): Int = FuzzySearchHelper.compare(input, value)

    fun compareCollection(values: Collection<String>): Int? =
        values.maxOfOrNull { compareString(it) }
}

data class FuzzySearchOption<T>(
    val match: FuzzySearchComparator.(T) -> Int?,
    val weight: Int = 1,
)

data class FuzzyResultEntity<T>(
    val score: Int,
    val entity: T,
)