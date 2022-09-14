package ao.co.proitconsulting.zoomunitel.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.models.RevistaModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.flaviofaria.kenburnsview.KenBurnsView
import com.makeramen.roundedimageview.RoundedImageView


class RevistasDetailsAdapter : RecyclerView.Adapter<RevistasDetailsAdapter.RevistaViewHolder>() {

    var itemClickListener : ((revistaList: List<RevistaModel>, position:Int)->Unit)?=null

    inner class RevistaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val rvImgBackgnd:KenBurnsView = itemView.findViewById(R.id.rvImgBackgnd)
        val rvImg:RoundedImageView = itemView.findViewById(R.id.rvImg)
        val txtRvTitle:TextView = itemView.findViewById(R.id.txtRvTitle)
        val txtData:TextView = itemView.findViewById(R.id.txtData)
        val txtDescricao:TextView = itemView.findViewById(R.id.txtDescricao)
        val btnVermais:Button = itemView.findViewById(R.id.btnVermais)



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
                R.layout.revista_item_detail,
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

            holder.txtRvTitle.text = revista.title
            holder.txtDescricao.text = revista.descricao
            holder.txtData.text = StringBuilder().append(MetodosUsados.getTimeStamp(revista.created_at)).toString()




        }

        holder.btnVermais.setOnClickListener {
            itemClickListener?.invoke(differ.currentList,position)
        }
    }



    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}