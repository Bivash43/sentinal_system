# Contributing to Project Sentinel

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
2. Run backend:
   - `cd sentinal_backend && ./mvnw spring-boot:run`
3. Run ML worker (if not using Docker worker):
   - `cd sentinal_ml`
   - `pip install -r requirements.txt`
   - `python -m app.worker`

## Pull Request Checklist

- [ ] The change is scoped and documented.
- [ ] No secrets or credentials are committed.
- [ ] Relevant tests were added or updated.
- [ ] API or config changes are reflected in `README.md`.
- [ ] Changelog entry is added when appropriate.

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
