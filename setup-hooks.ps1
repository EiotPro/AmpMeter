Write-Host "Setting up git hooks for AmpMeter project..." -ForegroundColor Cyan

# Make sure .githooks directory exists
if (-not (Test-Path .githooks)) {
    Write-Host "Creating .githooks directory..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path .githooks -Force | Out-Null
}

# Configure git to use the hooks
Write-Host "Configuring git to use hooks from .githooks directory..." -ForegroundColor Yellow
git config core.hooksPath .githooks

# Make sure the pre-commit hook is executable (Windows doesn't care, but good for cross-platform)
Write-Host "Making pre-commit hook executable..." -ForegroundColor Yellow
if (Test-Path .githooks/pre-commit.ps1) {
    Copy-Item .githooks/pre-commit.ps1 .git/hooks/ -Force
    Write-Host "Pre-commit hook has been installed." -ForegroundColor Green
} else {
    Write-Host "Warning: pre-commit.ps1 not found in .githooks directory." -ForegroundColor Red
}

Write-Host "Hook setup complete!" -ForegroundColor Green
Write-Host "Note: Pre-commit hooks will run automatically before each commit to check for code quality issues." -ForegroundColor Cyan 