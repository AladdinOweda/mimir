package com.mimir.service.files

/**
 * Application file catalog (Blueprint). Full Unified File Engine is separate;
 * this is the product-facing list/metadata surface.
 */
data class FileRef(val id: String, val name: String, val mime: String? = null, val sizeBytes: Long = 0)

interface FileCatalog {
    fun list(): List<FileRef>
    fun register(name: String, mime: String? = null, sizeBytes: Long = 0): FileRef
    fun get(id: String): FileRef?
}

class InMemoryFileCatalog : FileCatalog {
    private val map = linkedMapOf<String, FileRef>()
    private var seq = 0L
    override fun list() = map.values.toList()
    override fun register(name: String, mime: String?, sizeBytes: Long): FileRef {
        val id = "file-${++seq}"
        val ref = FileRef(id, name, mime, sizeBytes)
        map[id] = ref
        return ref
    }
    override fun get(id: String) = map[id]
}
