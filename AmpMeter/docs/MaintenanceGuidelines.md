# AmpMeter Project Maintenance Guidelines

## Project Cleanliness Guidelines

This document outlines best practices for maintaining a clean and well-organized codebase for the AmpMeter Android application.

### 1. Package Structure

- **Follow the clean architecture pattern**:
  - `data` package: For data sources, repositories implementations, DTOs, and mappers
  - `domain` package: For business logic, models, and repository interfaces
  - `presentation` package: For UI components (activities, fragments, view models)
  - `di` package: For dependency injection modules
  - `util` package: For utility classes

- **Avoid duplicate directories**: Make sure all components are in their appropriate packages and not duplicated elsewhere.

### 2. Managing UI Components

- **Keep UI components in the presentation layer**: All fragments, activities, and view models should be in the `presentation` package.

- **Follow the MVVM pattern**:
  - Fragment/Activity: Handle UI rendering and user interactions
  - ViewModel: Handle UI logic and state
  - Repository: Interface between data and presentation layers

- **Naming conventions**:
  - Fragment classes should end with `Fragment`
  - ViewModel classes should end with `ViewModel`
  - Layout files should match component names (e.g., `fragment_dashboard.xml` for `DashboardFragment`)

### 3. Managing Dependencies

- **Gradle Dependencies**:
  - Regularly update dependencies to their latest stable versions
  - Consider using a centralized dependency management approach (e.g., `libs.versions.toml`)
  - Remove unused dependencies to reduce APK size

- **Build Scripts**:
  - Keep only necessary build scripts
  - Document the purpose of each build script
  - Avoid creating multiple scripts with overlapping functionality

### 4. Clean Code Practices

- **Remove unused imports**: Delete unused imports to prevent confusion and improve readability.

- **Remove unused functions and classes**: Regularly audit code for unused elements.

- **Document complex functions**: Add comments explaining complex logic.

- **Follow Android best practices**:
  - Use ViewBinding over findViewById
  - Use Navigation Component for fragment navigation
  - Use Hilt for dependency injection

### 5. Navigation Setup

- **Ensure proper Navigation Component implementation**:
  - Use NavHostFragment in your layout
  - Properly define navigation graph in XML
  - Use the correct approach to obtain the NavController:
    ```kotlin
    // Correct way to get NavController from NavHostFragment
    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    val navController = navHostFragment.navController
    ```

### 6. Regular Cleanup Tasks

- **Remove build artifacts**:
  - Run clean task before builds: `./gradlew clean`
  - Periodically clean cache: `./clean-android-cache.ps1 -Force`

- **Remove crash logs**:
  - Delete `hs_err_*.log` files after investigating crashes

- **Audit unused resources**:
  - Remove unused layouts, drawables, and strings
  - Use Lint to identify unused resources: `./gradlew lint`

### 7. Error Prevention

- **Test after cleanup**: Always run tests and build the app after cleaning up to ensure no regressions.

- **Version control**:
  - Commit changes regularly
  - Use meaningful commit messages
  - Consider creating branches for major cleanup tasks

### 8. Documentation

- **Document architecture decisions**: Create and maintain documentation for major architecture decisions.

- **Update README**: Keep the README up-to-date with setup instructions and project overview.

- **Document complex components**: Add inline documentation to explain complex components or custom views.

### Common Issues to Watch For

1. **Navigation issues**: Ensure NavHostFragment is properly configured in layout files and NavController is obtained correctly.

2. **Duplicate UI components**: Watch out for duplicate fragments or view models in different packages.

3. **Resource conflicts**: Ensure resource IDs are unique and not duplicated across different XML files.

4. **Build script proliferation**: Avoid creating multiple build scripts with overlapping functionality.

5. **Gradle cache corruption**: Occasionally clean Gradle cache to prevent build issues.

By following these guidelines, we can maintain a clean, efficient, and maintainable codebase for the AmpMeter application. 