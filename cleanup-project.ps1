Write-Host "Running project cleanup for AmpMeter..." -ForegroundColor Cyan

# Check for duplicate UI components
Write-Host "Checking for duplicate UI components..." -ForegroundColor Yellow
$UI_FILES_IN_ROOT = (Get-ChildItem -Path "app/src/main/java/com/example/ampmeter/ui" -Filter "*.kt" -Recurse -ErrorAction SilentlyContinue)
if ($UI_FILES_IN_ROOT.Count -gt 0) {
    Write-Host "Found duplicate UI components in ui/ directory. These should be in presentation/ui/ instead." -ForegroundColor Red
    foreach ($file in $UI_FILES_IN_ROOT) {
        Write-Host "  - $($file.FullName)" -ForegroundColor Red
    }
    $confirmDelete = Read-Host "Do you want to delete these duplicate files? (y/n)"
    if ($confirmDelete -eq "y") {
        Remove-Item -Path "app/src/main/java/com/example/ampmeter/ui" -Recurse -Force
        Write-Host "Duplicate UI components removed." -ForegroundColor Green
    }
}

# Clean build files
Write-Host "Cleaning build files..." -ForegroundColor Yellow
& ./gradlew clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error cleaning build files." -ForegroundColor Red
} else {
    Write-Host "Build files cleaned successfully." -ForegroundColor Green
}

# Clean Android cache
Write-Host "Cleaning Android cache..." -ForegroundColor Yellow
if (Test-Path "clean-android-cache.ps1") {
    & ./clean-android-cache.ps1 -Force
    Write-Host "Android cache cleaned." -ForegroundColor Green
} else {
    Write-Host "clean-android-cache.ps1 script not found." -ForegroundColor Red
}

# Remove error logs
Write-Host "Removing Java error logs..." -ForegroundColor Yellow
$errorLogs = Get-ChildItem -Path "." -Filter "hs_err_pid*.log" -Recurse
if ($errorLogs.Count -gt 0) {
    foreach ($log in $errorLogs) {
        Remove-Item $log.FullName -Force
        Write-Host "Removed: $($log.Name)" -ForegroundColor Green
    }
} else {
    Write-Host "No error logs found." -ForegroundColor Green
}

# Run lint to identify unused resources
Write-Host "Running lint to identify unused resources..." -ForegroundColor Yellow
& ./gradlew lint
if ($LASTEXITCODE -eq 0) {
    Write-Host "Lint completed successfully. Check reports at app/build/reports/lint-results.html" -ForegroundColor Green
} else {
    Write-Host "Lint found issues. Please review app/build/reports/lint-results.html" -ForegroundColor Yellow
}

Write-Host "Cleanup complete!" -ForegroundColor Cyan
Write-Host "For more information on maintaining a clean project, see AmpMeter/docs/MaintenanceGuidelines.md" -ForegroundColor Cyan 