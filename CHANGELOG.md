# Changelog

All notable changes to this project will be documented in this file.

The format is inspired by [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project aims to follow [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added

- Open-source style README with badges and contributor-facing sections.
- `CONTRIBUTING.md` to standardize contribution workflow.
- `CHANGELOG.md` to track release history.
- `sentinal_ml/tests/fire_transactions.py` script for sending mixed transaction loads (normal, velocity stress, high amount) to the analyze API.

### Changed

- Updated project documentation for portfolio presentation.
- Updated `README.md` with a manual transaction test script section.

## [0.1.0] - 2026-03-29

### Added

- Initial distributed fraud orchestration platform:
  - Spring Boot backend
  - Kafka messaging
  - Python ML worker
  - PostgreSQL and Redis integration
  - Prometheus and Grafana monitoring
