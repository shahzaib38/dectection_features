package image.crystalapps.dectection_features


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object CameraPermissions {

    const val  CAMERA_PERMISSION :Int  = 0XFACE
    const val CAMERA_GALLERY_PERMISSION =0XABCD

    val CAMERA_PERMISSION_ARRAY : Array<String> = arrayOf(Manifest.permission.CAMERA)
    val GALLERY_ARRAY:Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @RequiresApi(Build.VERSION_CODES.N)
    fun hasPermissions(context: Context , permissions: Array<String>): Boolean {
        permissions.forEach { permission ->  //Lambda Trailing
            if(isPermissionGranted(context,permission)){
                return false } }
        return true }



    private fun isPermissionGranted(context: Context , permission :String) : Boolean{
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED }




    fun isCameraPermissionGranted(context: Context):Boolean{
        return hasPermissions(context, CameraPermissions.CAMERA_PERMISSION_ARRAY) }

    fun isGalleryPermissionGranted(context: Context ):Boolean{
        return hasPermissions(context, CameraPermissions.GALLERY_ARRAY) }


}