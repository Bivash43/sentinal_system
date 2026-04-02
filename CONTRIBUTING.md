# Contributing to Project Sentinal

Thanks for your interest in contributing.

## How to Contribute

1. Fork the repository.
2. Create a feature branch from `main`.
3. Make focused changes with clear commit messages.
4. Add/update tests for behavior changes.
5. Open a pull request with context and validation steps.

## Development Setup

1. Start local infrastructure:
   - `docker compose up -d --build`
   - `ai-worker` waits for healthy `postgres` and `kafka` before startup.
2. Run backend:
   - `cd sentinal_backend && ./mvnw spring-boot:run`
3. Run ML worker (if not using Docker worker):
   - `cd sentinal_ml`
   - `pip install -r requirements.txt`
   - `python -m app.worker`
4. Run tests:
   - `cd sentinal_backend && ./mvnw test`

## Security and Auth Notes

- Authentication is JWT-based:
  - Login endpoint: `POST /api/auth/login`
  - Protected endpoints require `Authorization: Bearer <token>`
- RBAC currently includes `ADMIN`, `ANALYST`, and `VIEWER`.
- Role module (`/api/roles/*`) is restricted to the seeded bootstrap admin account (configured by `app.security.bootstrap-admin.username`).
- Forbidden access attempts are audited:
  - Log pattern: `SECURITY_AUDIT 403 ...`
  - Metric: `sentinel_security_forbidden_total`

## Configuration Checklist for Contributors

- Verify security-related backend config in `sentinal_backend/src/main/resources/application.properties`:
  - `app.security.jwt.secret`
  - `app.security.jwt.expiration-ms`
  - `app.security.bootstrap-admin.username`
  - `app.security.bootstrap-admin.password`
- Never commit production secrets. Use environment-specific overrides for non-local environments.

## Pull Request Checklist

- [ ] The change is scoped and documented.
- [ ] No secrets or credentials are committed.
- [ ] Relevant tests were added or updated.
- [ ] API or config changes are reflected in `README.md`.
- [ ] Changelog entry is added when appropriate.
- [ ] RBAC/security impacts (roles, endpoint access, 401/403 behavior) are validated and documented.

## Coding Guidelines

- Keep methods small and focused.
- Prefer explicit names over abbreviations.
- Avoid unrelated refactors in the same PR.
- Keep backward compatibility unless change is documented.

## Reporting Issues

When creating an issue, include:

- Expected behavior
- Actual behavior
- Reproduction steps
- Environment details (OS, Java/Python versions, Docker version)

## Code of Conduct

Be respectful and constructive in all discussions and reviews.
