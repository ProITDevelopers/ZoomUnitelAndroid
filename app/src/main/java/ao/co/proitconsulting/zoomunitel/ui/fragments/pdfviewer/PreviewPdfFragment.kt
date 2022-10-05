

package ao.co.proitconsulting.zoomunitel.ui.fragments.pdfviewer

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.prelollipop.GetUnsafeOkHttpClientSecurity
import ao.co.proitconsulting.zoomunitel.databinding.FragmentPreviewPdfBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.models.RevistaModel
import ao.co.proitconsulting.zoomunitel.ui.activities.MainActivity
import com.github.barteksc.pdfviewer.PDFView
import com.github.ybq.android.spinkit.SpinKitView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference

@Suppress("DEPRECATION")
class PreviewPdfFragment : Fragment() {

    private val TAG = "TAG_Pdf"
    private var _binding: FragmentPreviewPdfBinding? = null
    private val binding get() = _binding!!

    private val args: PreviewPdfFragmentArgs by navArgs()


    private lateinit var coordinatorLayout: ConstraintLayout
    private lateinit var errorLayout: RelativeLayout
    private lateinit var imgErro: ImageView
    private lateinit var txtErro: TextView

    private lateinit var revista: RevistaModel
    private lateinit var pdfView: PDFView
    private lateinit var progressBar: SpinKitView
    private lateinit var txtProgress: TextView

    private lateinit var linearPageSectios: ConstraintLayout
    private lateinit var txtPosition: TextView
    private lateinit var imgFirstPage: ImageView
    private lateinit var imgPreviousPage: ImageView
    private lateinit var imgNextPage: ImageView
    private lateinit var imgLastPage: ImageView

    //NORMAL_DRAWABLE_COLORS
    private lateinit var firstPageDrawable: Drawable
    private lateinit var previousPageDrawable: Drawable
    private lateinit var nextPageDrawable: Drawable
    private lateinit var lastPageDrawable: Drawable

    //GRAY_DRAWABLE_COLORS
    private lateinit var firstPageDrawableGray: Drawable
    private lateinit var previousPageDrawableGray: Drawable
    private lateinit var nextPageDrawableGray: Drawable
    private lateinit var lastPageDrawableGray: Drawable


    private var currentPageNumber:Int=0
    private var lastPageNumber:Int=0
    private var myAsyncTasks = arrayListOf<AsyncTask<*,*,*>>()

    private fun cancelRunningTasks() {
        for ( myAsyncTask in myAsyncTasks) {
            if (myAsyncTask.status.equals(AsyncTask.Status.RUNNING)) {
                myAsyncTask.cancel(true)
            }
        }
        myAsyncTasks.clear()
    }

