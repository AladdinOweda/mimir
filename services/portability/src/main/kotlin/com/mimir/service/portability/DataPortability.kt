package com.mimir.service.portability

/** INT-013 skeleton — export/import job handles, not full serializers. */
enum class PortabilityJobStatus { Pending, Running, Completed, Failed }

data class PortabilityJobId(val value: String)

data class PortabilityJob(
    val id: PortabilityJobId,
    val kind: String,
    val status: PortabilityJobStatus = PortabilityJobStatus.Pending,
)

interface DataPortability {
    fun requestExport(scope: String): PortabilityJob
    fun requestImport(scope: String): PortabilityJob
    fun status(id: PortabilityJobId): PortabilityJob?
}

class InMemoryDataPortability : DataPortability {
    private val jobs = linkedMapOf<String, PortabilityJob>()
    private var seq = 0L
    private fun newJob(kind: String): PortabilityJob {
        val id = PortabilityJobId("port-${++seq}")
        val j = PortabilityJob(id, kind)
        jobs[id.value] = j
        return j
    }
    override fun requestExport(scope: String) = newJob("export:$scope")
    override fun requestImport(scope: String) = newJob("import:$scope")
    override fun status(id: PortabilityJobId) = jobs[id.value]
}
