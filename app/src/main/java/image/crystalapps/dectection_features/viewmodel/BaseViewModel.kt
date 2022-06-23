package image.crystalapps.dectection_features.viewmodel


import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import image.crystalapps.dectection_features.repo.BaseRepository
import java.lang.ref.WeakReference


abstract class BaseViewModel<N>  constructor(val baseDataManager : BaseRepository) :  ViewModel(){



    lateinit var  mNavigator  : WeakReference<N>

    fun setNavigator(mNavigator :N){
        this.mNavigator = WeakReference<N>(mNavigator)
    }

    open fun getNavigator() :N{

        return mNavigator.get()!!
    }


    public fun navigate(context : Context?, activityClass: Class<out Activity>?){

        context?.startActivity(Intent(context, activityClass))


    }

}