package image.crystalapps.dectection_features.ui


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel


abstract class BaseActivity<out VM : ViewModel,out VDB : ViewDataBinding> : AppCompatActivity(){


    private var progressBar : ProgressBar?=null
    private  var mViewDataBinding :VDB?=null

    private  var mViewModel :VM? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding() }



    abstract fun getBindingVariable(): Int

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract  fun getViewModel() :VM


    open  fun setProgressBar(progress : ProgressBar){
        progressBar =progress
    }

    open  fun showProgressBar(){
        if(progressBar!=null) {
            progressBar?.visibility = View.VISIBLE
        }

    }


    open fun hideProgressBar(){

        if(progressBar!=null) {
            progressBar?.visibility = View.INVISIBLE
        }

    }


    override fun onStop() {
        super.onStop()
        hideProgressBar() }

    open fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    open fun getViewDataBinding(): VDB? {
        return mViewDataBinding }


    private fun performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        this.mViewModel = if (mViewModel == null) getViewModel() else mViewModel
        mViewDataBinding?.run {
            setVariable(getBindingVariable(), mViewModel)
            executePendingBindings()
        }
    }



    open  fun newActivity(baseActivity : BaseActivity<*, *>, type :Class<Any>){
        val intent=   Intent(baseActivity,type)
        startActivity(intent) }



    fun shareSingleImage(imageUri :Uri){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/jpeg"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun sharePdfFile(pdfUri :Uri){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            type = "application/pdf"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }



    fun shareData(value :String ="no value was shared"){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, value)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}