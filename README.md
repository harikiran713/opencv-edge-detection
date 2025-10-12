# 🧪 Android + OpenCV (C++) + OpenGL + Web — R&D Assessment

This project demonstrates **real-time edge detection** using the Android Camera API, **OpenCV (C++)**, and **OpenGL ES 2.0** for rendering. It also includes a **TypeScript-based web viewer** that displays a processed frame to illustrate native-to-web integration.

---

## 🧭 Table of Contents

1. [Overview](#overview)
2. [Tech Stack](#tech-stack)
3. [Architecture](#architecture)
4. [Project Structure](#project-structure)
5. [Installation & Setup](#installation--setup)
6. [Android App Instructions](#android-app-instructions)
8. [Features](#features)
9. [Demo & Screenshots](#demo--screenshots)
10. [Troubleshooting](#troubleshooting)
11. [Git Commit Guidelines](#git-commit-guidelines)
12. [Author](#author)

---

## 🧩 Overview

This app captures live frames from the Android camera, processes them using **C++ (OpenCV)** for **Canny Edge Detection**, and renders the result in real time using **OpenGL ES**.
It also includes a **TypeScript web component** to visualize processed frames in a browser.

---

## 🛠 Tech Stack

| Category            | Technology              |
| ------------------- | ----------------------- |
| Android Development | Kotlin / Java           |
| Native Processing   | OpenCV (C++)            |
| Rendering           | OpenGL ES 2.0           |
| Native Interface    | JNI + NDK + CMake       |
| Version Control     | Git + GitHub / GitLab   |

---

## 🧠 Architecture

```
Camera Feed (Android)
        ↓
JNI Bridge (Java ↔ C++)
        ↓
OpenCV (C++ Edge Detection)
        ↓
OpenGL Renderer (TextureView)

```

---

## 📁 Project Structure

```
project-root/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── cpp/                   # Native C++ code
│   │   │   │   ├── CMakeLists.txt
│   │   │   │   └── native-lib.cpp
│   │   │   ├── java/com/example/opencvapp/
│   │   │   │   └── MainActivity.kt    # Main Activity (UI + JNI call)
│   │   │   ├── res/                   # Layouts, strings, etc.
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│
├── screenshots/
│   └── main_screen.png
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── local.properties
├── .gitignore

```

---

## ⚙️ Installation & Setup

### 🧩 Prerequisites

| Tool                 | Notes                                        |
| -------------------- | -------------------------------------------- |
| Android Studio       | Latest version                               |
| Android NDK + CMake  | Installed via SDK Tools                      |
| OpenCV Android SDK   | [Download here](https://opencv.org/releases) |
| Git                  | For version control and submission           |

---

### 🧱 Android Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/harikiran713/opencv-edge-detection.git
cd app
```

#### 2. Configure OpenCV

Extract the OpenCV Android SDK and update your `CMakeLists.txt`:

```cmake
include_directories("C:/OpenCV-android-sdk/sdk/native/jni/include")
```

#### 3. Open in Android Studio

* Go to **File → Open → Select Project Folder**
* Ensure **NDK** and **CMake** are installed
* Rebuild the project

#### 4. Run the App

* Connect an Android device
* Click **Run ▶️**
* Tap **“Start C++ Edge Detection”** to view real-time edge output



## 🌟 Features

| Feature                    | Description                                      |
| -------------------------- | ------------------------------------------------ |
| 📸 Camera Feed Integration | Captures live camera feed using TextureView      |
| ⚙️ C++ Frame Processing    | Performs Canny edge detection using OpenCV (C++) |
| 🔁 JNI Bridge              | Enables communication between Kotlin and C++     |
| 🎨 OpenGL ES Rendering     | Displays processed frames at ~15 FPS             |


---

## 🖼 Demo & Screenshots

**Main Screen**

![Main Screen](https://github.com/harikiran713/opencv-edge-detection/blob/main/screenshots/main_screen.png?raw=true)
![working ](https://github.com/harikiran713/opencv-edge-detection/blob/main/screenshots/image.png?raw=true)
![human pic ](https://github.com/harikiran713/opencv-edge-detection/blob/main/screenshots/hari.png?raw=true)

🎥 **Demo Video**

[YouTube Demo](https://youtube.com/shorts/xhBdc-hijig?feature=share)

📱 **Download APK**

[Google Drive Link](https://drive.google.com/file/d/1nr64DdUTUd7Vu4Pbijwtv1MRMQmGoHE_/view?usp=sharing)

---



---

## ⚙️ Troubleshooting

| Issue                 | Fix                                                |
| --------------------- | -------------------------------------------------- |
| ❌ Build fails         | Verify OpenCV SDK path in `CMakeLists.txt`         |
| ⚫ Black output        | Check camera permissions in `AndroidManifest.xml`  |
| 💥 JNI error          | Ensure native method names match in Kotlin and C++ |
| 🌐 Web not displaying | Run `tsc` and recompile TypeScript code            |

---




## 👨‍💻 Author

**Harikiran Lode**
