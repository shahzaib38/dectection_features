package image.crystalapps.dectection_features

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import image.crystalapps.dectection_features.databinding.MainDataBinding
import image.crystalapps.dectection_features.ui.BaseActivity
import image.crystalapps.dectection_features.viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, MainDataBinding>() {



    private val mViewModel by viewModels<MainViewModel>()
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.activity_main
    override fun getViewModel(): MainViewModel   = mViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

    }

}
