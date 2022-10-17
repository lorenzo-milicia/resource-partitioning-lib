package com.github.lorenzomilicia.resourcepartitioninglib.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class NumbersUtilsKtTest {

	@Test
	internal fun `Diving into ranges`() {
		val start = 0
		val end = 23
		val binSize = 5

		val bins = divideIntoBins(start, end, binSize)

		val expectedResult = listOf(
			0 until 5,
			5 until 10,
			10 until 15,
			15 until 20,
			20..23,
		)

		assertEquals(expectedResult, bins)
	}

	@Test
	internal fun `Dividing into exact ranges`() {
		val start = 0
		val end = 20
		val binSize = 5

		val bins = divideIntoBins(start, end, binSize)

		val expectedResult = listOf(
			0 until 5,
			5 until 10,
			10 until 15,
			15..20,
		)

		assertEquals(expectedResult, bins)
	}

	@Test
	internal fun `Diving when last bin is of size one`() {
		val start = 0
		val end = 21
		val binSize = 5

		val bins = divideIntoBins(start, end, binSize)

		val expectedResult = listOf(
			0 until 5,
			5 until 10,
			10 until 15,
			15 until 20,
			20..21,
		)

		assertEquals(expectedResult, bins)

	}
}