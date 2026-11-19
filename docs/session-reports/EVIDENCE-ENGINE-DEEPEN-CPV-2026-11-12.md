# Evidence — computer / process / vault deepen

## Process
- Shell high-risk gate (approvalToken)
- Tests OK (2): argv echo + risk_blocked

## Computer
- Observe level blocks ACT
- Assist ACT requires approvalToken
- Tests OK (3)

## Vault
- validKey on PUT; keyCount helper
- Tests OK (3): roundtrip, wrong passphrase, invalid key

## Total this batch
8 tests OK (kotlinc+JUnit)
