package com.mimir.platform.events

import com.mimir.contracts.events.EventPublisherPort
import com.mimir.contracts.events.ProductEvent
import com.mimir.contracts.events.ProductEventHandler

import com.mimir.kernel.contract.EventEnvelope
import java.util.concurrent.CopyOnWriteArrayList

fun interface EventHandler {
    fun onEvent(event: EventEnvelope)
}

/** In-process event bus (Blueprint shared events). Not durable. */
class EventBus : EventPublisherPort {
    private val handlers = CopyOnWriteArrayList<EventHandler>()

    fun subscribe(handler: EventHandler) { handlers.add(handler) }
    fun unsubscribe(handler: EventHandler) { handlers.remove(handler) }

    fun publish(event: EventEnvelope) {
        handlers.forEach { h ->
            runCatching { h.onEvent(event) }
        }
    }

    fun publish(type: String, source: String, payload: Map<String, String> = emptyMap(), correlationId: String? = null) {
        publish(EventEnvelope(type = type, source = source, correlationId = correlationId, payload = payload))
    }

    override fun publish(event: ProductEvent) {
        publish(event.type, event.source, event.payload, event.correlationId)
    }

    override fun subscribe(handler: ProductEventHandler) {
        subscribe(EventHandler { env ->
            handler.onEvent(
                ProductEvent(env.type, env.source, env.payload, env.correlationId),
            )
        })
    }

}
