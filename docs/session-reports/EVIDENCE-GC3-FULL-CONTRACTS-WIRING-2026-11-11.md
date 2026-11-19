# Evidence — G-C3 full contracts + wiring

## Contracts
terminal · forge · browser · agent · workflow · events

## Port bindings
| Port | Implementation |
|------|----------------|
| TerminalPort | TerminalEngine |
| ForgePort | ForgeEngine |
| BrowserPort | BrowserEngine |
| AgentPort | AgentRuntimeEngine |
| WorkflowPort | WorkflowEngine |
| EventPublisherPort | EventBus |

## Tests
6× Contract*Test OK (kotlinc+JUnit)

## Note
ForgePort build/test methods intentionally honest (not silent fake success). Full invoke path remains KernelHost.
