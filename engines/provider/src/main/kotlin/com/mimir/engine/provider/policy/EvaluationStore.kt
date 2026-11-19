package com.mimir.engine.provider.policy

import com.mimir.engine.provider.contract.ProviderId

data class EvaluationNote(
    val providerId: String,
    val stage: String, // security|technical|product
    val passed: Boolean,
    val summary: String,
    val evidenceRefs: List<String> = emptyList(),
    val atEpochMs: Long = 0L,
)

class EvaluationStore {
    private val notes = linkedMapOf<String, MutableList<EvaluationNote>>()

    fun add(note: EvaluationNote) {
        notes.getOrPut(note.providerId) { mutableListOf() }.add(note)
    }

    fun list(providerId: String) = notes[providerId]?.toList().orEmpty()

    fun hasPassing(providerId: String, stage: String) =
        notes[providerId]?.any { it.stage == stage && it.passed } == true
}
