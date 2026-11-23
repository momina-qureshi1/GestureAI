# GestureAI – AI-Based Hand Gesture Recognition Android Application

## 1. Project Description

GestureAI is an AI-powered Android application developed using Java and TensorFlow Lite. It recognizes different hand gestures from images and live camera input using a trained deep learning model.

The application uses a MobileNetV2-based image classification model converted into TensorFlow Lite format to perform on-device gesture recognition.

### Main Features
- User registration and login
- Live hand gesture detection using the device camera
- Gesture recognition from gallery images
- AI-based gesture classification
- Gesture history management
- Simple and user-friendly interface

### Supported Gestures
The application is designed to recognize 15 gestures:

1. Call
2. Dislike
3. Fist
4. Four
5. Like
6. Mute
7. No Gesture
8. OK
9. One
10. Palm
11. Peace
12. Rock
13. Stop
14. Three
15. Two Up

## 2. Prerequisites

The following software and hardware are required to develop or run the project:

- Windows operating system
- Android Studio
- Java Development Kit (JDK) 17
- Android SDK (API 34)
- Android device running Android 7.0 or later (API 24+)
- USB cable for testing on a physical Android device
- Internet connection for downloading project dependencies during initial setup

**Note:** The APK can be installed directly on a compatible Android device without installing Android Studio.

## 3. Installation Instructions

### A. Running Through Android Studio

1. Download and extract the GestureAI project ZIP file.
2. Open Android Studio.
3. Select **Open** and choose the extracted GestureAI project folder.
4. Allow Android Studio to sync the Gradle files.
5. Connect an Android device or start an emulator.
6. Enable USB debugging if using a physical device.
7. Click the **Run** button to build and launch the application.

### B. Installing Through APK

1. Locate the provided `GestureAI.apk` file.
2. Transfer it to your Android device.
3. Open the APK file.
4. Allow installation from the selected source if Android requests permission.
5. Complete the installation.
6. Open GestureAI.

## 4. Dependencies and Technologies

The project uses the following technologies and libraries:

| Technology / Library | Purpose |
|---|---|
| Java | Android application development |
| XML | User interface design |
| Android Studio | Development environment |
| TensorFlow Lite 2.17.0 | On-device machine learning inference |
| MobileNetV2 | Image classification model architecture |
| CameraX 1.4.2 | Live camera functionality |
| SQLite | Local database management |
| SharedPreferences | User session management |
| AndroidX AppCompat | Android compatibility |
| ConstraintLayout | User interface layouts |
| RecyclerView | Displaying list-based data |

### Machine Learning Model
- Model architecture: MobileNetV2
- Model format: TensorFlow Lite (`.tflite`)
- Input image size: 224 × 224 pixels
- Output: 15 gesture classes
- Processing: On-device inference

## 5. Execution Instructions

1. Launch the GestureAI application.
2. Register a new account or log in using an existing account.
3. On the home screen, select the desired feature.

### Live Gesture Detection
1. Select **Start Live Detection**.
2. Grant camera permission.
3. Show a hand gesture in front of the camera.
4. The application processes the camera image and displays the predicted gesture.

### Gallery Gesture Recognition
1. Select **Choose Image from Gallery**.
2. Choose an image containing a hand gesture.
3. The application processes the selected image.
4. View the predicted gesture and confidence score.

### Gesture History
Users can access previously saved gesture predictions through the history feature, if available in the installed application.

## 6. Project Structure

```text
GestureAI/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/com/gestureai/app/
│           │   ├── MainActivity.java
│           │   ├── SplashActivity.java
│           │   ├── LoginActivity.java
│           │   ├── SignUpActivity.java
│           │   ├── HomeActivity.java
│           │   ├── GestureActivity.java
│           │   ├── GalleryActivity.java
│           │   ├── database/
│           │   ├── ml/
│           │   └── utils/
│           │
│           ├── res/
│           ├── assets/
│           │   └── gesture_model.tflite
│           └── AndroidManifest.xml
│
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```

## 7. Permissions

The application requires camera permission for live hand gesture detection.

Gallery access is handled through Android's image picker.

## 8. Limitations

- Recognition accuracy may vary depending on lighting, camera quality, hand position, and image clarity.
- Similar hand gestures may sometimes be confused.
- Clear hand visibility is recommended for better predictions.

## 9. Developer Information

**Developer:** Momina Qureshi  
**Project Name:** GestureAI  
**Platform:** Android  
**Programming Language:** Java  
**Project Type:** AI-Based Mini Application  
**Machine Learning Framework:** TensorFlow Lite

