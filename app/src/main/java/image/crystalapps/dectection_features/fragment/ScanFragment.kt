package image.crystalapps.dectection_features.fragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import image.crystalapps.dectection_features.*
import image.crystalapps.dectection_features.CameraPermissions.isCameraPermissionGranted
import image.crystalapps.dectection_features.CameraPermissions.isGalleryPermissionGranted
import image.crystalapps.dectection_features.InternetConnection.isInternetAvailable
import image.crystalapps.dectection_features.R
import image.crystalapps.dectection_features.databinding.ScanFragmentDataBinding
import image.crystalapps.dectection_features.navigator.ScanNavigator
import image.crystalapps.dectection_features.viewmodel.ScanViewModel
import kotlinx.android.synthetic.main.camera_options.*
import java.io.FileNotFoundException
import java.util.Observer
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class ScanFragment : BaseFragment<ScanViewModel , ScanFragmentDataBinding>(),
    ValueAnimator.AnimatorUpdateListener , Animator.AnimatorListener
    , View.OnTouchListener , ScaleGestureDetector.OnScaleGestureListener, ScanNavigator {


    private val mViewModel by activityViewModels<ScanViewModel>()

    override fun getBindingVariable(): Int = BR.viewModel
    override fun getLayoutId(): Int = R.layout.scan_fragment
    override fun getViewModel(): ScanViewModel = mViewModel

    private var mScanFragmentDataBinding: ScanFragmentDataBinding? = null
    private var mMainActivity: MainActivity? = null
    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var windowManager: WindowManager

    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }


    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private const val RESULT_LOADING = 11122

    }

    var camera: Camera? = null
    val valueAnimator = ValueAnimator.ofFloat(1.0f, 0.8f)
    var scaleGestureDetector: ScaleGestureDetector? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("onViewCreated")
        mScanFragmentDataBinding = getViewDataBinding()


        val baseActivitty = getBaseActivity()
        if (baseActivitty is MainActivity) {
            mMainActivity = baseActivitty
        }

        // windowManager = androidx.window.WindowManager(view.context)

        scaleGestureDetector = ScaleGestureDetector(requireContext(), this)


        valueAnimator.duration = 350

        valueAnimator.addUpdateListener(this)
        valueAnimator.addListener(this)
        if (isCameraPermissionGranted(requireContext()))
            startCamera() else requestCameraPermission()
         mViewModel.setNavigator(this)

        mScanFragmentDataBinding?.include2?.previewFinder?.setOnTouchListener(this)

        // mViewModel.translateText.observe( viewLifecycleOwner , translatedObserver)

    }

    private fun startCamera() {


        mScanFragmentDataBinding?.include2?.previewFinder?.post {


            val mdisplay = mScanFragmentDataBinding?.include2?.previewFinder?.display

            if (mdisplay != null) {
                displayId = mdisplay.displayId
            }

            setUpCamera()
        }
    }

    private fun setUpCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

            println("Thread 2" + Thread.currentThread())

            cameraProvider = cameraProviderFuture.get()

            lensFacing = when {
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun bindCameraUseCases() {

        val windowManager =
            androidx.window.WindowManager(requireContext()).getCurrentWindowMetrics()
//         Get screen metrics used to setup camera for full screen resolution
        val metrics = windowManager.bounds

        val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())
        //     Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = mScanFragmentDataBinding?.include2?.previewFinder?.display?.rotation

        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // Preview
        val preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation ?: android.view.Surface.ROTATION_90)
            .build()

        // ImageCapture
        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation ?: android.view.Surface.ROTATION_90)
            .build()

        cameraProvider.unbindAll()

        try {
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            this.camera = camera
            cameraXLiveData(camera)
            captureImage(imageCapture)
            torch(camera)
//            displayListener(imageCapture)
//
//
//            mScanFragmentDataBinding?.scaleGestureDetector( requireContext(),camera)

            // Attach the viewfinder's surface provider to preview use case
            preview.setSurfaceProvider(mScanFragmentDataBinding?.include2?.previewFinder?.surfaceProvider)

        } catch (exc: Exception) {
            //   Log.e(CameraFragment.TAG, "Use case binding failed", exc)


        }
    }


    private fun requestGalleryPermission(){
        requestPermissions(CameraPermissions.GALLERY_ARRAY, CameraPermissions.CAMERA_GALLERY_PERMISSION)
    }
    private fun openIntent(){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, RESULT_LOADING) }


    override  fun openGallery(){

        println("Gallery ")

        if(isInternetAvailable(requireActivity())) {
            if (isGalleryPermissionGranted(requireContext()))
            { openIntent()
            } else { requestGalleryPermission() }

        }else{

     //       val action =  ScanNavigatorDirections.actionGlobalInternetConnectionDialog()
   //         findNavController().navigate(action)
//          mMainActivity?.internetConnectionDialog()

        }
    }


    private fun torch(camera : Camera){
        val cameraController =  camera.cameraControl
        val cameraInfo =camera.cameraInfo

        mScanFragmentDataBinding?.actionButtonId?.cameraOptionId?.torch?.setOnClickListener {
            when(cameraInfo.torchState.value) {
                0->{
                    cameraController.enableTorch(true) }

                1-> {
                    cameraController.enableTorch(false) }
                else-> {
                    cameraController.enableTorch(false) } }
        }


    }

    private fun captureImage(imageCapture: ImageCapture) {



        mScanFragmentDataBinding?.actionButtonId?.capture?.setOnClickListener {

            println("Visible")

            mScanFragmentDataBinding?.include2?.processId?.visibility = View.VISIBLE



            if (isInternetAvailable(requireActivity())) {
                imageCapture.takePicture(
                    Executors.newSingleThreadExecutor(),
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {

                            mScanFragmentDataBinding?.include2?.processId?.visibility = View.VISIBLE

                            println("Capture Success "+Thread.currentThread().name)
                                     mViewModel.scanText(visible = {



                                     } ,image , invisible = {

                                         println("invisible")

                                     }){

                                         println("Text generated"+it )

                                         println("onResult Success "+Thread.currentThread().name)

                                         mScanFragmentDataBinding?.include2?.processId?.visibility = View.INVISIBLE


                                         val action = ScanFragmentDirections.actionScanIdFragmentToTextDialog(it)

                                         findNavController().navigate(action)

                                     }



                        }

                        override fun onError(exception: ImageCaptureException) {

                            val message = exception.message
                            if (message != null) {

                                println("exception")


                            }
                        }
                    })
            } else {
                //     mMainActivity?.internetConnectionDialog()

                mScanFragmentDataBinding?.include2?.processId?.visibility = View.INVISIBLE

                //              val action =  ScanNavigatorDirections.actionGlobalInternetConnectionDialog()
//                findNavController().navigate(action)

            }
        }


    }

    private fun cameraXLiveData(camera: Camera) {
        val cameraController = camera.cameraInfo

   //      cameraController.zoomState.observe(viewLifecycleOwner, zoomState)
          cameraController.torchState.observe(viewLifecycleOwner, torchState)

    }


    private   fun  changeTorchState(state :Int = R.drawable.ic_flash_off){
        mScanFragmentDataBinding?.actionButtonId?.cameraOptionId?.torch?.setImageResource(state)


    }


    //TorchLiveData
    private  val torchState =  androidx.lifecycle.Observer<Int> { torchState ->
        if(torchState!=null) {
            when (torchState) {
                TorchState.OFF -> {
                    changeTorchState(R.drawable.ic_flash_off)
                }
                TorchState.ON -> {
                    changeTorchState(R.drawable.flashon)
                }
                else -> {
                    changeTorchState(R.drawable.ic_flash_off)
                }
            }

        }

    }




    private fun hasBackCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false
    }

    private fun hasFrontCamera(): Boolean {
        return cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {

    }

    override fun onAnimationStart(animation: Animator?) {

    }

    override fun onAnimationEnd(animation: Animator?) {

    }

    override fun onAnimationCancel(animation: Animator?) {

    }

    override fun onAnimationRepeat(animation: Animator?) {

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {


        return false
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {

        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {


        return false
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {

    }

    fun Fragment.requestCameraPermission() {
        requestPermissions(
            CameraPermissions.CAMERA_PERMISSION_ARRAY,
            CameraPermissions.CAMERA_PERMISSION
        )
    }


    fun Activity.requestCameraPermission() {
        requestPermissions(
            CameraPermissions.CAMERA_PERMISSION_ARRAY,
            CameraPermissions.CAMERA_PERMISSION
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            try {
                val dataImage = data?.data
                if(dataImage!=null) {
              val bitmap = BitmapUtils.getBitmap(requireContext() ,dataImage)
              mViewModel.scanText( visible = {
                  mScanFragmentDataBinding?.include2?.processId?.visibility = View.VISIBLE


              }, bitmap = bitmap, invisible = {
                  mScanFragmentDataBinding?.include2?.processId?.visibility = View.INVISIBLE

              }
              , text = {


                      val action  =   ScanFragmentDirections.actionScanIdFragmentToTextDialog(it)

                      findNavController().navigate(action )

                  }

              )

                } else{
                 //   mMainActivity?.toast("Image is not selected try again")



                  }

            } catch (e: FileNotFoundException) {
                e.printStackTrace() }
        } else {
          //  mMainActivity?.toast("You haven't picked Image")
        //


        }
    }





    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CameraPermissions.CAMERA_PERMISSION) {

            val cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (cameraPermission) startCamera() else requireActivity().finish()
        }else if(requestCode ==  CameraPermissions.CAMERA_GALLERY_PERMISSION){
            openIntent()}
    }



}