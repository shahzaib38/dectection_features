package image.crystalapps.dectection_features.repo

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import java.util.concurrent.CompletableFuture


interface VisionImageProcessor {

    suspend fun processImageProxy(imageProxy: ImageProxy): String

    suspend fun processImage(imageProxy: ImageProxy):String

    suspend fun processImageProxy(bitmap: Bitmap) :String


    fun stop()

    fun getProgressLiveData(): LiveData<Boolean>
    fun getTranslateTextLiveData() : LiveData<String>
    fun clear()


    // fun processImgeProxyFlow( bitmap: Bitmap): Flow<String>
}
