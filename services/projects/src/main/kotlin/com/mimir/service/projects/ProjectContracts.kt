package com.mimir.service.projects

/** Minimal contract — implementation TBD. */
data class ProjectId(val value: String)
data class ProjectSummary(val id: ProjectId, val title: String, val archived: Boolean = false)

interface ProjectDirectory {
    fun list(): List<ProjectSummary>
    fun get(id: ProjectId): ProjectSummary?
}
class InMemoryProjectDirectory : ProjectDirectory {
    private val items = linkedMapOf<String, ProjectSummary>()
    override fun list() = items.values.toList()
    override fun get(id: ProjectId) = items[id.value]
    fun put(summary: ProjectSummary) { items[summary.id.value] = summary }
}
