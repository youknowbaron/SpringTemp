package com.baron.learning.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("storage")
data class StorageProperties(
        val location: String = "upload-dir"
)
