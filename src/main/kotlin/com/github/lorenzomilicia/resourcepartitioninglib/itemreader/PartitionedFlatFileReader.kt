package com.github.lorenzomilicia.resourcepartitioninglib.itemreader

import org.slf4j.LoggerFactory
import org.springframework.batch.item.file.FlatFileItemReader

open class PartitionedFlatFileReader<T>: FlatFileItemReader<T>() {

	private var startingLineIndex: Int? = null
	private var endingLineIndex: Int? = null

	override fun doOpen() {
		startingLineIndex?.let { currentItemCount = it }
		endingLineIndex?.let { setMaxItemCount(it + 1) }
		super.doOpen()
	}

	fun setLinesToRead(startingLineIndex: Int, endingLineIndex: Int) {
		require(
			startingLineIndex >= 0
		) { "Starting line should be non negative" }
		require(
			endingLineIndex >= 0
		) { "Ending line should be non negative" }
		require(endingLineIndex >= startingLineIndex) { "Ending line should not be lesser than starting line" }

		this.startingLineIndex = startingLineIndex
		this.endingLineIndex = endingLineIndex
	}

	@Deprecated(
		"Use setLinesToRead to specify the starting line.",
		ReplaceWith("setLinesToRead"),
		DeprecationLevel.WARNING
	)
	override fun setLinesToSkip(linesToSkip: Int) {
		log.warn("setLinesToSkip should not be used, use setLinesToRead to specify the starting line")
		super.setLinesToSkip(linesToSkip)
	}

	private companion object {

		private val log =
			LoggerFactory.getLogger(PartitionedFlatFileReader::class.java)
	}
}