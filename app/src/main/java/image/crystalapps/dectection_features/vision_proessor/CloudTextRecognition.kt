package image.crystalapps.dectection_features.vision_proessor


import android.content.Context
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudTextRecognition(context: Context)  : VisionProcessorBase<FirebaseVisionText>(context) {

    private val detector = FirebaseVision.getInstance().cloudTextRecognizer


//    override fun getTranslateTextLiveData(): LiveData<String> = translateTextLiveData


    override fun stop() {
        super.stop()
        detector.close()

    }

    override fun detectInImage(image: FirebaseVisionImage): Task<FirebaseVisionText> {


        return detector.processImage(image)
    }


//    override fun onSuccess(results: FirebaseVisionText, graphicOverlay: GraphicOverlay) {}

    override fun onSuccess(results: FirebaseVisionText) {
        postData(results.text)
    }

    override fun onFailure(e: Exception) {
        val message = e.message

        println(message)
        if (message != null) {
            postData(NO_TEXT_FOUND)
        }
    }


    companion object {
        const val NO_TEXT_FOUND = "No text found try again"
    }

//    override fun requestDetectInImageFuture(firebaseVisionImage: FirebaseVisionImage): CompletableFuture<String> {
//        Log.i("Closed future","data test")
//        val scanCompletableFuture = CompletableFuture<String>()
//        detectInImage(firebaseVisionImage).addOnSuccessListener {
//
//            if (it != null) {
//
//
//                Log.i("Closed future","data ${it.text
//                }")
//                scanCompletableFuture.complete(it.text)
//            }
//        }.addOnFailureListener {
//            Log.i("Closed future","data ${it
//            }")
//
//            scanCompletableFuture.exceptionally {
//                it.message
//            }
//
//        }
//
//
//
//        return scanCompletableFuture
//    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun requestDetectInImageFlow(
        firebaseVisionImage: FirebaseVisionImage,
        imageProxy: ImageProxy
    ): String {


        // val data =   suspendCancellableCoroutine<String> {cancellableCoroutine ->



        detector.processImage(firebaseVisionImage).addOnSuccessListener {
            imageProxy.close()

            if (it != null) {
                //    deferred.complete(it.text)

                Log.i("Closed","data ${it.text
                }")
                //    cancellableCoroutine.resume(it.text)

                //   this.offer(it.text)
            }

        }.addOnFailureListener {
            imageProxy.close()

            //   cancellableCoroutine.resumeWithException(it)
            // deferred.completeExceptionally(it)
            //   this.close(it)
        }


        // }

        // CompletableDeferred<String>()

        //   val deferred  = CompletableDeferred<String>()

//
//        detector.processImage(firebaseVisionImage).addOnSuccessListener {
//            imageProxy.close()
//
//            if (it != null) {
//                deferred.complete(it.text)
//
//             //   this.offer(it.text)
//            }
//
//        }.addOnFailureListener {
//            imageProxy.close()
//
//        deferred.completeExceptionally(it)
//        //   this.close(it)
//        }
        return "data"}

    override suspend fun requestDetectTextFromImage(firebaseVisionImage: FirebaseVisionImage): String  {


        println("detect image ")
        return   suspendCancellableCoroutine<String > {callback ->

            detector.processImage(firebaseVisionImage).addOnSuccessListener {
                //  imageProxy.close()

                if (it != null) {


                    println("detected"+it.text)
                    callback.resume(it.text)

                } else {
                    callback.resume("No Text Found tr again")

                }

            }.addOnFailureListener {

                callback.resumeWithException(it )
                //   callback.resume(Result.onError)

            }

        }

    }


}