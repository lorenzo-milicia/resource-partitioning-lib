package com.github.lorenzomilicia.resourcepartitioninglib.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.core.io.FileSystemResource
import java.io.File

internal class ResourceUtilsKtTest {

	@field:TempDir
	lateinit var tempFolder: File

	private val emptyFile: File
		get() {
			val file = File(tempFolder, "emptyFile.csv")
			file.createNewFile()
			return file
		}

	private val nonEmptyFile: File
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

	@Test
	internal fun `Given an empty file, then the count of lines is zero`() {
		val lineCount = FileSystemResource(emptyFile).countLines()

		assertEquals(0, lineCount)
	}

	@Test
	internal fun `Given a non empty file, then the count of lines is correct`() {
		val lineCount = FileSystemResource(nonEmptyFile).countLines()

		assertEquals(5, lineCount)
	}
}