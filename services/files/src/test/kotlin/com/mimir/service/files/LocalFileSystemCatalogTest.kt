package com.mimir.service.files

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LocalFileSystemCatalogTest {
    @Test
    fun sandbox_list_and_reject_traversal() {
        val dir = File.createTempFile("mimir-files", null).apply { delete(); mkdirs() }
        try {
            val cat = LocalFileSystemCatalog(dir)
            cat.register("note.txt")
            File(dir, "note.txt").writeText("hi")
            assertEquals(1, cat.list().size)
            assertEquals("note.txt", cat.get("note.txt")?.name)
            assertFails { cat.register("../escape.txt") }
            assertNull(cat.get("../escape.txt"))
            assertTrue(cat.list().none { it.name.contains("..") })
        } finally {
            dir.deleteRecursively()
        }
    }
}
