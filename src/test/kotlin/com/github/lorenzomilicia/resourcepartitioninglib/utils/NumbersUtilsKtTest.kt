package com.github.lorenzomilicia.resourcepartitioninglib.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class NumbersUtilsKtTest {

	@Test
	internal fun `Diving into ranges`() {
		val start = 0L
		val end = 23L
		val binSize = 5L

		val bins = divideIntoBins(start, end, binSize)

		val expectedResult = listOf(
			0L until 5,
			5L until 10,
			10L until 15,
			15L until 20,
			20L..23,
		)

		assertEquals(expectedResult, bins)
	}

	@Test
	internal fun `Dividing into exact ranges`() {
		val start = 0L
		val end = 20L
		val binSize = 5L

		val bins = divideIntoBins(start, end, binSize)

		val expectedResult = listOf(
			0L until 5,
			5L until 10,
			10L until 15,
			15L..20,
		)

		assertEquals(expectedResult, bins)
	}

	@Test
	internal fun `Diving when last bin is of size one`() {
		val start = 0L
		val end = 21L
		val binSize = 5L

		val bins = divideIntoBins(start, end, binSize)

		val expectedResult = listOf(
			0L until 5,
			5L until 10,
			10L until 15,
			15L until 20,
			20L..21,
		)

		assertEquals(expectedResult, bins)

	}
}