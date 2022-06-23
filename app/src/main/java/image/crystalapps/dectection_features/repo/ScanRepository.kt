package image.crystalapps.dectection_features.repo


import android.graphics.Bitmap
import androidx.camera.core.ImageProxy

import kotlinx.coroutines.flow.Flow
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor(
    private val visionImageProcessor: VisionImageProcessor ,

    ): BaseRepository() {


    suspend fun scanText(bitmap: Bitmap ) =visionImageProcessor.processImageProxy(bitmap)



    suspend fun scanText(image: ImageProxy) = visionImageProcessor.processImage(image)




}