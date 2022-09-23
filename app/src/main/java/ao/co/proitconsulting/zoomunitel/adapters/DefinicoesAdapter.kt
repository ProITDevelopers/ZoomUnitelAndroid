package ao.co.proitconsulting.zoomunitel.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.models.DefinicoesModel


class DefinicoesAdapter: RecyclerView.Adapter<DefinicoesAdapter.DefinicoesAdapterViewHolder>(){

    private val adapterData = mutableListOf<DefinicoesModel>()

    var itemClickListener : ((view:View, item:DefinicoesModel,position:Int)->Unit)?=null


    //--------onCreateViewHolder: inflate layout with view holder-------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefinicoesAdapterViewHolder {

        val layout = when (viewType) {

            TYPE_ABOUT -> R.layout.item_definicoes
            TYPE_SETTINGS -> R.layout.item_definicoes
            TYPE_HEADER -> R.layout.item_definicoes_header
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        return DefinicoesAdapterViewHolder(view)
    }

    //-----------onBindViewHolder: bind view with data model---------
    override fun onBindViewHolder(holder: DefinicoesAdapterViewHolder, position: Int) {
        holder.bind(adapterData[position])
        holder.itemClickListener = itemClickListener


    }




    override fun getItemCount(): Int = adapterData.size

    override fun getItemViewType(position: Int): Int {
        return when (adapterData[position]) {
            is DefinicoesModel.About -> TYPE_ABOUT
            is DefinicoesModel.Settings -> TYPE_SETTINGS
            else -> TYPE_HEADER
        }
    }

    fun setData(data: List<DefinicoesModel>) {
        adapterData.apply {
            clear()
            addAll(data)
        }
    }



    companion object {
        private const val TYPE_ABOUT = 0
        private const val TYPE_SETTINGS= 1
        private const val TYPE_HEADER = 2
    }

    class DefinicoesAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var itemClickListener : ((view:View, item:DefinicoesModel,position:Int)->Unit)?=null

        private fun bindAbout(item: DefinicoesModel.About) {
            //Do your view assignment here from the data model


            itemView.findViewById<TextView>(R.id.txtTitle)?.text = item.aboutTitle
            itemView.findViewById<TextView>(R.id.txtDesc)?.text = item.aboutDesc

            itemView.setOnClickListener {
                itemClickListener?.invoke(it,item,adapterPosition)
            }
        }

        private fun bindSettings(item: DefinicoesModel.Settings) {
            //Do your view assignment here from the data model

            itemView.findViewById<TextView>(R.id.txtTitle)?.text = item.settingsTitle
            itemView.findViewById<TextView>(R.id.txtDesc)?.visibility = View.GONE

            itemView.setOnClickListener {
                itemClickListener?.invoke(it,item,adapterPosition)
            }
        }



        private fun bindHeader(item: DefinicoesModel.Header) {
            //Do your view assignment here from the data model
            itemView.findViewById<ConstraintLayout>(R.id.clRoot)?.setBackgroundColor(Color.parseColor("#e6540f"))
            itemView.findViewById<TextView>(R.id.tvNameLabel)?.text = item.title

            itemView.setOnClickListener {
                itemClickListener?.invoke(it,item,adapterPosition)
            }

        }




        fun bind(definicoesModel: DefinicoesModel) {

            when (definicoesModel) {
                is DefinicoesModel.About -> bindAbout(definicoesModel)
                is DefinicoesModel.Settings -> bindSettings(definicoesModel)
                is DefinicoesModel.Header -> bindHeader(definicoesModel)
            }
        }





    }




}