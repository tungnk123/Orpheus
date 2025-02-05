package com.tungnk123.orpheus.helper

import com.tungnk123.orpheus.utils.extensions.subListNonStrict
import me.xdrop.fuzzywuzzy.FuzzySearch

class FuzzySearcher<T>(private val options: List<FuzzySearchOption<T>>) {
    fun search(
        terms: String,
        entities: List<T>,
        maxLength: Int? = null
    ): List<T> =
        entities.map { compare(terms, it) }
            .sortedByDescending { it.score }
            .let { if (maxLength != null) it.subListNonStrict(maxLength) else it }
            .map { it.entity }

    private fun compare(terms: String, entity: T) = FuzzyResultEntity(
        options.maxOfOrNull {
            it.match(FuzzySearchComparator(terms), entity)?.times(it.weight) ?: 0
        } ?: 0,
        entity
    )
}

object FuzzySearchHelper {
    fun compare(input: String, against: String) =
        FuzzySearch.tokenSetPartialRatio(input.normalize(), against.normalize())

    private val symbolsRegex = Regex("""[~${'$'}&+,:;=?@#|'"<>.^*()\[\]%!\-_/\\]+""")
    private fun String.normalize() =
        lowercase().replace(symbolsRegex, "").replace("\\s+".toRegex(), " ")
}

class FuzzySearchComparator(private val input: String) {
    fun compareString(value: String) = FuzzySearchHelper.compare(input, value)
    fun compareCollection(values: Collection<String>) = values.maxOfOrNull { compareString(it) }
}

data class FuzzySearchOption<T>(val match: FuzzySearchComparator.(T) -> Int?, val weight: Int = 1)
data class FuzzyResultEntity<T>(val score: Int, val entity: T)
