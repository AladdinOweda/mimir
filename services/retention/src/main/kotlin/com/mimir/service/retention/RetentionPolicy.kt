package com.mimir.service.retention

/**
 * INT-014 skeleton: retention intents only — no aggressive deletion by default.
 */
enum class RetentionClass {
    Ephemeral,
    Standard,
    Archival,
    LegalHold,
}

data class RetentionRule(
    val resourceType: String,
    val clazz: RetentionClass,
    val maxAgeDays: Int?,
)

interface RetentionPolicy {
    fun rules(): List<RetentionRule>
    fun classify(resourceType: String): RetentionClass
}

class DefaultRetentionPolicy : RetentionPolicy {
    private val rules = listOf(
        RetentionRule("activity", RetentionClass.Standard, 90),
        RetentionRule("download", RetentionClass.Ephemeral, 30),
        RetentionRule("knowledge", RetentionClass.Archival, null),
        RetentionRule("vault", RetentionClass.LegalHold, null),
    )
    override fun rules() = rules
    override fun classify(resourceType: String) =
        rules.find { it.resourceType == resourceType }?.clazz ?: RetentionClass.Standard
}


data class RetentionCandidate(
    val resourceType: String,
    val resourceId: String,
    val ageDays: Int,
    val clazz: RetentionClass,
    val actionable: Boolean,
)

/**
 * Evaluates age against rules. LegalHold/Archival never auto-delete.
 * Returns candidates for review — does not delete.
 */
class RetentionEvaluator(private val policy: RetentionPolicy = DefaultRetentionPolicy()) {
    fun evaluate(resourceType: String, resourceId: String, ageDays: Int): RetentionCandidate {
        val clazz = policy.classify(resourceType)
        val rule = policy.rules().find { it.resourceType == resourceType }
        val max = rule?.maxAgeDays
        val over = max != null && ageDays > max
        val actionable = over && clazz != RetentionClass.LegalHold && clazz != RetentionClass.Archival
        return RetentionCandidate(resourceType, resourceId, ageDays, clazz, actionable)
    }

    fun plan(items: List<Triple<String, String, Int>>): Map<String, String> {
        val cands = items.map { (t, id, age) -> evaluate(t, id, age) }
        return mapOf(
            "total" to cands.size.toString(),
            "actionable" to cands.count { it.actionable }.toString(),
            "held" to cands.count { it.clazz == RetentionClass.LegalHold || it.clazz == RetentionClass.Archival }.toString(),
            "ids" to cands.filter { it.actionable }.joinToString(",") { it.resourceId },
        )
    }
}
