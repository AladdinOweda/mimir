package com.mimir.contracts.uft

object UftCapabilityIds {
    const val TRANSFER_CREATE = "uft.transfer.create"
    const val TRANSFER_PROGRESS = "uft.transfer.progress"
    const val TRANSFER_COMPLETE = "uft.transfer.complete"
    const val TRANSFER_CANCEL = "uft.transfer.cancel"
    const val TRANSFER_STATUS = "uft.transfer.status"
    const val TRANSFER_LIST = "uft.transfer.list"
    const val TRANSFER_CHECKSUM = "uft.transfer.checksum"
    const val TRANSFER_RESUME = "uft.transfer.resume"
    val ALL = listOf(
        TRANSFER_CREATE, TRANSFER_PROGRESS, TRANSFER_COMPLETE, TRANSFER_CANCEL,
        TRANSFER_STATUS, TRANSFER_LIST, TRANSFER_CHECKSUM, TRANSFER_RESUME,
    )
}

data class UftTransferId(val value: String)

interface UftPort {
    fun create(source: String, destination: String, direction: String = "Copy"): UftTransferId
    fun complete(id: UftTransferId, failed: Boolean = false): Boolean
}
