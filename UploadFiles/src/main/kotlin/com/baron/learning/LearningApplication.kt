package com.baron.learning

import com.baron.learning.storage.StorageProperties
import com.baron.learning.storage.StorageService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties::class)
class LearningApplication {
    @Bean
    fun init(storageService: StorageService): CommandLineRunner {
        return CommandLineRunner {
            storageService.deleteAll()
            storageService.init()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<LearningApplication>(*args)
}
