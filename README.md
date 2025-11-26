# sanluapp

docker compose --env-file .env up --build

## JWT configuration

This project uses JWT for API authentication. The backend reads the following properties from Spring configuration (the local profile reads them from environment variables):

- `jwt.secret` — secret key used to sign tokens. Recommend a base64-encoded 256-bit (32 bytes) random value. Keep this secret out of version control.
- `jwt.access.expiration-minutes` — access token lifetime in minutes (default 15).
- `jwt.refresh.expiration-days` — refresh token lifetime in days (default 30).

For local development you can put the secret in `local.env` (this repo already contains `local.env`). Example (PowerShell) to generate a secure base64 secret and add to `local.env`:

```powershell
$bytes = New-Object 'System.Byte[]' 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes) | Out-File -FilePath .\local.env -Append
# then edit local.env and add a line:
# JWT_SECRET=YourBase64SecretHere
```

Or manually add to `local.env`:

```
JWT_SECRET=replace_with_a_base64_secret
```

Important: do NOT commit your production secrets. For deployments, set `JWT_SECRET` in your container/orchestration environment (e.g. Docker secrets, cloud secret manager).
