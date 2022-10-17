package com.github.lorenzomilicia.resourcepartitioninglib.itemreader

import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.item.file.FlatFileItemReader
import kotlin.properties.Delegates

open class MyFileReader<T>: FlatFileItemReader<T>(), StepExecutionListener {

	private var startingCount by Delegates.notNull<Int>()
	private var finishingCount by Delegates.notNull<Int>()

	override fun doOpen() {
		super.doOpen()
		currentItemCount = startingCount
		setMaxItemCount(finishingCount + 1)
	}

	override fun beforeStep(stepExecution: StepExecution) {
		startingCount = stepExecution.executionContext.getInt("startingLine")
		finishingCount = stepExecution.executionContext.getInt("finishingLine")
	}

	override fun afterStep(stepExecution: StepExecution): ExitStatus {
		return ExitStatus.COMPLETED
	}

	override fun setLinesToSkip(linesToSkip: Int) {
		log.warn("Lines to skip is ignored, define the lines to skip in the partitioner")
	}

	private companion object {
		private val log = LoggerFactory.getLogger(MyFileReader::class.java)
	}
}