package com.github.lorenzomilicia.resourcepartitioninglib.itemreader

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.batch.item.ExecutionContext
import org.springframework.core.io.FileSystemResource
import java.io.File
import kotlin.test.assertEquals

internal class PartitionedFlatFileReaderTest {

	@field:TempDir
	lateinit var tempFolder: File

	@Test
	internal fun `Reading a file`() {
		val resource = FileSystemResource(testFile.also { it.createNewFile() })
		val reader = PartitionedFlatFileReader<String>()

		val linesRead = mutableListOf<String>()

		reader.setResource(resource)
		reader.setLinesToRead(0, 4)
		reader.setLineMapper { it, _ -> it }

		reader.open(ExecutionContext())

		do {
			val line = reader.read()
			if (line != null) {
				linesRead.add(line)
			}
		} while (line != null)

		reader.close()

		assertEquals(5, linesRead.size)
	}

	private val testFile: File
		get() {
			val file = File(tempFolder, "testFile.txt")
			file.createNewFile()
			file.writer().use {
				it.write("First line\n")
				it.write("Second line\n")
				it.write("Third line\n")
				it.write("Fourth line\n")
				it.write("Fifth line\n")
			}
			return file
		}
}