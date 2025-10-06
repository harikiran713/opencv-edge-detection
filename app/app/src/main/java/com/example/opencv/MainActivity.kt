package com.example.opencv

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.*
import android.util.Log
import android.view.Surface
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.opencv.databinding.ActivityMainBinding
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var textureView: android.view.TextureView
    private lateinit var processedImageView: ImageView
    private lateinit var btnToggle: Button
    private lateinit var tvStatus: TextView

    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private var backgroundHandler: Handler? = null
    private var backgroundThread: HandlerThread? = null

    private var isProcessingEnabled = false
    private var lastFrameTime = 0L
    private var lastProcessTime = 0L

    companion object {
        private const val TAG = "EdgeDetectionApp"
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val PREVIEW_WIDTH = 640
        private const val PREVIEW_HEIGHT = 480
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        setupClickListeners()

        // Load native library
        System.loadLibrary("native-lib")
    }

    private fun initializeViews() {
        textureView = binding.textureView
        processedImageView = binding.processedImageView
        btnToggle = binding.btnToggle
        tvStatus = binding.tvStatus
    }

    private fun setupClickListeners() {
        btnToggle.setOnClickListener {
            isProcessingEnabled = !isProcessingEnabled
            updateUI()

            if (isProcessingEnabled) {
                Toast.makeText(this, "C++ Edge Detection Started", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Normal Camera View", Toast.LENGTH_SHORT).show()
                processedImageView.setImageBitmap(null)
            }
        }

        textureView.surfaceTextureListener = object : android.view.TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }

    private fun updateUI() {
        val statusText = if (isProcessingEnabled)
            "C++ EDGE DETECTION: ACTIVE - Processing in Native Code"
        else
            "NORMAL CAMERA VIEW - Tap button to start C++ edge detection"

        val buttonText = if (isProcessingEnabled) "STOP EDGE DETECTION" else "START C++ EDGE DETECTION"

        tvStatus.text = statusText
        btnToggle.text = buttonText
    }

    private fun openCamera() {
        val cameraManager = getSystemService(CameraManager::class.java)
        val cameraId = cameraManager.cameraIdList[0]

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            return
        }

        // Increase buffer to 3 for stability
        imageReader = ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, ImageFormat.YUV_420_888, 3)

        imageReader?.setOnImageAvailableListener({ reader ->
            val now = System.currentTimeMillis()

            // Throttle to one frame every 150 ms (~6–7 FPS)
            if (now - lastProcessTime < 150) {
                reader.acquireLatestImage()?.close()
                return@setOnImageAvailableListener
            }
            lastProcessTime = now

            var image: Image? = null
            try {
                image = reader.acquireLatestImage()
                if (image != null && isProcessingEnabled) {
                    processImageInNative(image)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error acquiring or processing image: ${e.message}")
            } finally {
                image?.close() // ✅ always close, even on failure
            }
        }, backgroundHandler)

        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                createCameraPreviewSession()
            }

            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
                cameraDevice = null
            }

            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
                cameraDevice = null
                Toast.makeText(this@MainActivity, "Camera error: $error", Toast.LENGTH_SHORT).show()
            }
        }, backgroundHandler)
    }

    private fun createCameraPreviewSession() {
        val texture = textureView.surfaceTexture
        texture?.setDefaultBufferSize(PREVIEW_WIDTH, PREVIEW_HEIGHT)
        val surface = Surface(texture)

        val captureRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder?.addTarget(surface)
        imageReader?.surface?.let { captureRequestBuilder?.addTarget(it) }

        val targets = listOfNotNull(surface, imageReader?.surface)

        cameraDevice?.createCaptureSession(
            targets,
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    captureRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                    session.setRepeatingRequest(captureRequestBuilder?.build()!!, null, backgroundHandler)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Toast.makeText(this@MainActivity, "Failed to configure camera", Toast.LENGTH_SHORT).show()
                }
            },
            backgroundHandler
        )
    }

    private fun processImageInNative(image: Image) {
        val planes = image.planes
        val yPlane = planes[0]
        val uPlane = planes[1]
        val vPlane = planes[2]

        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21Data = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21Data, 0, ySize)
        vBuffer.get(nv21Data, ySize, vSize)
        uBuffer.get(nv21Data, ySize + vSize, uSize)

        val yStride = yPlane.rowStride
        val uvStride = uPlane.rowStride
        val uvPixelStride = uPlane.pixelStride

        val processedBitmap = processYUVFrame(
            nv21Data,
            image.width,
            image.height,
            yStride,
            uvStride,
            uvPixelStride
        )

        runOnUiThread {
            processedBitmap?.let {
                processedImageView.setImageBitmap(it)

                val currentTime = System.currentTimeMillis()
                if (lastFrameTime > 0) {
                    val fps = 1000f / (currentTime - lastFrameTime)
                    tvStatus.text = "C++ EDGE DETECTION - FPS: ${fps.toInt()}"
                }
                lastFrameTime = currentTime
            }
        }
    }

    private external fun processYUVFrame(
        yuvData: ByteArray,
        width: Int,
        height: Int,
        yStride: Int,
        uvStride: Int,
        uvPixelStride: Int
    ): android.graphics.Bitmap?

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
    }

    override fun onPause() {
        super.onPause()
        closeCamera()
        stopBackgroundThread()
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
            backgroundHandler = null
        } catch (e: InterruptedException) {
            Log.e(TAG, "Error stopping background thread", e)
        }
    }

    private fun closeCamera() {
        cameraCaptureSession?.close()
        cameraCaptureSession = null
        cameraDevice?.close()
        cameraDevice = null
        imageReader?.close()
        imageReader = null
    }
}
