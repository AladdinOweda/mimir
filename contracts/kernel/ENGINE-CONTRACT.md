# Engine contract (mimir.engine/1.x)

**Code:** `com.mimir.kernel.contract.MimirEngine` / `EngineManifest` / `InvocationRequest` / `InvocationResult`

## Rules
1. Engines register via KernelHost.attach(engine, manifest).
2. All capability work goes through KernelHost.invoke — no UI→engine private calls for privileged ops.
3. `approvalGranted` boolean in input is stripped; only ApprovalToken is valid.
4. Manifest.provides lists CapabilityRef ids from CapabilityCatalog where applicable.
5. Health and lifecycle states follow EngineState enum.

## Versioning
- Compatible: add optional output keys, new optional capabilities.
- Breaking: remove capability, change required input keys, change error semantics.
