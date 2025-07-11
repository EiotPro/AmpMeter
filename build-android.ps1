#!/usr/bin/env pwsh

# Script to build Android app with optimized settings
# This script configures the build with Java compatibility and optimized Gradle settings

param(
    [switch]$Clean = $false,
    [switch]$Debug = $false,
    [switch]$Release = $false,
    [switch]$Offline = $false
)

Write-Host "Starting optimized Android build process..."

# Set build task based on parameters
$buildTask = "assembleDebug"
if ($Release) {
    $buildTask = "assembleRelease"
}

# Clean build directories if requested
if ($Clean) {
    Write-Host "Cleaning build directories..."
    if (Test-Path -Path "build") {
        Remove-Item -Recurse -Force "build"
        Write-Host "Removed root build directory"
    }

    if (Test-Path -Path "app/build") {
        Remove-Item -Recurse -Force "app/build"
        Write-Host "Removed app build directory"
    }
}

# Set environment variables for Java compatibility
# For Java 21, we need to use specific module access flags
$env:JAVA_TOOL_OPTIONS="--add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED"

# Add module access for compiler modules
$env:JAVA_TOOL_OPTIONS="$env:JAVA_TOOL_OPTIONS --add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"

# Build command with optimizations
$gradleCommand = ".\gradlew $buildTask"

# Add optional flags
if ($Debug) {
    $gradleCommand += " --debug"
}

if ($Offline) {
    $gradleCommand += " --offline"
}

# Add performance optimizations
$gradleCommand += " --no-daemon --parallel --build-cache"

# Add memory optimizations - use less memory to avoid OOM errors
$gradleCommand += " -Dorg.gradle.jvmargs='-Xmx1024m -XX:MaxMetaspaceSize=256m -XX:+HeapDumpOnOutOfMemoryError'"

# Execute the build command
Write-Host "Running: $gradleCommand"
Invoke-Expression $gradleCommand

# Check build result
if ($LASTEXITCODE -eq 0) {
    Write-Host "Build completed successfully!" -ForegroundColor Green
    
    # Show output APK location
    if ($Release) {
        $apkPath = Get-ChildItem -Path "app\build\outputs\apk\release\*.apk" -ErrorAction SilentlyContinue
    } else {
        $apkPath = Get-ChildItem -Path "app\build\outputs\apk\debug\*.apk" -ErrorAction SilentlyContinue
    }
    
    if ($apkPath) {
        Write-Host "APK generated at: $($apkPath.FullName)" -ForegroundColor Cyan
    }
} else {
    Write-Host "Build failed with exit code $LASTEXITCODE" -ForegroundColor Red
}

# Reset environment variables
$env:JAVA_TOOL_OPTIONS=""

Write-Host "Build process completed." 