package com.rezaharisz.sayurkah.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import com.rezaharisz.sayurkah.R
import com.rezaharisz.sayurkah.databinding.ActivityCameraBinding
import com.rezaharisz.sayurkah.helper.BitmapOutputAnalysis
import com.rezaharisz.sayurkah.ml.Model
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_FRONT
    private var camera: Camera? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var model: Model

    companion object{
        private const val REQUEST_CODE_PERMISSION = 0x98
        private val REQUIRED_PERMISSIONS: Array<String> = arrayOf(Manifest.permission.CAMERA)
        private const val RATIO_4_3_VALUE: Double = (4.0 / 3.0)
        private const val RATIO_16_9_VALUE: Double = (16.0 / 9.0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (!allPermissionGranted){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION)
        } else{
            val options: org.tensorflow.lite.support.model.Model.Options = org.tensorflow.lite.support.model.Model.Options.Builder().setDevice(
                org.tensorflow.lite.support.model.Model.Device.GPU).setNumThreads(5).build()
            model = Model.newInstance(applicationContext, options)
            cameraExecutor = Executors.newSingleThreadExecutor()
            setCameraController()
            setCamera()
        }
    }

    private fun setMLOutput(bitmap: Bitmap){
        val tensorImage: TensorImage = TensorImage.fromBitmap(bitmap)
        val result: Model.Outputs = model.process(tensorImage)
        val output: List<Category> = result.probabilityAsCategoryList.apply {
            sortByDescending { it.score }
        }

        lifecycleScope.launch(Dispatchers.Main){
            output.firstOrNull()?.let {
                val percentage = (it.score / 1) * 100

                if (percentage > 40){
                    binding.tvOutput.text = StringBuilder().append(it.label).append(" ${
                        DecimalFormat(
                        "##"
                    ).format(percentage)} %")
                } else {
                    binding.tvOutput.text = ""
                }
            }
        }
    }

    private val allPermissionGranted: Boolean get() {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private val hasBackCamera: Boolean get() {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private val hasFrontCamera: Boolean get() {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    private fun aspectRatio(width: Int, height: Int): Int{
        val previewRatio: Double = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)){
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun setCameraUseCase(){
        val cameraSelector: CameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val metrics: DisplayMetrics = DisplayMetrics().also {
            binding.previewView.display.getRealMetrics(it)
        }
        val rotation: Int = binding.previewView.display.rotation
        val screenAspectRatio: Int = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, BitmapOutputAnalysis(applicationContext){ mlOutput ->
                    setMLOutput(mlOutput)
                })
            }
        cameraProvider?.unbindAll()

        try {
            camera = cameraProvider?.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )
            preview?.setSurfaceProvider(binding.previewView.surfaceProvider)
        } catch (e: Exception){
            Log.e("FAILURE_CAMERA", "Camera binding failure", e)
        }
    }

    private fun setCameraController(){
        binding.fbSwitchCamera.setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT){
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
            setCameraUseCase()
        }
        try {
            binding.fbSwitchCamera.isEnabled = hasBackCamera && hasFrontCamera
        } catch (e: CameraInfoUnavailableException){
            binding.fbSwitchCamera.isEnabled = false
            Log.e("FAILURE_CAMERA", "Camera failure : ", e)
        }
    }

    private fun setCamera(){
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            lensFacing = when{
                hasFrontCamera -> CameraSelector.LENS_FACING_FRONT
                hasBackCamera -> CameraSelector.LENS_FACING_BACK
                else -> throw IllegalStateException("No Camera Available")
            }
            setCameraController()
            setCameraUseCase()
        }, ContextCompat.getMainExecutor(this))
    }

}