    private fun addRunningTask(task:AsyncTask<*,*,*>) {
        myAsyncTasks.add(task)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPageNumber",currentPageNumber)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        revista = args.revista
        if (savedInstanceState!=null){
            currentPageNumber = savedInstanceState.getInt("currentPageNumber",-1)
            if (currentPageNumber == -1)
                currentPageNumber = 0
        }

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPreviewPdfBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val frameLayout = MainActivity.getFrameLayoutImgToolbar()
        if (frameLayout != null)
            frameLayout.visibility = View.VISIBLE

        Log.d(TAG, "Oncreate: isNetworkAvailable: ${Constants.isNetworkAvailable}")

        coordinatorLayout = root.findViewById(R.id.coordinatorLayout)
        errorLayout = root.findViewById(R.id.erroLayout)
        imgErro = root.findViewById(R.id.imgErro)
        txtErro = root.findViewById(R.id.txtErro)

        pdfView = binding.pdfViewer
        progressBar = binding.spinKitBottom
        txtProgress = binding.txtProgress

        linearPageSectios = binding.linearPageSectios
        txtPosition = binding.txtPosition
        imgFirstPage = binding.imgFirstPage
        imgPreviousPage = binding.imgPreviousPage
        imgNextPage = binding.imgNextPage
        imgLastPage = binding.imgLastPage


        context.let {
            firstPageDrawable = resources.getDrawable(R.drawable.ic_baseline_first_page_24)!!
            previousPageDrawable = resources.getDrawable(R.drawable.ic_baseline_previous_page_24)!!
            nextPageDrawable = resources.getDrawable(R.drawable.ic_baseline_next_page_24)!!
            lastPageDrawable = resources.getDrawable(R.drawable.ic_baseline_last_page_24)!!

            firstPageDrawableGray = resources.getDrawable(R.drawable.ic_baseline_first_page_24)!!
            previousPageDrawableGray = resources.getDrawable(R.drawable.ic_baseline_previous_page_24)!!
            nextPageDrawableGray = resources.getDrawable(R.drawable.ic_baseline_next_page_24)!!
            lastPageDrawableGray = resources.getDrawable(R.drawable.ic_baseline_last_page_24)!!

            firstPageDrawable = DrawableCompat.wrap(firstPageDrawable)
            previousPageDrawable = DrawableCompat.wrap(previousPageDrawable)
            nextPageDrawable = DrawableCompat.wrap(nextPageDrawable)
            lastPageDrawable = DrawableCompat.wrap(lastPageDrawable)

            firstPageDrawableGray = DrawableCompat.wrap(firstPageDrawableGray)
            previousPageDrawableGray = DrawableCompat.wrap(previousPageDrawableGray)
            nextPageDrawableGray = DrawableCompat.wrap(nextPageDrawableGray)
            lastPageDrawableGray = DrawableCompat.wrap(lastPageDrawableGray)

            DrawableCompat.setTint(firstPageDrawable,resources.getColor(R.color.orange_unitel))
            DrawableCompat.setTintMode(firstPageDrawable, PorterDuff.Mode.SRC_IN)

            DrawableCompat.setTint(previousPageDrawable,resources.getColor(R.color.orange_unitel))
            DrawableCompat.setTintMode(previousPageDrawable, PorterDuff.Mode.SRC_IN)

            DrawableCompat.setTint(nextPageDrawable,resources.getColor(R.color.orange_unitel))
            DrawableCompat.setTintMode(nextPageDrawable, PorterDuff.Mode.SRC_IN)

            DrawableCompat.setTint(lastPageDrawable,resources.getColor(R.color.orange_unitel))
            DrawableCompat.setTintMode(lastPageDrawable, PorterDuff.Mode.SRC_IN)


            DrawableCompat.setTint(firstPageDrawableGray,resources.getColor(R.color.gray_color))
            DrawableCompat.setTintMode(firstPageDrawableGray, PorterDuff.Mode.SRC_IN)

            DrawableCompat.setTint(previousPageDrawableGray,resources.getColor(R.color.gray_color))
            DrawableCompat.setTintMode(previousPageDrawableGray, PorterDuff.Mode.SRC_IN)

            DrawableCompat.setTint(nextPageDrawableGray,resources.getColor(R.color.gray_color))
            DrawableCompat.setTintMode(nextPageDrawableGray, PorterDuff.Mode.SRC_IN)

            DrawableCompat.setTint(lastPageDrawableGray,resources.getColor(R.color.gray_color))
            DrawableCompat.setTintMode(lastPageDrawableGray, PorterDuff.Mode.SRC_IN)
        }

        imgFirstPage.setImageDrawable(firstPageDrawableGray)
        imgFirstPage.isEnabled = false

        imgPreviousPage.setImageDrawable(previousPageDrawableGray)
        imgPreviousPage.isEnabled = false

        imgNextPage.setImageDrawable(nextPageDrawable)
        imgNextPage.isEnabled = true

        imgLastPage.setImageDrawable(lastPageDrawable)
        imgLastPage.isEnabled = true

        imgFirstPage.setOnClickListener {
            pdfView.jumpTo(0)
        }

        imgPreviousPage.setOnClickListener {
            pdfView.jumpTo(currentPageNumber-1,true)
        }

        imgNextPage.setOnClickListener {
            pdfView.jumpTo(currentPageNumber+1,true)

        }

        imgLastPage.setOnClickListener {
            pdfView.jumpTo(lastPageNumber-1)
        }

        pdfView.setOnClickListener {
            showAndHideViews()
        }

//        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                // in here you can do logic when backPress is clicked
//                try {
//                    if (((activity as MainActivity)).supportActionBar != null){
//                        if (!((activity as MainActivity)).supportActionBar!!.isShowing){
//                            ((activity as MainActivity)).supportActionBar!!.show()
//                            linearPageSectios.visibility = View.VISIBLE
//
//                        }else{
//                            findNavController().navigateUp()
//                        }
//                    }
//                } catch (e: Exception) {
//
//                }
//
//
//
//            }
//        })



        checkForStoragePermission()

        return root
    }

