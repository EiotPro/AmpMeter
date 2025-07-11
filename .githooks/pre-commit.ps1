# Pre-commit hook for AmpMeter Android application
Write-Host "Running pre-commit checks..." -ForegroundColor Cyan

# Check for duplicate UI components in wrong locations
Write-Host "Checking for duplicate UI components..." -ForegroundColor Cyan
$UI_FILES_IN_ROOT = (Get-ChildItem -Path "app/src/main/java/com/example/ampmeter/ui" -Filter "*.kt" -Recurse -ErrorAction SilentlyContinue).Count
if ($UI_FILES_IN_ROOT -gt 0) {
    Write-Host "Error: Found UI components in app/src/main/java/com/example/ampmeter/ui directory." -ForegroundColor Red
    Write-Host "UI components should be in the presentation package. See docs/MaintenanceGuidelines.md" -ForegroundColor Yellow
    exit 1
}

# Run lint checks
Write-Host "Running lint checks..." -ForegroundColor Cyan
try {
    & ./gradlew lint --daemon
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Lint checks failed. Please fix the issues before committing." -ForegroundColor Red
        Write-Host "See the report at app/build/reports/lint-results.html" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "Failed to run lint checks: $_" -ForegroundColor Red
    exit 1
}

# Check navigation setup
Write-Host "Checking Navigation Component setup..." -ForegroundColor Cyan
$NAV_HOST_COUNT = (Select-String -Path "app/src/main/res/layout/*.xml" -Pattern 'android:name="androidx.navigation.fragment.NavHostFragment"' -AllMatches | Measure-Object).Count
$NAV_GRAPH_COUNT = (Select-String -Path "app/src/main/res/layout/*.xml" -Pattern 'app:navGraph="@navigation/' -AllMatches | Measure-Object).Count

if ($NAV_HOST_COUNT -eq 0) {
    Write-Host "Warning: NavHostFragment not found in layout files!" -ForegroundColor Yellow
    Write-Host "Navigation may not work correctly." -ForegroundColor Yellow
    exit 1
}

if ($NAV_GRAPH_COUNT -eq 0) {
    Write-Host "Warning: No navGraph attribute found in layout files!" -ForegroundColor Yellow
    Write-Host "Navigation may not work correctly." -ForegroundColor Yellow
    exit 1
}

# Success
Write-Host "Pre-commit checks passed!" -ForegroundColor Green
exit 0 