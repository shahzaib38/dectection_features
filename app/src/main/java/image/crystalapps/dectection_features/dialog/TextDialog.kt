package image.crystalapps.dectection_features.dialog

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import image.crystalapps.dectection_features.BR
import image.crystalapps.dectection_features.R
import image.crystalapps.dectection_features.databinding.TextDataBinding
import image.crystalapps.dectection_features.viewmodel.ScanViewModel

@AndroidEntryPoint
class TextDialog : BaseDialogFragment<ScanViewModel , TextDataBinding>() {


    private val args by  navArgs<TextDialogArgs>()

    private val mViewModel by activityViewModels<ScanViewModel>()

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getViewModel(): ScanViewModel? = mViewModel

    override fun getLayoutId(): Int  = R.layout.text_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

    val binding =  getViewDataBinding()
        binding?.textId?.text =    args.text


        binding?.closeId?.setOnClickListener {
            dismiss() }

    }

}