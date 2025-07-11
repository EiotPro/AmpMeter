#!/usr/bin/env pwsh

# Script to clean Android and Gradle caches to free up disk space
# This script removes temporary files and caches that can consume a lot of disk space

param(
    [switch]$Force = $false
)

$totalFreed = 0

function Remove-DirectoryIfExists {
    param (
        [string]$Path,
        [string]$Description
    )
    
    if (Test-Path -Path $Path) {
        $size = (Get-ChildItem -Path $Path -Recurse | Measure-Object -Property Length -Sum).Sum / 1MB
        $size = [math]::Round($size, 2)
        
        if ($Force) {
            Remove-Item -Recurse -Force -Path $Path -ErrorAction SilentlyContinue
            if (-not (Test-Path -Path $Path)) {
                Write-Host "Removed $Description ($size MB)" -ForegroundColor Green
                return $size
            } else {
                Write-Host "Failed to remove $Description" -ForegroundColor Red
                return 0
            }
        } else {
            Write-Host "$Description will free $size MB" -ForegroundColor Cyan
            return 0
        }
    }
    return 0
}

Write-Host "Android and Gradle Cache Cleaner" -ForegroundColor Cyan
Write-Host "--------------------------------" -ForegroundColor Cyan

if (-not $Force) {
    Write-Host "Running in simulation mode. Use -Force to actually delete files." -ForegroundColor Yellow
    Write-Host ""
}

# Project-specific build directories
Write-Host "Checking project build directories..." -ForegroundColor Cyan
$totalFreed += Remove-DirectoryIfExists -Path "build" -Description "Project build directory"
$totalFreed += Remove-DirectoryIfExists -Path "app/build" -Description "App build directory"
$totalFreed += Remove-DirectoryIfExists -Path ".gradle" -Description "Project .gradle directory"

# Android Studio caches
$androidStudioCachePath = "$env:LOCALAPPDATA\Google\AndroidStudio*"
$caches = Get-ChildItem -Path $androidStudioCachePath -Directory -ErrorAction SilentlyContinue

foreach ($cache in $caches) {
    $totalFreed += Remove-DirectoryIfExists -Path "$($cache.FullName)\system\caches" -Description "Android Studio system caches"
    $totalFreed += Remove-DirectoryIfExists -Path "$($cache.FullName)\system\compiler" -Description "Android Studio compiler cache"
    $totalFreed += Remove-DirectoryIfExists -Path "$($cache.FullName)\system\log" -Description "Android Studio logs"
}

# Gradle caches
$gradleCache = "$env:USERPROFILE\.gradle\caches"
$totalFreed += Remove-DirectoryIfExists -Path "$gradleCache\build-cache-1" -Description "Gradle build cache"
$totalFreed += Remove-DirectoryIfExists -Path "$gradleCache\transforms-*" -Description "Gradle transforms cache"
$totalFreed += Remove-DirectoryIfExists -Path "$gradleCache\jars-*" -Description "Gradle jars cache"

# Android SDK caches
$androidSdk = "$env:LOCALAPPDATA\Android\sdk"
$totalFreed += Remove-DirectoryIfExists -Path "$androidSdk\build-cache" -Description "Android SDK build cache"
$totalFreed += Remove-DirectoryIfExists -Path "$androidSdk\.temp" -Description "Android SDK temp directory"

# Android AVD temp files
$avdPath = "$env:USERPROFILE\.android\avd"
if (Test-Path -Path $avdPath) {
    $avds = Get-ChildItem -Path $avdPath -Directory -ErrorAction SilentlyContinue
    foreach ($avd in $avds) {
        $totalFreed += Remove-DirectoryIfExists -Path "$($avd.FullName)\cache" -Description "AVD cache for $($avd.Name)"
        $totalFreed += Remove-DirectoryIfExists -Path "$($avd.FullName)\tmp" -Description "AVD temp for $($avd.Name)"
    }
}

# Summary
Write-Host ""
if ($Force) {
    Write-Host "Total disk space freed: $totalFreed MB" -ForegroundColor Green
} else {
    Write-Host "Total disk space that would be freed: $totalFreed MB" -ForegroundColor Cyan
    Write-Host "Run with -Force parameter to actually delete these files." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Cache cleaning process completed." 