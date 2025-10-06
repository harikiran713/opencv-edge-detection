cmake_minimum_required(VERSION 3.18.1)
project("native-lib")

# Use C++17
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ✅ Correct path to the folder containing OpenCVConfig.cmake
set(OpenCV_DIR "C:/Users/omsai/Downloads/opencv-4.12.0-android-sdk/OpenCV-android-sdk/sdk/native/jni")

# Find OpenCV package
find_package(OpenCV REQUIRED)

message(STATUS "OpenCV library status:")
message(STATUS "    OpenCV_INCLUDE_DIRS: ${OpenCV_INCLUDE_DIRS}")
message(STATUS "    OpenCV_LIBS: ${OpenCV_LIBS}")

# Add your native library
add_library(native-lib SHARED native-lib.cpp)

# Find Android native libraries
find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

# Include OpenCV headers
include_directories(${OpenCV_INCLUDE_DIRS})

# Link libraries
target_link_libraries(
        native-lib
        ${OpenCV_LIBS}
        ${log-lib}
        ${jnigraphics-lib}
        android
)
