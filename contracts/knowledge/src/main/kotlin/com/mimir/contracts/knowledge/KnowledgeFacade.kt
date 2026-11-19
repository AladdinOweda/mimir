package com.mimir.contracts.knowledge

/**
 * INT-003 / ADR-038: product Brain surface. No vendor types; implementations may use adapters.
 */
data class KnowledgeHit(val id: String, val title: String, val snippet: String, val score: Double = 0.0)

data class KnowledgeDoc(val id: String, val title: String, val body: String)

interface KnowledgeFacade {
    fun index(id: String, title: String, body: String)
    fun search(query: String, limit: Int = 10): List<KnowledgeHit>
    fun get(id: String): KnowledgeDoc?
}

/** Honest in-memory stub — not a production RAG engine. */
class InMemoryKnowledgeFacade : KnowledgeFacade {
    private val docs = linkedMapOf<String, KnowledgeDoc>()

    override fun index(id: String, title: String, body: String) {
        docs[id] = KnowledgeDoc(id, title, body)
    }

    override fun search(query: String, limit: Int): List<KnowledgeHit> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        return docs.values.mapNotNull { d ->
            val hay = (d.title + "\n" + d.body).lowercase()
            if (!hay.contains(q)) return@mapNotNull null
            val score = if (d.title.lowercase().contains(q)) 1.0 else 0.5
            KnowledgeHit(d.id, d.title, d.body.take(160), score)
        }.sortedByDescending { it.score }.take(limit)
    }

    override fun get(id: String): KnowledgeDoc? = docs[id]
}
