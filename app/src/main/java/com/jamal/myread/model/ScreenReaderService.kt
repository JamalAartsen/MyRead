package com.jamal.myread.model

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.jamal.myread.NotificationUtils
import com.jamal.myread.databinding.ScreenReaderItemBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class ScreenReaderService : Service() {
    private lateinit var floatView: ViewGroup
    private lateinit var floatWindowLayoutParams: WindowManager.LayoutParams
    private var LAYOUT_TYPE: Int? = null
    private lateinit var windowManager: WindowManager
    private val TAG = "ScreenReaderService"

    private var mMediaProjection: MediaProjection? = null
    private var mStoreDir: String? = null
    private var mImageReader: ImageReader? = null
    private var mHandler: Handler? = null
    private var mDisplay: Display? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mDensity = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mRotation = 0
    private var mOrientationChangeCallback: OrientationChangeCallback? = null
    private var resultCode: Int? = null
    private var data: Intent? = null
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ScreenReaderItemBinding.inflate(inflater)
        floatView = binding.root

        binding.readerBtn.setOnClickListener {
            Toast.makeText(this, "This is a test!", Toast.LENGTH_SHORT).show()
            startProjection(resultCode!!, data)
            getImageFromExternalStorage()
            Handler(Looper.getMainLooper()).postDelayed({
                stopProjection()
                var image: InputImage? = null
                try {
                    image = InputImage.fromFilePath(
                        this, FileProvider.getUriForFile(
                            this, "com.example.android.fileprovider",
                            getImageFromExternalStorage()!!
                        )
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val result = recognizer.process(image!!)
                    .addOnSuccessListener { visionText ->
                        Log.d(TAG, "TASK COMPLETED!!! ${visionText.text}")
                        deleteAllImagesFromExternalStorage()
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "Task failed! $e")
                    }
            }, 500)

        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_PHONE
        }

        floatWindowLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_TYPE!!,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        floatWindowLayoutParams.apply {
            gravity = Gravity.CENTER
            x = 0
            y = -200
        }

        windowManager.addView(floatView, floatWindowLayoutParams)

        binding.closeBtn.setOnClickListener {
            stopSelf()
            windowManager.removeView(floatView)
        }

        val updatedFloatWindowLayoutParams = floatWindowLayoutParams
        var x = 0.0
        var y = 0.0
        var px = 0.0
        var py = 0.0

        binding.readerBtn.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = updatedFloatWindowLayoutParams.x.toDouble()
                        y = updatedFloatWindowLayoutParams.y.toDouble()

                        px = event.rawX.toDouble()
                        py = event.rawY.toDouble()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        updatedFloatWindowLayoutParams.x = (x + event.rawX - px).toInt()
                        updatedFloatWindowLayoutParams.y = (y + event.rawY - py).toInt()

                        windowManager.updateViewLayout(floatView, updatedFloatWindowLayoutParams)
                    }
                }
                return false
            }

        })

        // create store dir
        val externalFilesDir = getExternalFilesDir(null)
        if (externalFilesDir != null) {
            mStoreDir = externalFilesDir.absolutePath + "/screenshots/"
            val storeDirectory = File(mStoreDir)
            if (!storeDirectory.exists()) {
                val success = storeDirectory.mkdirs()
                if (!success) {
                    Log.e(TAG, "failed to create file storage directory.")
                    stopSelf()
                }
            }
        } else {
            Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.")
            stopSelf()
        }

        // start capture handling thread
        object : Thread() {
            override fun run() {
                Looper.prepare()
                mHandler = Handler(Looper.myLooper()!!)
                Looper.loop()
            }
        }.start()
    }

    /**
     * Deletes all images inside the folder.
     *
     * @author Jamal Aartsen
     */
    private fun deleteAllImagesFromExternalStorage() {
        val file = File(mStoreDir!!)
        val fileArray = file.listFiles()

        if (fileArray?.isEmpty() != null) {
            for (files in fileArray) {
                files.delete()
                Log.d(TAG, "deleteAllImagesFromExternalStorage: $files")
            }
        }
    }

    fun getImageFromExternalStorage(): File? {
        val file = File(mStoreDir!!)
        val fileArray = file.listFiles()
        var firstFile: File? = null

        if (fileArray?.isEmpty() != null) {
            for (files in fileArray) {
                firstFile = files
                Log.d(TAG, "getImageFromExternalStorage: $files")
                break
            }
        }
        
        return firstFile
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isStartCommand(intent)) {
            val notification = NotificationUtils.getNotification(this)
            startForeground(notification!!.first, notification!!.second)

            resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED)
            data = intent.getParcelableExtra<Intent>(DATA)

        } else if (isStopCommand(intent)) {
            stopProjection()
        } else {
            stopSelf()
        }
        return START_NOT_STICKY
    }

    fun startProjection(resultCode: Int, data: Intent?) {
        val mpManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        mMediaProjection = mpManager.getMediaProjection(resultCode, data!!)
        if (mMediaProjection != null) {
            // display metrics
            mDensity = Resources.getSystem().displayMetrics.densityDpi
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            val defaultDisplay =
                getSystemService<DisplayManager>()?.getDisplay(Display.DEFAULT_DISPLAY)
            mDisplay = defaultDisplay

            // create virtual display depending on device width / height
            createVirtualDisplay()

            // register orientation change callback
            mOrientationChangeCallback = OrientationChangeCallback(this)
            if (mOrientationChangeCallback!!.canDetectOrientation()) {
                mOrientationChangeCallback!!.enable()
            }

            // register media projection stop callback
            mMediaProjection!!.registerCallback(MediaProjectionStopCallback(), mHandler)
        }

    }


    fun stopProjection() {
        if (mHandler != null) {
            mHandler!!.post {
                if (mMediaProjection != null) {
                    mMediaProjection!!.stop()
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun createVirtualDisplay() {
        // get width and height
        mWidth = Resources.getSystem().displayMetrics.widthPixels
        mHeight = Resources.getSystem().displayMetrics.heightPixels

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 1)
        mVirtualDisplay = mMediaProjection!!.createVirtualDisplay(
            SCREENCAP_NAME, mWidth, mHeight,
            mDensity, virtualDisplayFlags, mImageReader!!.surface, null, mHandler
        )
        mImageReader!!.setOnImageAvailableListener(ImageAvailableListener(), mHandler)
    }

    private inner class ImageAvailableListener : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader) {
            var fos: FileOutputStream? = null
            var bitmap: Bitmap? = null
            try {
                mImageReader!!.acquireLatestImage().use { image ->
                    if (image != null) {
                        val planes = image.planes
                        val buffer = planes[0].buffer
                        val pixelStride = planes[0].pixelStride
                        val rowStride = planes[0].rowStride
                        val rowPadding = rowStride - pixelStride * mWidth

                        // create bitmap
                        bitmap = Bitmap.createBitmap(
                            mWidth + rowPadding / pixelStride,
                            mHeight,
                            Bitmap.Config.ARGB_8888
                        )
                        bitmap!!.copyPixelsFromBuffer(buffer)

                        // write bitmap to a file
                        fos =
                            FileOutputStream(mStoreDir + "/myscreen_" + IMAGES_PRODUCED + ".png")
                        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        IMAGES_PRODUCED++
                        Log.e(
                            TAG,
                            "captured image kayo: " + IMAGES_PRODUCED
                        )
                        Log.d(TAG, "onImageAvailable jamal: $fos")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (fos != null) {
                    try {
                        fos!!.close()
                    } catch (ioe: IOException) {
                        ioe.printStackTrace()
                    }
                }
                if (bitmap != null) {
                    bitmap!!.recycle()
                }
                Log.d(TAG, "onImageAvailable: hqhdwj")
            }
        }
    }

    private inner class OrientationChangeCallback(context: Context?) :
        OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            val rotation = mDisplay!!.rotation
            if (rotation != mRotation) {
                mRotation = rotation
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay!!.release()
                    if (mImageReader != null) mImageReader!!.setOnImageAvailableListener(null, null)

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private inner class MediaProjectionStopCallback : MediaProjection.Callback() {
        override fun onStop() {
            Log.e(TAG, "stopping projection.")
            mHandler!!.post {
                if (mVirtualDisplay != null) mVirtualDisplay!!.release()
                if (mImageReader != null) mImageReader!!.setOnImageAvailableListener(null, null)
                if (mOrientationChangeCallback != null) mOrientationChangeCallback!!.disable()
                mMediaProjection!!.unregisterCallback(this@MediaProjectionStopCallback)
            }
        }
    }

    companion object {
        private const val TAG = "ScreenCaptureService"
        private const val RESULT_CODE = "RESULT_CODE"
        private const val DATA = "DATA"
        private const val ACTION = "ACTION"
        private const val START = "START"
        private const val STOP = "STOP"
        private const val SCREENCAP_NAME = "screencap"
        private var IMAGES_PRODUCED = 0
        fun getStartIntent(context: Context?, resultCode: Int, data: Intent?): Intent {
            val intent = Intent(context, ScreenReaderService::class.java)
            intent.putExtra(ACTION, START)
            intent.putExtra(RESULT_CODE, resultCode)
            intent.putExtra(DATA, data)
            return intent
        }

        fun getStopIntent(context: Context?): Intent {
            val intent = Intent(context, ScreenReaderService::class.java)
            intent.putExtra(ACTION, STOP)
            return intent
        }

        private fun isStartCommand(intent: Intent): Boolean {
            return (intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA)
                    && intent.hasExtra(ACTION) && intent.getStringExtra(ACTION) == START)
        }

        private fun isStopCommand(intent: Intent): Boolean {
            return intent.hasExtra(ACTION) && intent.getStringExtra(ACTION) == STOP
        }

        private val virtualDisplayFlags: Int
            private get() = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
    }
}