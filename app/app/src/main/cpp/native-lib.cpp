#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/bitmap.h>

using namespace cv;

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_opencv_MainActivity_processYUVFrame(
        JNIEnv *env,
        jobject /* this */,
        jbyteArray yuvData,
        jint width,
        jint height,
        jint yStride,
        jint uvStride,
        jint uvPixelStride) {

    // ✅ Access Java byte array safely
    jbyte *yuvBytes = env->GetByteArrayElements(yuvData, nullptr);
    if (!yuvBytes) return nullptr;

    // ✅ Convert to cv::Mat
    Mat yuvMat(height + height / 2, width, CV_8UC1, (unsigned char *) yuvBytes);

    // ✅ Convert YUV to RGB
    Mat rgbMat;
    cvtColor(yuvMat, rgbMat, COLOR_YUV2RGB_NV21);

    // ✅ Process (Canny Edge Detection)
    Mat edges;
    cvtColor(rgbMat, edges, COLOR_RGB2GRAY);
    GaussianBlur(edges, edges, Size(5, 5), 1.5, 1.5);
    Canny(edges, edges, 100, 200);

    // ✅ Convert edges to RGBA (for Android Bitmap)
    Mat rgba;
    cvtColor(edges, rgba, COLOR_GRAY2RGBA);

    // ✅ Create Bitmap to return
    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapMethod = env->GetStaticMethodID(
            bitmapClass, "createBitmap",
            "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jfieldID argb8888Field = env->GetStaticFieldID(
            bitmapConfigClass, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
    jobject argb8888Obj = env->GetStaticObjectField(bitmapConfigClass, argb8888Field);

    jobject bitmapObj = env->CallStaticObjectMethod(bitmapClass, createBitmapMethod,
                                                    width, height, argb8888Obj);

    void *bitmapPixels;
    if (AndroidBitmap_lockPixels(env, bitmapObj, &bitmapPixels) < 0) {
        env->ReleaseByteArrayElements(yuvData, yuvBytes, JNI_ABORT);
        return nullptr;
    }

    memcpy(bitmapPixels, rgba.data, rgba.total() * rgba.elemSize());
    AndroidBitmap_unlockPixels(env, bitmapObj);

    // ✅ Release Java array
    env->ReleaseByteArrayElements(yuvData, yuvBytes, JNI_ABORT);

    return bitmapObj;
}
