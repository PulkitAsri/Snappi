package com.example.snapchatclone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.camera_fragment.*
import kotlinx.android.synthetic.main.camera_fragment.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.camera.core.ImageCapture.Metadata


typealias LumaListener = (luma: Double) -> Unit

class CameraFragment: Fragment() {

    lateinit var logoutButton:Button
    lateinit var findUsersButton:Button
    lateinit var flipCameraButton:Button
    lateinit var goToStory:Button
    lateinit var goToChat:Button


    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA


    fun newInstance():CameraFragment{
        return CameraFragment()
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View =inflater.inflate(R.layout.camera_fragment,container,false)

        findUsersButton=view.findViewById(R.id.findUserButton)
        logoutButton=view.findViewById(R.id.logoutButton)
        flipCameraButton=view.findViewById(R.id.flipCamera)
        goToChat=view.findViewById(R.id.goToChat)
        goToStory=view.findViewById(R.id.goToStory)




        findUsersButton.setOnClickListener {
            findUsers()
        }
        logoutButton.setOnClickListener {
            logout()
        }
        flipCameraButton.setOnClickListener{
            flipCamera()
        }
        goToStory.setOnClickListener{

        }
        goToChat.setOnClickListener{

        }



        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button
        view.camera_capture_button.setOnClickListener { takePhoto() }
        outputDirectory = getOutputDirectory(context!!)
        cameraExecutor = Executors.newSingleThreadExecutor()
        return view

    }
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis()) + ".jpg")

        val metadata =Metadata().apply {
            isReversedHorizontal = (lensFacing== CameraSelector.DEFAULT_FRONT_CAMERA)
        }

        //Output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).setMetadata(metadata)
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Log.d(TAG, msg)
                    val intent=Intent(context, CapturedImageActivity::class.java)
                    intent.putExtra("imageUri",savedUri)
                    startActivity(intent)
                }
            })
    }

    private fun startCamera() {

        bindCameraUseCases()
    }

    private fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context!!)
        cameraProviderFuture.addListener(Runnable {

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = lensFacing
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview,imageCapture)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))


    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context!!, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()

    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())mediaDir else appContext.filesDir
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(context, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        Firebase.auth.signOut()
        val intent=Intent(context,SplashScreenActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return
    }

    private fun findUsers() {
        val intent=Intent(context,FindUsersActivity::class.java)
        startActivity(intent)
        return

    }
    private fun flipCamera(){
        lensFacing=if(CameraSelector.DEFAULT_FRONT_CAMERA==lensFacing) CameraSelector.DEFAULT_BACK_CAMERA
        else CameraSelector.DEFAULT_FRONT_CAMERA

        bindCameraUseCases()
    }


}


