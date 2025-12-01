# Zingg Labeller - Design Assignment

This repo contains my analysis of Labeller.java from zingg project.

## whats inside

- original-code/ - the original file i analyzed
- refactored/ - code changes i am suggesting  
- design-critique.md - detailed writeup

## issues i found

1. too many things happening in single class
2. System.in hardcoded so cant test properly
3. exception handling is not proper
4. using numbers directly instead of constants
5. same code written in multiple places

## what i suggest

basically split into smaller classes and use interfaces so we can test easily

check design-critique.md for full details
