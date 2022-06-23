package image.crystalapps.dectection_features.di


import android.content.Context
//import com.google.auth.oauth2.GoogleCredentials
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import image.crystalapps.dectection_features.repo.VisionImageProcessor
import image.crystalapps.dectection_features.vision_proessor.CloudTextRecognition
import java.io.InputStream
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class AppModule {


    @Provides
    @Singleton
    fun getString():String {
        return "shahzaib" }


//    @Provides
//    fun getRefreshToken(@ApplicationContext context  : Context) : GoogleCredentials {
//        val stream: InputStream = context.resources.openRawResource(R.raw.credential)
//        return GoogleCredentials.fromStream(stream)
//            .createScoped(mutableListOf("https://www.googleapis.com/auth/androidpublisher"))
//
//
//          }
//
//


    @Provides
    @Singleton
    fun provideVisionImageProcessor(@ApplicationContext context : Context) :
            VisionImageProcessor = CloudTextRecognition(context)

    @Singleton
    @Provides
    fun getRecognizer():TextRecognizer{

        return  TextRecognition.getClient()
    }



}
