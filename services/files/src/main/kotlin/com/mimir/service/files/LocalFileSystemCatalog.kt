package com.mimir.service.files

import java.io.File

/**
 * INT-004 S2: lists files under a sandboxed root only (no path escape).
 */
class LocalFileSystemCatalog(
    private val root: File,
) : FileCatalog {
    init {
        root.mkdirs()
    }

    private fun safeChild(name: String): File? {
        if (name.isBlank() || name.contains("..") || name.contains('/') || name.contains('\\')) return null
        val f = File(root, name).canonicalFile
        if (!f.path.startsWith(root.canonicalFile.path)) return null
        return f
    }

    override fun list(): List<FileRef> =
        root.listFiles()?.filter { it.isFile }?.map {
            FileRef(id = it.name, name = it.name, mime = null, sizeBytes = it.length())
        }?.sortedBy { it.name } ?: emptyList()

    override fun register(name: String, mime: String?, sizeBytes: Long): FileRef {
        val f = safeChild(name) ?: error("invalid name")
        if (!f.exists()) f.writeBytes(ByteArray(0))
        return FileRef(id = f.name, name = f.name, mime = mime, sizeBytes = f.length())
    }

    override fun get(id: String): FileRef? {
        val f = safeChild(id) ?: return null
        if (!f.isFile) return null
        return FileRef(id = f.name, name = f.name, sizeBytes = f.length())
    }
}
