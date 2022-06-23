package image.crystalapps.dectection_features.vision_proessor



import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.GuardedBy
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import image.crystalapps.dectection_features.repo.VisionImageProcessor

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.CompletableFuture

abstract class VisionProcessorBase<T>(private val context: Context) : VisionImageProcessor {

    companion object {
        private const val TAG = "VisionProcessorBase"
    }


    private val mutableProgressLiveData = MutableLiveData<Boolean>()
    private val translateTextLiveData = MutableLiveData<String>("Scanning Text....")

    override fun getTranslateTextLiveData(): LiveData<String> = translateTextLiveData

    override fun getProgressLiveData(): LiveData<Boolean> {
        return mutableProgressLiveData }


    open fun postData(value:String ){
        translateTextLiveData.postValue(value )

    }


 //   private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    private var isShutdown = false


    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override suspend fun processImage(imageProxy : ImageProxy): String {

        if (isShutdown) {
            // return Result.onShutDown<String>("System is closed")

        }

        val mediaImage =  imageProxy.image
        val rotationDegree = imageProxy.imageInfo.rotationDegrees
        val degree = degreesToFirebaseRotation(rotationDegree)
        val firebaseVisionImage=   FirebaseVisionImage.fromMediaImage(mediaImage!!, degree)


        return  requestDetectInImage(firebaseVisionImage)
    }



    override suspend fun processImageProxy(bitmap: Bitmap) :String {

        if (isShutdown) {
            // return Result.onShutDown<String>("System is closed")

        }
        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)

        return  requestDetectInImage(firebaseVisionImage)
    }




    @GuardedBy("this")
    private var processingMetaData: FrameMetadata? = null


    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.") }


    @ExperimentalGetImage
    override suspend fun processImageProxy(image: ImageProxy) :String{

        Log.i("Closed","adad")

//  if (isShutdown) {
//   return
//  }


        postData("Scanning Text please wait...")

        mutableProgressLiveData.postValue(true)

        val mediaImage =  image.image
        val rotationDegree = image.imageInfo.rotationDegrees
        val degree = degreesToFirebaseRotation(rotationDegree)
        val firebaseVisionImage=   FirebaseVisionImage.fromMediaImage(mediaImage!!, degree)

        Log.i("Closed","second")

//  requestDetectInImage(firebaseVisionImage ).addOnCompleteListener {
//   image.close()
//  }

        return ""
    }

    override fun clear() {

        postData("Scanning Text please wait...")
    }

// override fun processImageProxy(bitmap: Bitmap) {
////  mutableProgressLiveData.postValue(true)
////  postData("Scanning Text please wait...")
////
////
////  val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
////  requestDetectInImage(firebaseVisionImage)
//
// }



// override fun processImgeProxyFlow( bitmap: Bitmap):CompletableFuture<String>{
//
//  println("processImageProxyFlow "+Thread.currentThread().name)
//  val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap)
//  return  requestDetectInImageFuture(firebaseVisionImage)
//
//
// }

    abstract suspend fun  requestDetectInImageFlow(firebaseVisionImage : FirebaseVisionImage,imageProxy: ImageProxy): String



    private suspend fun  requestDetectInImage(firebaseVisionImage : FirebaseVisionImage):String
    {

        return requestDetectTextFromImage(firebaseVisionImage)


    }

    protected abstract suspend fun requestDetectTextFromImage(firebaseVisionImage: FirebaseVisionImage ): String     //Template Method


// private fun requestDetectInImage(firebaseVisionImage : FirebaseVisionImage): Task<T> {
//
//  return detectInImage(firebaseVisionImage).addOnSuccessListener{ results: T ->
//
//
//   mutableProgressLiveData.postValue(false)
//""   this@VisionProcessorBase.onSuccess(results)
//
//  }.addOnFailureListener { e: Exception ->
//   mutableProgressLiveData.postValue(false)
//
//   ""
//  // this@VisionProcessorBase.onFailure(e)
//
//  }
//
// }

    override fun stop() {
      //  executor.shutdown()
        isShutdown = true
    }




    protected abstract fun detectInImage(image: FirebaseVisionImage ): Task<T>  //Template Method

//    protected abstract fun onSuccess(results: T, graphicOverlay: GraphicOverlay)   //Template Method
    protected abstract fun onSuccess(results: T)
    protected abstract fun onFailure(e: Exception)    //Template Method


}