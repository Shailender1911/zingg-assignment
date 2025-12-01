# Zingg Labeller Design Assignment

Design critique for Labeller.java from zingg codebase.

## Folder structure

- original-code/ - contains the original Labeller.java
- refactored/ - my suggested improvements
- design-critique.md - analysis and suggestions

## Main issues found

1. Class doing too many things
2. Hardcoded System.in makes testing hard
3. Exception handling needs improvement
4. Magic numbers used for options
5. Code duplication across classes

## Changes suggested

Split into smaller classes with single responsibility and use interfaces for testability.

See design-critique.md for details.

