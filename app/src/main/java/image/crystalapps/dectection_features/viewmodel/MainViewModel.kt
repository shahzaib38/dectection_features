package image.crystalapps.dectection_features.viewmodel


import androidx.hilt.lifecycle.ViewModelInject
import image.crystalapps.dectection_features.navigator.MainNavigator
import image.crystalapps.dectection_features.repo.MainRepository

class MainViewModel
@ViewModelInject constructor(private val mainRepository: MainRepository) :
    BaseViewModel<MainNavigator>(mainRepository) {










}