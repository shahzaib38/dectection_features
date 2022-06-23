package image.crystalapps.dectection_features.viewmodel


import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import image.crystalapps.dectection_features.navigator.ScanNavigator
import image.crystalapps.dectection_features.repo.ScanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanViewModel  @ViewModelInject constructor(private val mainRepository : ScanRepository) : BaseViewModel<ScanNavigator>(mainRepository) {




    fun scanText(visible :()-> Unit  ,
                 imageProxy : ImageProxy ,
                 invisible :()->Unit  ,
                 update :(String) ->Unit ) {



        viewModelScope.launch(Dispatchers.IO) {

            try {

                val result = mainRepository.scanText(imageProxy)

                println("Resutl"+result )
                update(result)

            } catch (exception:Exception){

                val message = exception.message
                if (message != null) {
                  update("No Text found try again")

                }

            }finally {
                withContext(Dispatchers.Main) {
                    imageProxy.close()

               invisible.invoke()


                }

            }
      }
    }




    fun openGallery(){

        getNavigator().openGallery()

    }

    fun showSettings(){

    }


    fun scanText(visible :()->Unit  ,bitmap : Bitmap , text :(String)->Unit,

                 invisible: () -> Unit
                 ){

        viewModelScope.launch(Dispatchers.IO) {

            try {

              //  progress(true)
                val result = mainRepository.scanText(bitmap )


                println("Result "+Result )
             text(result)



            } catch (exception:Exception){


                val message = exception.message
                if (message != null) {
                    println("message "+message )

                    //    updateText("No Text found try again")
                }


            }finally {

                withContext(Dispatchers.Main) {
                //    getNavigator().progressInVisible()

                }

                //progress(false)

            }
        }
    }


    private  var mutableLiveData = MutableLiveData<String>()
    val translate : LiveData<String> = mutableLiveData


}