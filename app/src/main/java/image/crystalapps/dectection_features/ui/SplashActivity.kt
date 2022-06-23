package image.crystalapps.dectection_features.ui



import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import image.crystalapps.dectection_features.BR
import image.crystalapps.dectection_features.MainActivity
import image.crystalapps.dectection_features.R
import image.crystalapps.dectection_features.databinding.MainDataBinding
import image.crystalapps.dectection_features.ui.BaseActivity
import image.crystalapps.dectection_features.viewmodel.MainViewModel

@AndroidEntryPoint
class SplashActivity : BaseActivity<MainViewModel, MainDataBinding>() {

    private val mViewModel by viewModels<MainViewModel>()

    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.splash_activity
    override fun getViewModel(): MainViewModel   = mViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN ,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)


        Thread.sleep(10000)
        startActivity(Intent(this , MainActivity::class.java))

    }

}