    private fun showAndHideViews() {
        if (((activity as MainActivity)).supportActionBar != null){
            if (((activity as MainActivity)).supportActionBar!!.isShowing){
                ((activity as MainActivity)).supportActionBar!!.hide()
                linearPageSectios.visibility = View.GONE
            }else{
                ((activity as MainActivity)).supportActionBar!!.show()
                linearPageSectios.visibility = View.VISIBLE
            }

        }
    }

    private fun checkForStoragePermission() {
        Dexter.withContext(requireContext())
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                            verificarConnecxao()
                        }else {
                            MetodosUsados.showCustomSnackBar(
                                view,requireActivity(),Constants.ToastALERTA,
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

    private fun verificarConnecxao() {


        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                + File.separator + "ZoOM_Unitel")

        val storageDir = folder.absolutePath

        if (!folder.exists())
            folder.mkdirs()

        var nomePDF = MetodosUsados.removeAcentos(revista.title)
        nomePDF = nomePDF.replace("\\s+".toRegex(),"_")
        nomePDF = nomePDF.replace("/","_")
        nomePDF = nomePDF.lowercase()
        Constants.PDF_FILE_NAME = File.separator+nomePDF+".pdf"

        Log.d(TAG, "PDF_FILE_NAME: ${Constants.PDF_FILE_NAME}")

        val fileSavedDoc = File(storageDir+ Constants.PDF_FILE_NAME)
        if (fileSavedDoc.isFile) {
            progressBar.visibility = View.GONE
            showPDfViewer(fileSavedDoc)
        } else {

            if (Constants.isNetworkAvailable){
                carregarPDF()

            }else{
                showErrorScreen()

            }
        }





    }

    private fun showErrorScreen(){

        imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24)
        txtErro.text = getString(R.string.msg_erro_internet)

        if (errorLayout.visibility == View.GONE){
            errorLayout.visibility = View.VISIBLE
            coordinatorLayout.visibility = View.GONE
        }
    }

