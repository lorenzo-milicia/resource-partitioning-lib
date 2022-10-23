# Custom partitioner for Spring Batch

## Who is it for?
For cases in which you have multiple files to process as part of the batch,
Spring Batch offers the ready to use `MultiResourcePartitioner`, which sets up
one `ExecutionContext` for each `Resource`, making it possible to process
multiple files in parallel.

Some use cases could go even further, and also partition each single file, but
there is no built-in partitioner that is designed to do anything like that.
In this library you will find an implementation of the `Partitioner` and an
extension of the `FlatFileItemReader` to do just that, giving you the possibility
to improve the performance of your batch processing even further, if your
specific use case allows it.

> ⚠️: **Since the order of execution of the partitions is not guaranteed, use this library
> only if the order in which the lines of the file are processed doesn't matter**

## How to use it
Using the `MultiResourceChunkedPartitioner` is pretty straight forward, and very similar to how you would
use the standard `MultiResourcePartitioner`.

The configuration of the partitioner should look something like this:

```kotlin
fun partitioner(resources: List<Resource>): MultiResourceChunkedPartitioner {
	val partitioner = MultiResourceChunkedPartitioner(resources)

	partitioner.setLinesToSkip(1)   // Set in the partitioner instead of the ItemReader
	partitioner.partitionSize = 10_000  // Sets the number of lines to process in each partition
	return partitioner
}
```

The `MultiResourceChunkedPartitioner` adds three key-value pairs to each `ExecutionContext`:
- `fileName` -  Same as the `MultiResourcePartitioner`
- `startingLineIndex` - The index of the line from which the `ItemReader` that will take that partition should start reading from
- `endingLineIndex` - The index of the line from which the `ItemReader` that will take that partition should stop reading at

If the `partitionSize` is not set, then the `MultiResourceChunkedPartitioner` will create one partition per file, behaving in the same way as the `MultiResourcePartitioner`.

The `PartitionedFlatFileReader` is designed to integrate easily with the `MultiResourceChunkedPartitioner`. The configuration of the reader should look something like this:
```kotlin
@Bean
@StepScope
fun reader(
    @Value("#{stepExecutionContext[fileName]}") pathToFile: String,
    @Value("#{stepExecutionContext[startingLineIndex]}") startingLineIndex: Int,
    @Value("#{stepExecutionContext[endingLineIndex]}") endingLineIndex: Int,
): PartitionedFlatFileReader<Any> {
    val reader = PartitionedFlatFileReader<String>()
    reader.setResource(FileSystemResource(pathToFile.substringAfter("file:/")))
    reader.setLinesToRead(startingLineIndex, endingLineIndex)
    reader.setLineMapper { it, idx ->
        // Line mapping
        [...]
    }
    return reader
}
```

WIP
