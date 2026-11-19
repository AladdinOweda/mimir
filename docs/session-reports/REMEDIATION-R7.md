# R7 Vault & Providers — 2026-11-04

## Delivered
- Vault AES-GCM + PBKDF2 (120k iterations) only
- XOR/stream cipher **forbidden**
- Weak passphrase rejected (min 8)
- Durable ciphertext via SQLite (`JdbcVaultBlobStore`)
- `CostGuard` + Provider authorize ignores client `allowSpend`
- Smoke **8/8 PASS**

## Not closed
- Platform Keystore / Android Keystore
- Live provider HTTP adapters