    private fun carregarPDF() {
        progressBar.visibility = View.VISIBLE
        txtProgress.visibility = View.GONE
        val pdfUrl = Uri.parse(Constants.PDF_PATH + revista.pdfLink)

        val downloadTask = DownloadRetrofitTask(this@PreviewPdfFragment)
        downloadTask.execute(pdfUrl.toString())
        addRunningTask(downloadTask)

//        val retrofit = RetrofitInstance.api.downloadPdfFile(pdfUrl.toString())
//        retrofit.enqueue(object :
//            Callback<ResponseBody> {
//
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//
//                    val downloadTask = DownloadRetrofitTask(this@PreviewPdfFragment)
//                    downloadTask.execute(response.body())
//                    addRunningTask(downloadTask)
////                    writeToFile(response.body()!!)
//
//                } else{
//
//                    progressBar.visibility = View.GONE
//                    MetodosUsados.showCustomSnackBar(coordinatorLayout,requireActivity(),Constants.ToastALERTA,getString(R.string.msg_erro_servidor))
//                }
//
//            }
//
//
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.d(TAG, "onFailure: ${t.message}")
//                progressBar.visibility = View.GONE
//
////                isNetworkAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                    isNetworkAvailable
////                }else{
////                    MetodosUsados.hasInternetConnection(requireContext())
////                }
//
//                if (!Constants.isNetworkAvailable){
//                    MetodosUsados.showCustomSnackBar(coordinatorLayout,requireActivity(),Constants.ToastALERTA,getString(R.string.msg_erro_internet))
//                }else if (!t.message.isNullOrEmpty() && t.message!!.contains("timeout")){
//                    MetodosUsados.showCustomSnackBar(coordinatorLayout,requireActivity(),Constants.ToastALERTA,getString(R.string.msg_erro_internet_timeout))
//                }else{
//                    MetodosUsados.showCustomSnackBar(coordinatorLayout,requireActivity(),Constants.ToastALERTA,getString(R.string.msg_erro_servidor))
//                }
//            }
//
//        })
    }


//    private fun writeToFile(response: ResponseBody) {
//        thread {
//            var pdf_File: File? = null
//            lateinit var inputStream:InputStream
//            lateinit var outputStream:FileOutputStream
//
//            try {
//                val fileLength = response.contentLength()
//
//                //download the file
//                inputStream = response.byteStream()
//                val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
//                        + File.separator + "ZoOM_Unitel")
//
//                val storageDir = folder.absolutePath
//
//                if (!folder.exists())
//                    folder.mkdirs()
//
//                pdf_File = File(storageDir+ Constants.PDF_FILE_NAME)
//
//                outputStream = FileOutputStream(pdf_File)
//
//                val data = ByteArray(4096)
//                var total: Long = 0
//                var count :Int=0
//                var progressResult:Long =0
//
//                while ((inputStream.read(data).also { count = it }) != -1){
//                    if (isCancelled) {
//                        inputStream.close()
//
//                    }
//                    total += count.toLong()
//                    // publishing the progress....
//                    if (fileLength > 0) { // only if total length is known
//                        progressResult = (total * 100 / fileLength)
//
//                        requireActivity().runOnUiThread {
//                            progressResult.let { progress->
//                                if (progress.toInt()<=100){
//                                    txtProgress.text = StringBuilder(progress.toInt()).append("%").toString()
//                                }else{
//                                    txtProgress.text = "100%"
//                                    txtProgress.visibility = View.GONE
//                                    progressBar.visibility = View.GONE
//                                }
//                            }
//                        }
//
//                    }
//
//                    outputStream.write(data, 0, count)
//
//                }
//
//            } catch (e: Exception) {
//            }finally {
//                if(isCancelled)
//                {
//                    // delete file code here
//                    pdf_File?.delete()
//
//                }
//                try {
//                    if (outputStream != null)
//                        outputStream.close()
//                    if (inputStream != null)
//                        inputStream.close()
//                } catch (ignored: IOException) {
//                }
//
//
//            }
//            requireActivity().runOnUiThread() {
//                pdf_File.let { fileResult->
//                    if (fileResult == null){
////                pdfView.visibility = (View.GONE)
////                MetodosUsados.showCustomSnackBar(getString(R.string.msg_nenhuma_informacao))
//
//                    }else if (fileResult.length().toInt() == 0) {
////                pdfView.setVisibility(View.GONE);
////                mostrarMensagemPopUp(getString(R.string.msg_nenhuma_informacao));
//
//                    }
//
//                   showPDfViewer(fileResult!!)
//                }
//            }
//
//
//        }
//
//
//    }




    //    class DownloadRetrofitTask(pdfFragment:PreviewPdfFragment) : AsyncTask<ResponseBody,Int?,File?>() {
    @Suppress("DEPRECATION")
    private class DownloadRetrofitTask(pdfFragment:PreviewPdfFragment) : AsyncTask<String?,Int?,File?>() {

        private  var activityWeakReference : WeakReference<PreviewPdfFragment>?=null

