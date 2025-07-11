# AmpMeter Maintenance Scripts

This document provides information about the maintenance scripts available in the AmpMeter project to help keep the codebase clean and organized.

## Available Scripts

### setup-hooks.ps1

This script sets up Git hooks for the project.

**Usage:**
```powershell
.\setup-hooks.ps1
```

**What it does:**
- Configures Git to use hooks from the `.githooks` directory
- Copies the pre-commit hook to the appropriate location
- Ensures the pre-commit hook is executable

### cleanup-project.ps1

This script performs various cleanup operations on the project.

**Usage:**
```powershell
.\cleanup-project.ps1
```

**What it does:**
- Checks for duplicate UI components and offers to remove them
- Cleans build files using Gradle
- Cleans Android cache using clean-android-cache.ps1
- Removes Java error logs
- Runs lint to identify unused resources

### clean-android-cache.ps1

This script cleans Android-specific cache files.

**Usage:**
```powershell
.\clean-android-cache.ps1 -Force
```

**What it does:**
- Removes temporary Android build files
- Clears Gradle cache
- Removes build directories

## Pre-commit Hooks

The pre-commit hook in `.githooks/pre-commit.ps1` performs the following checks before allowing a commit:

1. Checks for duplicate UI components in the wrong locations
2. Runs lint checks to identify code quality issues
3. Ensures navigation components are properly set up

## When to Use These Scripts

- **setup-hooks.ps1**: When setting up the project for the first time or after a fresh clone
- **cleanup-project.ps1**: Periodically to clean up the project and before major releases
- **clean-android-cache.ps1**: When experiencing build issues or when the cache gets too large 