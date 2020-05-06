# VideoWorld
An application that will show you a streamer's Twitch clips and plays it. You can save comments for a clip and download
any clip so you can see it at any time.

This is a proof-of concept on how to perform video downloads with WorkManager's API and Retrofit.

The app follows the MVVM architecture with the repository pattern, alongside Koin for DI.

In the branch 'with-dagger2' you could check the project how are the dependencies injected with Dagger2.
The branch implements advices and tips from Fred Porciúncula's talk from the Android Makers 2019 Paris conference.
I recommend everyone that wants to understand Dagger2 and learn Dagger2 to [see it](https://www.youtube.com/watch?v=9fn5s8_CYJI&t=1987s)

The code is well documented when it needs. Otherwise the functions are just one line or their self explain.

If you are here only for the know-how, see the download-video branch. The MainActivity holds the main code for the custom control view of the SimplePlayerView is interactions and the DownloadWorker the download implementation.

### Compiling
After cloning and opening the project, you would need to create the `ApiKeys.kt` file under the `utils` folder, and add your proper API key from the [Twitch API](https://dev.twitch.tv/docs/)
with a variable named `TWITCH_CLIENT_ID`.

### Modifying
#### Folder structure
There are 5 main folders: app, db, ui, utils.
* app: Contains Application and dependencies related stuff, such as Koin Modules, API and local models, workers, etc.
* db: Contains the Room database related stuff, such as Entities and Daos.
* ui: Contains the Activities, Dialogs, Fragments and ViewHolder used across the App. If you don't see a view_holder, then the View is probably using an Epoxy is auto data-binding [generated model](https://github.com/airbnb/epoxy/wiki/Data-Binding-Support)
* utils: Contains utilities, views and base classes, function extensions. 
* viewmodels: Contains the ViewModels used across the App.
#### Contributing
I made this app for practice/professional purposes, but PR's are welcome!

### Libraries and other stuff applied
* Koin for dependency injection
* Exoplayer + custom controls views
* Auto-playing feature
* ViewPager2
* Room
* Data binding
* Material Components
* ViewModel and LiveData
* Jetpack navigation
* Coroutines
* Dagger2 for dependency injection (`with-dagger` branch)
* Assisted injection (`with-dagger` branch)
* Retrofit for REST API communication and to perform downloads
* WorkManager + Coroutines + Notifications
* Injection in a Work (`with-dagger` branch and main branch)
* Epoxy
* MVVM architecture + Repository pattern
* State handling on configuration changes

### Check the app!
You could download the .apk file from the release tab.