        init{

            this.activityWeakReference = WeakReference<PreviewPdfFragment>(pdfFragment)
        }


        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            val pdfFragment: PreviewPdfFragment? = activityWeakReference?.get()
            if (pdfFragment == null || pdfFragment.isDetached){
                return
            }
            pdfFragment.txtProgress.visibility = View.VISIBLE
            pdfFragment.txtProgress.text = "0%"
            super.onPreExecute()

        }



//        override fun doInBackground(vararg response: ResponseBody?): File? {
//
//            var pdf_File: File? = null
//            lateinit var inputStream:InputStream
//            lateinit var outputStream:FileOutputStream
//
//
//            try {
//                val fileLength = response[0]!!.contentLength()
//
//                //download the file
//                inputStream = response[0]!!.byteStream()
//                val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
//                        + File.separator + "ZoOM_Unitel")
//
//                val storageDir = folder.absolutePath
//
//                if (!folder.exists())
//                    folder.mkdirs()
//
//                pdf_File = File(storageDir+ Constants.PDF_FILE_NAME)
//
//                outputStream = FileOutputStream(pdf_File)
//
//                val data = ByteArray(1024 * 4)
//                var total: Long = 0
//                var count :Int=0
//                var progressResult:Long =0
//
//
//
//                while ((inputStream.read(data).also { count = it }) != -1){
//                    if (isCancelled) {
//                        inputStream.close()
//                        return null
//                    }
//                    total += count.toLong()
//                    // publishing the progress....
//                    if (fileLength > 0) { // only if total length is known
//                        progressResult = (total * 100 / fileLength)
//                        publishProgress(progressResult.toInt())
//                    }
//
//                    outputStream.write(data, 0, count)
//
//                }
//
//            } catch (e: Exception) {
//            }finally {
//                if(isCancelled)
//                {
//                    // delete file code here
//                    pdf_File?.delete()
//
//                }
//                try {
//                    if (outputStream != null)
//                        outputStream.close()
//                    if (inputStream != null)
//                        inputStream.close()
//                } catch (ignored: IOException) {
//                }
//
//
//            }
//            return pdf_File
//        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg sUrl: String?): File? {

            var pdf_File: File? = null
            lateinit var inputStream:InputStream
            lateinit var outputStream:FileOutputStream

            val httpClient = GetUnsafeOkHttpClientSecurity.getUnsafeOkHttpClient()
            val okHttpClient = httpClient.build()
            val call = okHttpClient.newCall(Request.Builder().url(sUrl[0]!!).get().build())

