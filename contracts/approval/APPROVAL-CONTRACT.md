# Approval contract

**Code:** `com.mimir.kernel.security.ApprovalToken` + `ApprovalService`
**App facade:** `com.mimir.service.approvals.UserApprovalFacade`

## Rules
1. Never trust `input["approvalGranted"]`.
2. High/Critical capabilities require token via `input["approvalToken"]`.
3. Token binds principalId, actionId, targetResource, scope, expiry, single-use.
4. KernelHost.consumeIfValid before engine.invoke.
