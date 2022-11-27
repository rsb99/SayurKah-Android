package com.reresb.sayurkah.helper

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class BitmapOutputAnalysis(context: Context, private val listener: CameraBitmapOutputListener): ImageAnalysis.Analyzer {
    private val yuvToRGBConverter = YuvToRGBConverter(context)
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var rotationMatrix: Matrix

    @SuppressLint("UnsafeOptInUsageError")
    private fun ImageProxy.toBitmap(): Bitmap? {
        val image: Image = this.image ?: return null
        if (!::bitmapBuffer.isInitialized){
            rotationMatrix = Matrix()
            rotationMatrix.postRotate(this.imageInfo.rotationDegrees.toFloat())
            bitmapBuffer = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        }
        yuvToRGBConverter.yuvToRgb(image, bitmapBuffer)
        return Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            rotationMatrix,
            false
        )
    }

    override fun analyze(image: ImageProxy) {
        image.toBitmap()?.let {
            listener(it)
        }
        image.close()
    }

}

typealias CameraBitmapOutputListener = (bitmap: Bitmap) -> Unit