            try {
                val response = call.execute()
                if (response.isSuccessful && response.body() != null){
                    val fileLength = response.body()!!.contentLength()

                    //download the file
                    inputStream = response.body()!!.byteStream()
                    val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                            + File.separator + "ZoOM_Unitel")

                    val storageDir = folder.absolutePath

                    if (!folder.exists())
                        folder.mkdirs()

                    pdf_File = File(storageDir+ Constants.PDF_FILE_NAME)

                    outputStream = FileOutputStream(pdf_File)

                    val data = ByteArray(1024 * 4)
                    var total: Long = 0
                    var count :Int
                    var progressResult:Long



                    while ((inputStream.read(data).also { count = it }) != -1){
                        if (isCancelled) {
                            inputStream.close()
                            return null
                        }
                        total += count.toLong()
                        // publishing the progress....
                        if (fileLength > 0) { // only if total length is known
                            progressResult = (total * 100 / fileLength)
                            publishProgress(progressResult.toInt())
                        }

                        outputStream.write(data, 0, count)

                    }
                }


            } catch (e: Exception) {
            }finally {
                if(isCancelled)
                {
                    // delete file code here
                    pdf_File?.delete()

                }
                try {
                    if (outputStream != null)
                        outputStream.close()
                    if (inputStream != null)
                        inputStream.close()
                } catch (ignored: IOException) {
                }


            }
            return pdf_File
        }


        @Deprecated("Deprecated in Java")
        override fun onProgressUpdate(vararg progress: Int?) {
            val pdfFragment: PreviewPdfFragment? = activityWeakReference?.get()
            if (pdfFragment == null || pdfFragment.isDetached){
                return
            }
            Log.d(pdfFragment.TAG, "onProgressUpdate: ${progress[0]}%")
            pdfFragment.txtProgress.text = StringBuilder().append(progress[0]!!).append("%").toString()
            super.onProgressUpdate(*progress)

        }


        @Deprecated("Deprecated in Java")
        override fun onPostExecute(fileResult: File?) {
            val pdfFragment: PreviewPdfFragment? = activityWeakReference?.get()
            if (pdfFragment == null || pdfFragment.isDetached){
                return
            }
            pdfFragment.progressBar.visibility = View.GONE
            pdfFragment.txtProgress.visibility = View.GONE

            if (fileResult == null){
//                pdfView.visibility = (View.GONE)
//                MetodosUsados.showCustomSnackBar(getString(R.string.msg_nenhuma_informacao))
                return
            }else if (fileResult.length().toInt() == 0) {
//                pdfView.setVisibility(View.GONE);
//                mostrarMensagemPopUp(getString(R.string.msg_nenhuma_informacao));
                return
            }

            pdfFragment.showPDfViewer(fileResult)
            super.onPostExecute(fileResult)

        }



    }






    private fun showPDfViewer(fileResult: File) {
        pdfView.fromFile(fileResult)
            .password(null) // If have password
            .defaultPage(currentPageNumber) // Open default page, you can remember this value to open from the last time
            .enableSwipe(true)
            .swipeHorizontal(true)
            .pageSnap(true)
            .autoSpacing(true)
            .pageFling(true)
            .enableDoubletap(true) // Double tap to zoom
            .onDraw { canvas, pageWidth, pageHeight, displayedPage -> }
            .onDrawAll { canvas, pageWidth, pageHeight, displayedPage -> }
            .onPageError { page, t ->
                Toast.makeText(
                    requireContext(),
                    "Erro ao abrir a página $page",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .onPageChange { page, pageCount ->
                currentPageNumber = page
                Log.d("TAG_Pdf", "onPageChanged: page/pageCount: $page/$pageCount")
                txtPosition.text = (String.format("%s / %s", page + 1, pageCount))

                changePageListener()
            }
            .onTap { true }
            .onRender { nbPages -> lastPageNumber = nbPages }
            .enableAnnotationRendering(true)
//                .invalidPageColor(Color.WHITE)
            .load()

        txtPosition.visibility = (View.VISIBLE)
    }

    private fun changePageListener() {
        if (currentPageNumber==0){

            imgFirstPage.setImageDrawable(firstPageDrawableGray)
            imgFirstPage.isEnabled = false

            imgPreviousPage.setImageDrawable(previousPageDrawableGray)
            imgPreviousPage.isEnabled = false

            imgNextPage.setImageDrawable(nextPageDrawable)
            imgNextPage.isEnabled = true

            imgLastPage.setImageDrawable(lastPageDrawable)
            imgLastPage.isEnabled = true

        }
        if (currentPageNumber>0 && currentPageNumber<lastPageNumber-1){


            imgFirstPage.setImageDrawable(firstPageDrawable)
            imgFirstPage.isEnabled = true

            imgPreviousPage.setImageDrawable(previousPageDrawable)
            imgPreviousPage.isEnabled = true

            imgNextPage.setImageDrawable(nextPageDrawable)
            imgNextPage.isEnabled = true

            imgLastPage.setImageDrawable(lastPageDrawable)
            imgLastPage.isEnabled = true
        }

        if (currentPageNumber+1 == lastPageNumber){

            imgFirstPage.setImageDrawable(firstPageDrawable)
            imgFirstPage.isEnabled = true

            imgPreviousPage.setImageDrawable(previousPageDrawable)
            imgPreviousPage.isEnabled = true

            imgNextPage.setImageDrawable(nextPageDrawableGray)
            imgNextPage.isEnabled = false

            imgLastPage.setImageDrawable(lastPageDrawableGray)
            imgLastPage.isEnabled = false


        }
    }






    override fun onDestroyView() {
        cancelRunningTasks()
        if (((activity as MainActivity)).supportActionBar != null){
            if (!((activity as MainActivity)).supportActionBar!!.isShowing){
                ((activity as MainActivity)).supportActionBar!!.show()
            }
        }
        super.onDestroyView()
        _binding = null
    }
}


