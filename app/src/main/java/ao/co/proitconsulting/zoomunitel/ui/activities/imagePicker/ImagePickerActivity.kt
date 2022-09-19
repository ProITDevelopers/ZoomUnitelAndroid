package ao.co.proitconsulting.zoomunitel.ui.activities.imagePicker

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.databinding.ActivityImagePickerBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import java.io.File

class ImagePickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagePickerBinding
    companion object {
        val TAG = ImagePickerActivity::class.simpleName
        const val INTENT_IMAGE_PICKER_OPTION = "image_picker_option"
        const val INTENT_ASPECT_RATIO_X = "aspect_ratio_x"
        const val INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y"
        const val INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio"
        const val INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality"
        const val INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height"
        const val INTENT_BITMAP_MAX_WIDTH = "max_width"
        const val INTENT_BITMAP_MAX_HEIGHT = "max_height"

        const val REQUEST_IMAGE_CAPTURE = 0
        const val REQUEST_GALLERY_IMAGE = 1

        @JvmStatic
        fun showImagePickerOptions(context: Context, listener: PickerOptionListener){

            val title: SpannableString = SpannableString(context.getString(R.string.lbl_set_profile_photo))
            title.setSpan(
                ForegroundColorSpan(context.resources.getColor(R.color.white)),
                0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

//        // setup the alert builder
            val builder = AlertDialog.Builder(context,R.style.MyDialogTheme)
            builder.setTitle(title)

            // add a list
            val options = arrayOf(context.getString(R.string.lbl_take_camera_picture), context.getString(R.string.lbl_choose_from_gallery))

            builder.setItems(options, object : DialogInterface.OnClickListener{

                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when(which){
                        0 -> {
                            listener.onTakeCameraSelected()
                        }
                        1 -> {
                            listener.onChooseGallerySelected()
                        }
                    }
                }
            })


            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()

        }
    }

    private var lockAspectRatio: Boolean = false
    private var setBitmapMaxWidthHeight: Boolean = false
    private var ASPECT_RATIO_X: Int = 16
    private var ASPECT_RATIO_Y:Int = 9
    private var bitmapMaxWidth :Int = 1000
    private var bitmapMaxHeight :Int = 1000
    private var IMAGE_COMPRESSION :Int = 80

    private lateinit var fileName: String

    private val permissionsArray = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)

    interface PickerOptionListener {
        fun onTakeCameraSelected()

        fun onChooseGallerySelected()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        if (intent == null){
            MetodosUsados.showCustomSnackBar(binding.root,
            this,Constants.ToastALERTA,
            getString(R.string.toast_image_intent_null))
            return
        }

        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X)
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y)
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION)
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false)
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false)
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight);

        val requestCode = intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage()
        } else {
            chooseImageFromGallery()
        }
    }



    fun takeCameraImage(){


        Dexter.withContext(this@ImagePickerActivity)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                            fileName = System.currentTimeMillis().toString() + ".jpg"
                            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName))
                            if (takePictureIntent.resolveActivity(packageManager) != null) {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                            }
                        }else {
                            MetodosUsados.showCustomSnackBar(
                                binding.root,
                                this@ImagePickerActivity,
                                Constants.ToastALERTA,
                                getString(R.string.msg_permissao_armazenamento_continuar))
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // Remember to invoke this method when the custom rationale is closed
                    // or just by default if you don't want to use any custom rationale.
                    token?.continuePermissionRequest()
                }
            }).check()



    }


    fun chooseImageFromGallery(){

        Dexter.withContext(this@ImagePickerActivity)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                            val pickPhoto = Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
                        }else {
                            MetodosUsados.showCustomSnackBar(
                                binding.root,
                                this@ImagePickerActivity,
                                Constants.ToastALERTA,
                                getString(R.string.msg_permissao_armazenamento_continuar))
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // Remember to invoke this method when the custom rationale is closed
                    // or just by default if you don't want to use any custom rationale.
                    token?.continuePermissionRequest()
                }
            }).check()



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    cropImage(getCacheImagePath(fileName))
                } else {
                    setResultCancelled()
                }
            }
            REQUEST_GALLERY_IMAGE -> {
                if (resultCode == RESULT_OK) {
                    val imageUri: Uri? = data?.data
                    cropImage(imageUri!!)
                } else {
                    setResultCancelled()
                }
            }
            UCrop.REQUEST_CROP -> {
                if (resultCode == RESULT_OK) {
                    handleUCropResult(data!!)
                } else {
                    setResultCancelled()
                }
            }
            UCrop.RESULT_ERROR -> {
                val cropError:Throwable? = UCrop.getError(data!!)
                Log.e(TAG, "Crop error: $cropError")
                setResultCancelled()
            }
            else ->{
                setResultCancelled()
            }
        }
    }

    private fun cropImage(sourceUri: Uri){
        val destinationUri = Uri.fromFile(File(cacheDir, queryName(contentResolver, sourceUri)))
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)
        options.setToolbarTitle("Editar foto")
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary))

        if (lockAspectRatio)
            options.withAspectRatio(ASPECT_RATIO_X.toFloat(), ASPECT_RATIO_Y.toFloat())

        if (setBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);

        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .start(this)
    }

    private fun handleUCropResult(data:Intent) {
        if (data == null) {
            setResultCancelled()
            return
        }
        val resultUri:Uri = UCrop.getOutput(data)!!
        setResultOk(resultUri)
    }

    private fun setResultOk(imagePath:Uri) {
        val intent = Intent()
        intent.putExtra("path", imagePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setResultCancelled() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }
    private fun getCacheImagePath(fileName: String): Uri {
        val path = File(externalCacheDir, "camera")
        if (!path.exists())
            path.mkdirs()
        val imageFile = File(path, fileName!!)


        return getUriForFile(this, packageName + ".provider", imageFile)

    }





    private fun queryName(resolver: ContentResolver, uri:Uri):String{
        val returnCursor =
        resolver.query(uri, null, null, null, null)
        returnCursor.let{ cursor ->
            val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor?.moveToFirst()
            val name = cursor?.getString(nameIndex!!)
            cursor?.close()
            return name!!
        }

    }
}