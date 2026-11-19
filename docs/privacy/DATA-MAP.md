# Privacy Data Map (draft)
Updated: 2026-11-03

| Data | Storage | Leaves device? | Opt-in required |
|------|---------|----------------|-----------------|
| Vault secrets | local encrypted | No | N/A (local) |
| Workflow run state | local store | No by default | — |
| Terminal command logs | local / redacted | No by default | — |
| Model prompts (local noop) | memory | No | — |
| Model prompts (cloud adapter) | provider | **Yes** | **Yes** |
| Browser page content | local tab model | Only if user cloud tool | **Yes** |
| Crash reports | none until configured | if enabled | **Yes** |

Policy: **opt-in** for any network exfiltration. Constitution privacy > convenience.
