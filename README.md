
## Tech Used

- **Jetpack Compose** – UI
- **Android Jetpack Libraries**:
    - **ViewModel & Lifecycle** – Lifecycle-aware state handling
    - **Navigation Compose** – In-app navigation
    - **Room** – Local database for offline data persistence
    - **WorkManager** – Background job scheduler used for syncing
- **Hilt** – Dependency injection framework
- **Paging 3** –  paginated data
- **Retrofit + OkHttp** – REST API integration and network logging
- **Kotlin Coroutines** – Asynchronous programming and background work


### 🟢 Online Capabilities
- View a paginated list of pupils fetched from the remote API (data is stored locally first before being displayed), with a retry button to manually reload in case of failure
- Create new pupils with proper input validation
- Update and delete existing pupil records
- Sync pupils that were added or marked for deletion while offline
- Handle API errors and simulate real-world conditions (e.g. delays, failures)
- Search pupils: first check the local database, then fall back to the remote API if not found  ### 🔴 Offline Capabilities
- View a list of pupils from the local database
- Add and mark pupils for creation or deletion while offline
- Queue changes for automatic sync when the device reconnects
- Search pupil records locally without needing internet access


## Design & Assumptions
- Follows MVVM architecture for testability and separation of concerns
- Offline-first using Room and WorkManager for  sync
- Minimal UI to focus on core requirements
- Assumes image input is a valid URL or omitted, -Assumes list is ordered for pagination -Assumes network might be unstable

## Further Improvements
- Add more robust unit and UI testing
- Support offline updates


---