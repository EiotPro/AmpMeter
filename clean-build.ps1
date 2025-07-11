#!/usr/bin/env pwsh

# Script to clean build directories and rebuild the project

Write-Host "Cleaning build directories..."

# Remove build directories
if (Test-Path -Path "build") {
    Remove-Item -Recurse -Force "build"
    Write-Host "Removed root build directory"
}

if (Test-Path -Path "app/build") {
    Remove-Item -Recurse -Force "app/build"
    Write-Host "Removed app build directory"
}

# Run the Gradle command
Write-Host "Running Gradle assembleDebug..."
.\gradlew assembleDebug --no-daemon 