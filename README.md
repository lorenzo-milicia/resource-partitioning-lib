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

### Kotlin

```kotlin
fun partitioner(resources: List<Resource>): MultiResourceChunkedPartitioner {
	val partitioner = MultiResourceChunkedPartitioner(resources)

	partitioner.setLinesToSkip(1)   // Set in the partitioner instead of the ItemReader
	partitioner.partitionSize = 10_000  // Sets the number of lines to process in each partition
	return partitioner
}
```
###

### Java
```java
public class PartitioningStep   {
    
    MultiResourceChunkedPartitioner partitioner(ArrayList<Resource> resources) {
        MultiResourceChunkedPartitioner partitioner = new MultiResourceChunkedPartitioner(resources);
    
        partitioner.setLinesToSkip(1);   // Set in the partitioner instead of the ItemReader
        partitioner.setPartitionSize(10_000);  // Sets the number of lines to process in each partition
    
        return partitioner;
    }
}
```
###

The `MultiResourceChunkedPartitioner` adds three key-value pairs to each `ExecutionContext`:
- `fileName` -  Same as the `MultiResourcePartitioner`
- `startingLineIndex` - The index of the line from which the `ItemReader` that will take that partition should start reading from
- `endingLineIndex` - The index of the line from which the `ItemReader` that will take that partition should stop reading at

If the `partitionSize` is not set, then the `MultiResourceChunkedPartitioner` will create one partition per file, behaving in the same way as the `MultiResourcePartitioner`.

The `PartitionedFlatFileReader` is designed to integrate easily with the `MultiResourceChunkedPartitioner`. The configuration of the reader should look something like this:

### Kotlin

```kotlin
@Bean
@StepScope
fun <T> reader(
    @Value("#{stepExecutionContext[fileName]}") pathToFile: String,
    @Value("#{stepExecutionContext[startingLineIndex]}") startingLineIndex: Int,
    @Value("#{stepExecutionContext[endingLineIndex]}") endingLineIndex: Int,
): PartitionedFlatFileReader<T> {
    val reader = PartitionedFlatFileReader<T>()
    
    reader.setResource(FileSystemResource(pathToFile.substringAfter("file:/")))
    reader.setLinesToRead(startingLineIndex, endingLineIndex)
    reader.setLineMapper { it, idx ->
        // Line mapping
    }
    
    return reader
}
```
###

### Java

```java
public class PartitioningStep {

    @Bean
    @StepScope
    PartitionedFlatFileReader<T> itemReader(
            @Value("#{stepExecutionContext[fileName]}") String pathToFile,
            @Value("#{stepExecutionContext[startingLineIndex]}") int startingLineIndex,
            @Value("#{stepExecutionContext[endingLineIndex]}") int endingLineIndex
    ) {
        PartitionedFlatFileReader<T> reader = new PartitionedFlatFileReader<T>();

        reader.setResource(new FileSystemResource(pathToFile.substring(pathToFile.lastIndexOf("file:/") + 1)));
        reader.setLinesToRead(startingLineIndex, endingLineIndex);
        reader.setLineMapper(
                (row, idx) -> {
                    // Line mapping
                }
        );

        return reader;
    }
}
```
###

The `PartitionedFlatFileReader` behaves very much in the same way as the `FlatFileItemReader`, with the noticeable differences being
the method `setLinesToRead` which should take as parameters the values that the partitioner added to the `ExecutionContext`, and the fact that
the method `setLinesToSkip` is deprecated, since the lines to skip should be set at the partitioner level, to avoid skipping the lines for all the partitions
of the same file, and not just for the first partition.

## Notes
In case of conflicts, the default key names for *filekeyName*, *startingLinekeyName* and *endingLinekeyName* can be overridden.