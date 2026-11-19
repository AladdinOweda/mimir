package com.mimir.contracts.knowledge

object KnowledgeCapabilityIds {
    const val INDEX = "knowledge.index"
    const val SEARCH = "knowledge.search"
    const val GET = "knowledge.get"
    const val DELETE = "knowledge.delete"
    const val LIST = "knowledge.list"
    const val TAG = "knowledge.tag"
    val ALL = listOf(INDEX, SEARCH, GET, DELETE, LIST, TAG)
}
