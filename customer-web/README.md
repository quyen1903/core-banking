# QuinnBank Customer Web

Customer-facing demo internet banking frontend for QuinnBank Core.

## Run Locally

```powershell
pnpm install
pnpm dev
```

The app runs on `http://localhost:3000` by default.

## Backend Connection

Set the public API base URL when the Spring Boot backend is ready:

```env
NEXT_PUBLIC_QUINNBANK_API_BASE_URL=http://localhost:8080
```

The API client is isolated under `api/`. Existing backend routes are typed where
they exist. Missing banking workflows use synthetic mock data and TODO-marked
placeholder functions that do not mutate backend or local financial state.

## Security Notes

- Browser validation is only a usability layer.
- Financial commands must be authorized, validated, audited, idempotent, and
  posted by backend APIs.
- No secrets are stored in frontend source.
- Demo data is synthetic.
