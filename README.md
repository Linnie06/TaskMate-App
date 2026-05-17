# TaskMate App 📱

A modern Android task management application that helps you organize, track, and complete your tasks efficiently — all while keeping your data **securely stored on your device**.

## Features ✨

* **Quick Task Creation**: Add tasks instantly with an intuitive interface
* **Voice Input**: Use voice commands to create tasks hands-free
* **Local Storage**: All data stored securely on your device (via SharedPreferences)
* **Task Analytics**: Visualize your productivity with charts and statistics
* **Smart Reminders**: Set notifications for important tasks
* **Priority Management**: Organize tasks by urgency or importance
* **Due Date Tracking**: Plan and monitor deadlines
* **Offline Operation**: Fully functional without internet connection

## Setup Instructions 🛠️

### Prerequisites

1. **Install Java Development Kit (JDK)**

    * Download and install JDK 11 or higher from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://openjdk.org/)
    * Set `JAVA_HOME` environment variable:

        * Windows: Add `C:\Program Files\Java\jdk-11.x.x` to `JAVA_HOME`
        * Add `%JAVA_HOME%\bin` to your PATH
    * Verify installation:

      ```bash
      java -version
      ```

2. **Android Studio**

    * Download from [Android Studio website](https://developer.android.com/studio)
    * Install with Android SDK (API level 23 or higher)

### Building the App

1. Clone or download this repository
2. Open project in Android Studio
3. Sync Gradle files
4. Build and run the app

## Architecture 🏗️

* **MainActivity**: Core app interface with task management
* **TaskAdapter**: RecyclerView adapter for displaying tasks
* **TaskItem / Task.java**: Data model for each task
* **AddTaskDialog / EditTaskDialog**: UI dialogs for creating and editing tasks
* **AnalyticsActivity**: Displays charts and statistics
* **TaskNotificationReceiver**: Handles scheduled reminders
* **SharedPrefHelper**: Manages data persistence via SharedPreferences

## Project Structure 🧩

```
TaskMate_App_Enhanced/
├── app/
│   ├── src/main/java/com/example/taskmate_app/
│   │   ├── MainActivity.java
│   │   ├── AddTaskDialog.java
│   │   ├── SharedPrefHelper.java
│   │   └── ...
│   ├── src/main/res/
│   └── build.gradle.kts
├── README.md
└── settings.gradle.kts
```

## Data Storage 💾

TaskMate does **not** use a cloud or shared database.
All user data is stored **locally** on each device using Android’s `SharedPreferences`.

You can view your data at:

```
/data/data/com.example.taskmate_app/shared_prefs/
```

## Technologies Used 💻

* **Language**: Java
* **Android SDK**: Target API 34, Min API 23
* **Material Design**: Modern UI components
* **MPAndroidChart**: Data visualization
* **SharedPreferences**: Local data persistence
* **AlarmManager & Notifications**: Reminders and alerts

## Analytics 📊

* Pie chart visualization of completed vs pending tasks
* Real-time updates when task status changes
* Summary statistics and quick overview

## Notifications 🔔

* Scheduled task reminders
* Custom notification channels
* Background alarm handling

## Permissions 📋

* **RECORD_AUDIO** – Voice input
* **WAKE_LOCK** – For scheduled alarms
* **POST_NOTIFICATIONS** – For task reminders

## Troubleshooting 🔧

### Java Issues

* Ensure JDK 11 or higher is installed
* Set `JAVA_HOME` and update your `PATH`
* Restart Android Studio after making changes

### Build Issues

* Run:

  ```bash
  ./gradlew clean build
  ```
* Or “Sync Project with Gradle Files” in Android Studio

## Contributing 🤝

1. Fork this repository
2. Create a new feature branch
3. Implement your changes
4. Test thoroughly
5. Submit a pull request

## License 📄

This project is open source and available under the **MIT License**.

---

**TaskMate** — *Your personal productivity companion.* 🚀