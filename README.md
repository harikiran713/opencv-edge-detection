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
7. [Web Viewer Setup](#web-viewer-setup)
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
| Web Viewer          | TypeScript + HTML + CSS |
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
        ↓
Web Viewer (TypeScript)
```

---

## 📁 Project Structure (Updated)

Based on your VS Code project layout:

```
project-root/
│
├── app/
│   ├── build/                         # Build output (auto-generated)
│   ├── src/
│   │   ├── androidTest/java/com/example/opencvapp/
│   │   │   └── ExampleInstrumentedTest.kt
│   │   ├── main/
│   │   │   ├── cpp/                   # Native C++ code
│   │   │   │   ├── CMakeLists.txt
│   │   │   │   └── native-lib.cpp
│   │   │   ├── java/com/example/opencvapp/
│   │   │   │   └── MainActivity.kt    # Main Activity (UI + JNI call)
│   │   │   ├── res/                   # Layouts, strings, colors, etc.
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/com/example/opencvapp/
│   │       └── ExampleUnitTest.kt
│   │
│   ├── build.gradle.kts               # Module-level Gradle config
│   ├── proguard-rules.pro
│
├── gradle/                            # Gradle wrapper files
├── .idea/                             # IDE configuration
├── .gradle/                           # Gradle cache
├── .kotlin/                           # Kotlin compiler metadata
├── build.gradle.kts                   # Root Gradle file
├── settings.gradle.kts                # Project inclusion settings
├── gradle.properties                  # Global Gradle properties
├── local.properties                   # Local SDK/NDK paths
├── gradlew                            # Gradle wrapper script (Unix)
├── gradlew.bat                        # Gradle wrapper script (Windows)
├── .gitignore                         # Git ignore configuration
└── web/                               # TypeScript web viewer (to add)
    ├── index.html
    ├── style.css
    ├── main.ts
    └── tsconfig.json
```

---

## ⚙️ Installation & Setup

### 🧩 Prerequisites

| Tool                 | Notes                                        |
| -------------------- | -------------------------------------------- |
| Android Studio       | Latest version                               |
| Android NDK + CMake  | Installed via SDK Tools                      |
| OpenCV Android SDK   | [Download here](https://opencv.org/releases) |
| Node.js + TypeScript | For the web viewer                           |
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

---

## 🌐 Web Viewer Setup

#### 1. Move into the web folder

```bash
cd web
npm install
```

#### 2. Compile TypeScript

```bash
tsc
```

#### 3. Run the Viewer

Open `index.html` in a browser to see a static processed image or base64 frame.

---

## 🌟 Features

| Feature                    | Description                                      |
| -------------------------- | ------------------------------------------------ |
| 📸 Camera Feed Integration | Captures live camera feed using TextureView      |
| ⚙️ C++ Frame Processing    | Performs Canny edge detection using OpenCV (C++) |
| 🔁 JNI Bridge              | Enables communication between Kotlin and C++     |
| 🎨 OpenGL ES Rendering     | Displays processed frames at ~15 FPS             |
| 🌐 TypeScript Web Viewer   | Shows a processed static frame in the browser    |
| 🧮 Optional FPS Counter    | Measures and displays frame rate                 |
| 🕹️ Toggle Modes           | Switch between raw and processed camera view     |

---

## 🖼 Demo & Screenshots

📷 **Insert your screenshots here**

```markdown
![./screenshots/main_screen.png]
```

🎥 **Demo Video **

[YouTube Demo](https://youtube.com/shorts/Kuh7uaTOBLE?feature=share)

📱 **Download APK**

[Google Drive Link](https://drive.google.com/file/d/1nr64DdUTUd7Vu4Pbijwtv1MRMQmGoHE_/view?usp=sharing)

---

## 🧩 JNI Sample Code

```cpp
extern "C"
JNIEXPORT void JNICALL
Java_com_example_opencvapp_MainActivity_processFrame(
    JNIEnv *env, jobject, jlong inputAddr, jlong outputAddr) {

    cv::Mat &input = *(cv::Mat *)inputAddr;
    cv::Mat &output = *(cv::Mat *)outputAddr;
    cv::Canny(input, output, 80, 150);
}
```

---

## ⚙️ Troubleshooting

| Issue                 | Fix                                                |
| --------------------- | -------------------------------------------------- |
| ❌ Build fails         | Verify OpenCV SDK path in `CMakeLists.txt`         |
| ⚫ Black output        | Check camera permissions in `AndroidManifest.xml`  |
| 💥 JNI error          | Ensure native method names match in Kotlin and C++ |
| 🌐 Web not displaying | Run `tsc` and recompile TypeScript code            |

---

## 🧾 Git Commit Guidelines

| Type        | Description                                  |
| ----------- | -------------------------------------------- |
| `feat:`     | Add new feature (e.g., edge detection logic) |
| `fix:`      | Resolve bug or crash                         |
| `docs:`     | Update README or comments                    |
| `refactor:` | Code restructuring                           |
| `chore:`    | Build, Gradle, or dependency updates         |

**Examples:**

```bash
git commit -m "feat: add JNI interface for OpenCV edge detection"
git commit -m "docs: update README with setup instructions"
```

Make small, meaningful commits instead of one large “final commit.”

---

## 👨‍💻 Author

**Harikiran Lode**
