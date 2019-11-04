Watch Now Movie Sample with Android Architecture Components
===========================================================

This is a sample movie app that uses Android Architecture Components with Dagger 2.

Introduction
-------------

### Functionality
The app is composed of 2 main screens.
#### MovieFragment
Allows you to search movies on themoviedb.
Each search result is kept in the database in `Movie` table.

Each time a new page is fetched, the same `Movie` record in the
Database is updated with the new list of repository ids.

**NOTE** The UI currently loads all `Movie` items at once, which would not
perform well on lower end devices. Instead of manually writing lazy
adapters, we've decided to wait until the built in support in Room is released.

#### SimilarFragment
This fragment displays the details of a move and its similars.

### Building
You can open the project in Android studio and press run.
### Testing
The project uses only instrumentation tests that run on the device.
To run and generate a coverage report, you can run:

`./gradlew fullCoverageReport` (requires a connected device or an emulator)

### Libraries
* [Android Support Library][support-lib]
* [Android Architecture Components][arch]
* [Android Data Binding][data-binding]
* [Dagger 2][dagger2] for dependency injection
* [Retrofit][retrofit] for REST api communication
* [Glide][glide] for image loading
* [Timber][timber] for logging
* [espresso][espresso] for UI tests
* [mockito][mockito] for mocking in tests


[mockwebserver]: https://github.com/square/okhttp/tree/master/mockwebserver
[support-lib]: https://developer.android.com/topic/libraries/support-library/index.html
[arch]: https://developer.android.com/arch
[data-binding]: https://developer.android.com/topic/libraries/data-binding/index.html
[espresso]: https://google.github.io/android-testing-support-library/docs/espresso/
[dagger2]: https://google.github.io/dagger
[retrofit]: http://square.github.io/retrofit
[glide]: https://github.com/bumptech/glide
[timber]: https://github.com/JakeWharton/timber
[mockito]: http://site.mockito.org