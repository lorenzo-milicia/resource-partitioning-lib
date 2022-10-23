package com.github.lorenzomilicia.resourcepartitioninglib.partitioners

import com.github.lorenzomilicia.resourcepartitioninglib.utils.countLines
import com.github.lorenzomilicia.resourcepartitioninglib.utils.divideIntoBins
import org.springframework.batch.core.partition.support.Partitioner
import org.springframework.batch.item.ExecutionContext
import org.springframework.core.io.Resource

open class MultiResourceChunkedPartitioner(
	private val resources: List<Resource>,
): Partitioner {

	var filekeyName: String = FILE_KEY_NAME
	var startingLinekeyName: String = STARTING_LINE
	var endingLinekeyName: String = FINISHING_LINE

	var partitionSize: Int? = null

	private var linesToSkip: Int = 0

	override fun partition(gridSize: Int): MutableMap<String, ExecutionContext> =
		resources
			.associateWith { it.countLines() }
			.filterValues { it > 0 }
			.map { (resource, lines) -> resource to divideIntoBins(linesToSkip, lines - 1, partitionSize) }
			.flatMap { (resource, ranges) -> ranges.map { resource to it } }
			.mapIndexed { index, (resource, range) ->
				val executionContext = ExecutionContext()
				executionContext.put(filekeyName, resource.url.toExternalForm())
				executionContext.put(startingLinekeyName, range.first)
				executionContext.put(endingLinekeyName, range.last)
				"$PARTITION_KEY$index" to executionContext
			}
			.toMap()
			.toMutableMap()

	fun setLinesToSkip(linesToSkip: Int) {
		this.linesToSkip = linesToSkip
	}

	companion object {

		private const val PARTITION_KEY = "partition"
		private const val FILE_KEY_NAME = "fileName"
		private const val STARTING_LINE = "startingLineIndex"
		private const val FINISHING_LINE = "endingLineIndex"
	}

}