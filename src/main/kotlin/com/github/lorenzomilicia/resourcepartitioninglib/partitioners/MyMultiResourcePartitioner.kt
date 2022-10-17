package com.github.lorenzomilicia.resourcepartitioninglib.partitioners

import com.github.lorenzomilicia.resourcepartitioninglib.utils.countLines
import com.github.lorenzomilicia.resourcepartitioninglib.utils.divideIntoBins
import org.springframework.batch.core.partition.support.Partitioner
import org.springframework.batch.item.ExecutionContext
import org.springframework.core.io.Resource

open class MyMultiResourcePartitioner(
	private val resources: List<Resource>,
): Partitioner {

	var filekeyName: String = FILE_KEY_NAME
	var startingLinekeyName: String = STARTING_LINE
	var finishingLinekeyName: String = FINISHING_LINE

	var partitionSize: Int? = null

	private var linesToSkip: Int = 0

	override fun partition(gridSize: Int): MutableMap<String, ExecutionContext> =
		resources
			.map { it to divideIntoBins(linesToSkip, it.countLines(), partitionSize) }
			.flatMap { (file, ranges) -> ranges.map { file to it } }
			.mapIndexed { index, (resource, range) ->
				val executionContext = ExecutionContext()
				executionContext.put(filekeyName, resource.url.toExternalForm())
				executionContext.put(startingLinekeyName, range.first)
				executionContext.put(finishingLinekeyName, range.last)
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
		private const val STARTING_LINE = "startingLine"
		private const val FINISHING_LINE = "finishingLine"
	}

}