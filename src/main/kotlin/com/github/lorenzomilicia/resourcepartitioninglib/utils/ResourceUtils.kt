package com.github.lorenzomilicia.resourcepartitioninglib.utils

import org.springframework.core.io.Resource
import java.nio.file.Files

internal fun Resource.countLines(): Int =
	Files.lines(file.toPath()).use {
		it.count().toInt()
	}