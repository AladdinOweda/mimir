# Contracts linkage

| Contract module | Port / IDs | Bound implementation |
|-----------------|------------|----------------------|
| capability | CapabilityCatalog | policies in composition |
| knowledge | KnowledgeFacade | InMemoryKnowledgeFacade |
| terminal | TerminalPort | **TerminalEngine** |
| forge | ForgePort | **ForgeEngine** |
| browser | BrowserPort | **BrowserEngine** |
| agent | AgentPort | **AgentRuntimeEngine** |
| workflow | WorkflowPort | **WorkflowEngine** |
| events | EventPublisherPort | **EventBus** (shared/events) |

Kernel does not depend on contract modules. Engines depend on contracts for ports.
