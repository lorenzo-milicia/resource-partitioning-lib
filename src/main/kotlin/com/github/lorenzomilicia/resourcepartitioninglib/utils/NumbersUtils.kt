package com.github.lorenzomilicia.resourcepartitioninglib.utils

internal fun divideIntoBins(
	start: Int,
	end: Int,
	binSize: Int?,
): List<IntRange> {
	if (start < 0 || end < 0) throw IllegalArgumentException("Number should be positive")
	if (end < start) throw IllegalArgumentException("End cannot be lesser than start")
	if (binSize == null) return listOf(start..end)
	val ranges = mutableListOf<IntRange>()
	var iterator = start
	do {
		val isLast = iterator + binSize >= end
		val rangeStart = iterator
		val rangeEnd =
			if (isLast) end else iterator + binSize

		val range =
			if (!isLast) rangeStart until rangeEnd else rangeStart..rangeEnd
		ranges.add(range)
		iterator = rangeEnd
	} while (rangeEnd < end)
	return ranges.toList()
}