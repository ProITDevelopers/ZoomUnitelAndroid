package ao.co.proitconsulting.zoomunitel.adapters

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.prelollipop.GetUnsafeOkHttpClientSecurity
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.models.RevistaModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.flaviofaria.kenburnsview.KenBurnsView
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import com.github.ybq.android.spinkit.SpinKitView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.makeramen.roundedimageview.RoundedImageView
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference


class RevistasLerDownloadAdapter : RecyclerView.Adapter<RevistasLerDownloadAdapter.RevistaViewHolder>() {

    var itemClickListener : ((view: View,revista: RevistaModel)->Unit)?=null


    val aDI = AccelerateDecelerateInterpolator()
    val generator = RandomTransitionGenerator(10000,aDI)



    inner class RevistaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val spinKitView: SpinKitView = itemView.findViewById(R.id.spin_kit_bottom)
        val rvImgBackgnd:KenBurnsView = itemView.findViewById(R.id.rvImgBackgnd)
        val rvImg:RoundedImageView = itemView.findViewById(R.id.rvImg)
        val progressbar:ProgressBar = itemView.findViewById(R.id.progressbar)
        val txtProgress:TextView = itemView.findViewById(R.id.txtProgress)
        val btnLer:Button = itemView.findViewById(R.id.btnLer)
        val btnDownload:Button = itemView.findViewById(R.id.btnDownload)



    }

    private val differCallback = object : DiffUtil.ItemCallback<RevistaModel>(){

        override fun areItemsTheSame(oldItem: RevistaModel, newItem: RevistaModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: RevistaModel, newItem: RevistaModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RevistaViewHolder {
        return RevistaViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.revista_item_detail_ler_down,
                parent,false
            ),
        )
    }

    override fun onBindViewHolder(holder: RevistaViewHolder, position: Int) {
        val revista = differ.currentList[position]
        holder.itemView.apply {

            Glide.with(this)
                .load(Constants.IMAGE_PATH + revista.imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.rvImgBackgnd)

            Glide.with(this).asBitmap()
                .load(Constants.IMAGE_PATH + revista.imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.magazine_placeholder)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.spinKitView.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.spinKitView.visibility = View.GONE
                        return false
                    }

                })
                .into(holder.rvImg)



        }

        holder.rvImgBackgnd.setTransitionGenerator(generator)

        holder.rvImg.setOnClickListener {
            itemClickListener?.invoke(it,revista)
        }

        holder.btnLer.setOnClickListener {
            itemClickListener?.invoke(it,revista)
        }

        holder.btnDownload.setOnClickListener {
            it.isEnabled = false
            verificarPermissaoArmazenamento(it.context,revista,holder)
//            Toast.makeText(it.context, ""+holder.btnDownload.text +" "+revista.title, Toast.LENGTH_SHORT).show()
        }


    }

    private fun verificarPermissaoArmazenamento(
        context: Context,
        revista: RevistaModel,
        holder: RevistaViewHolder
    ) {
        Dexter.withContext(context)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                            verificarConnecxaoDOWNLOAD(context,revista,holder)
                        }else {
                            Toast.makeText(context, ""+context.getString(R.string.msg_permissao_armazenamento_continuar), Toast.LENGTH_SHORT).show()
                            holder.btnDownload.isEnabled = true
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

    private fun verificarConnecxaoDOWNLOAD(context: Context,revista: RevistaModel, holder: RevistaViewHolder) {
        val folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
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
            holder.progressbar.visibility = View.GONE
            Toast.makeText(context, "Guardado em\n $storageDir", Toast.LENGTH_SHORT).show()
            holder.btnDownload.isEnabled = true
        } else {

            if (Constants.isNetworkAvailable){
                downLoadPDF(revista,holder)

            }else{
                Toast.makeText(context, context.getString(R.string.msg_erro_internet), Toast.LENGTH_SHORT).show()
                holder.btnDownload.isEnabled = true

            }
        }
    }

    private fun downLoadPDF(revista: RevistaModel,holder: RevistaViewHolder) {
        val pdfUrl = Uri.parse(Constants.PDF_PATH + revista.pdfLink)
        val downloadTask = DownloadRetrofitTask(holder)
        downloadTask.execute(pdfUrl.toString())
//        addRunningTask(downloadTask)
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @Suppress("DEPRECATION")
    private class DownloadRetrofitTask(holder: RevistaViewHolder) : AsyncTask<String?,Int?,String?>() {

        private  var activityWeakReference : WeakReference<RevistaViewHolder>?=null

        init{

            this.activityWeakReference = WeakReference<RevistaViewHolder>(holder)
        }



        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            val holder: RevistaViewHolder? = activityWeakReference?.get()
            if (holder == null){
                return
            }
            holder.progressbar.visibility = View.VISIBLE
            holder.txtProgress.visibility = View.VISIBLE
            holder.txtProgress.text = "0%"
            super.onPreExecute()

        }


        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg sUrl: String?): String? {


            var pdf_File: File? = null
            lateinit var inputStream: InputStream
            lateinit var outputStream: FileOutputStream

            val httpClient = GetUnsafeOkHttpClientSecurity.getUnsafeOkHttpClient()
            val okHttpClient = httpClient.build()
            val call = okHttpClient.newCall(Request.Builder().url(sUrl[0]!!).get().build())

            try {
                val response = call.execute()
                if (response.isSuccessful && response.body != null){
                    val fileLength = response.body!!.contentLength()

                    //download the file
                    inputStream = response.body!!.byteStream()
                    val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                            + File.separator + "ZoOM_Unitel")

                    val storageDir = folder.absolutePath

                    if (!folder.exists())
                        folder.mkdirs()

                    pdf_File = File(storageDir+ Constants.PDF_FILE_NAME)

                    outputStream = FileOutputStream(pdf_File)

                    val data = ByteArray(1024 * 4)
                    var total: Long = 0
                    var count :Int=0
                    var progressResult:Long =0



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

                        Constants.resultMessage = "Guardado em\n $storageDir"
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
            return null
        }


        @Deprecated("Deprecated in Java")
        override fun onProgressUpdate(vararg progress: Int?) {
            val holder: RevistaViewHolder? = activityWeakReference?.get()
            if (holder == null){
                return
            }
            holder.progressbar.isIndeterminate = false
            holder.progressbar.max = 100
            holder.progressbar.progress = progress[0]!!

            holder.txtProgress.visibility = View.VISIBLE
            holder.txtProgress.text = StringBuilder().append(progress[0]!!).append("%").toString()

            super.onProgressUpdate(*progress)

        }


        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            val holder: RevistaViewHolder? = activityWeakReference?.get()
            if (holder == null){
                return
            }
            if (result != null)
                Toast.makeText(holder.itemView.context, "Download erro: $result", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(holder.itemView.context, Constants.resultMessage, Toast.LENGTH_SHORT).show()

            holder.progressbar.visibility = View.GONE
            holder.txtProgress.visibility = View.GONE
            holder.btnDownload.isEnabled = true
            super.onPostExecute(result)

        }



    }


}