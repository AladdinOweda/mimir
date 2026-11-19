# ProductContext ↔ Kernel EventBus

## Wiring
- `installKernelEventBridge()` on init (idempotent)
- Forwards every `host.events` envelope to product `EventBus`
- Records activity line: type = env.type, summary = payload, resource = correlationId|source
- `startAll()` ensures bridge + publishes `product.engines.started`
- `observabilitySnapshot()` = kernel securitySnapshot + bridge flag

## Test
KernelEventBridgePatternTest OK (1)
