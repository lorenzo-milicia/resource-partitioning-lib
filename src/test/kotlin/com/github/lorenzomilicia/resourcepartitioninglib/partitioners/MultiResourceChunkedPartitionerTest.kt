package com.github.lorenzomilicia.resourcepartitioninglib.partitioners

import com.github.lorenzomilicia.resourcepartitioninglib.utils.countLines
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
internal class MultiResourceChunkedPartitionerTest {

	@field:TempDir
	lateinit var tempFolder: File

	@BeforeEach
	internal fun setUp() {
		mockkStatic(Resource::countLines)
	}

	@Test
	internal fun `Diving files in partitions`() {
		val resources = listOf(
			FileSystemResource(File(tempFolder, "firstfile.txt").also { it.createNewFile() }),
			FileSystemResource(File(tempFolder, "second.txt").also { it.createNewFile() }),
		)
		val partitioner = MultiResourceChunkedPartitioner(resources)
		partitioner.partitionSize = 5

		every { resources[0].countLines() } returns 8
		every { resources[1].countLines() } returns 5

		val partitions = partitioner.partition(0)

		val context1 = assertNotNull(partitions["partition0"])
		val context2 = assertNotNull(partitions["partition1"])
		val context3 = assertNotNull(partitions["partition2"])

		assertEquals(resources[0].url.toExternalForm(), context1.getString("fileName"))
		assertEquals(resources[0].url.toExternalForm(), context2.getString("fileName"))
		assertEquals(resources[1].url.toExternalForm(), context3.getString("fileName"))


		assertEquals(0, context1.getInt("startingLine"))
		assertEquals(5, context2.getInt("startingLine"))
		assertEquals(0, context3.getInt("startingLine"))

		assertEquals(4, context1.getInt("finishingLine"))
		assertEquals(8, context2.getInt("finishingLine"))
		assertEquals(5, context3.getInt("finishingLine"))
	}
}