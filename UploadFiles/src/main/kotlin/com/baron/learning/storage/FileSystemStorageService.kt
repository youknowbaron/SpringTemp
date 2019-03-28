package com.baron.learning.storage

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Stream
import java.net.MalformedURLException
import org.springframework.core.io.UrlResource

@Service
class FileSystemStorageService @Autowired constructor(properties: StorageProperties) : StorageService {

    private val rootLocation = Paths.get(properties.location)

    override fun init() {
        try {
            Files.createDirectories(rootLocation)
        } catch (e: IOException) {
            throw StorageException("Could not initialize storage", e)
        }
    }

    override fun store(file: MultipartFile) {
        val fileName = StringUtils.cleanPath(file.originalFilename ?: "")
        try {
            if (file.isEmpty) {
                throw StorageException("Failed to store empty file $fileName")
            }
            if (fileName.contains("..")) {
                // This is a security check
                throw StorageException("Cannot store file with relative path outside current directory $fileName")
            }
            file.inputStream.use { inputStream ->
                Files.copy(inputStream, this.rootLocation.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (e: IOException) {
            throw StorageException("Failed to store file $fileName", e)
        }
    }

    override fun loadAll(): Stream<Path> {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter{ path -> path != this.rootLocation }
                    .map(this.rootLocation::relativize)
        } catch (e: IOException) {
            throw StorageException("Failed to read stored files", e)
        }
    }

    override fun load(filename: String): Path {
        return rootLocation.resolve(filename)
    }

    override fun loadAsResource(filename: String): Resource {
        try {
            val file = load(filename)
            val resource = UrlResource(file.toUri())
            return if (resource.exists() || resource.isReadable) {
                resource
            } else {
                throw StorageFileNotFoundException("Could not read file: $filename")

            }
        } catch (e: MalformedURLException) {
            throw StorageFileNotFoundException("Could not read file: $filename", e)
        }

    }

    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }
}