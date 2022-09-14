package ao.co.proitconsulting.zoomunitel.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.models.RevistaModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.flaviofaria.kenburnsview.KenBurnsView
import com.makeramen.roundedimageview.RoundedImageView


class RevistasLerDownloadAdapter : RecyclerView.Adapter<RevistasLerDownloadAdapter.RevistaViewHolder>() {

    var itemClickListener : ((revista: RevistaModel)->Unit)?=null

    inner class RevistaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){


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

            Glide.with(this)
                .load(Constants.IMAGE_PATH + revista.imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.rvImg)





        }

        holder.btnLer.setOnClickListener {
            itemClickListener?.invoke(revista)
        }

        holder.btnDownload.setOnClickListener {
            Toast.makeText(it.context, ""+holder.btnDownload.text +" "+revista.title, Toast.LENGTH_SHORT).show()
        }
    }



    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}