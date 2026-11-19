# Event envelope

**Code:** `com.mimir.kernel.contract.EventEnvelope`

Fields: type, source, correlationId, payload, version, atEpochMs.
Delivery semantics (at-least-once vs exactly-once) are owned by shared/events when implemented.
