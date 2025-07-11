#!/usr/bin/env pwsh

# Script to clean build directories and free up disk space before building

Write-Host "Cleaning build directories to free up disk space..."

# Remove build directories
if (Test-Path -Path "build") {
    Remove-Item -Recurse -Force "build"
    Write-Host "Removed root build directory"
}

if (Test-Path -Path "app/build") {
    Remove-Item -Recurse -Force "app/build"
    Write-Host "Removed app build directory"
}

# Run the Gradle command with minimal options
Write-Host "Running minimal Gradle build..."
.\gradlew assembleDebug --no-daemon --rerun-tasks -x lint -x test -x packageDebug 