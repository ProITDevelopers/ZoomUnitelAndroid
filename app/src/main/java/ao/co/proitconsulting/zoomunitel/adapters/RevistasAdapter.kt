package ao.co.proitconsulting.zoomunitel.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.models.RevistaModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.makeramen.roundedimageview.RoundedImageView


class RevistasAdapter : RecyclerView.Adapter<RevistasAdapter.RevistaViewHolder>() {

    inner class RevistaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val imageSlide:RoundedImageView = itemView.findViewById(R.id.imageSlide)
        val shimmerFrameLayout:ShimmerFrameLayout = itemView.findViewById(R.id.shimmerFrameLayout)

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
                R.layout.revista_slide_item,
                parent,false
            ),
        )
    }

    override fun onBindViewHolder(holder: RevistaViewHolder, position: Int) {
        val revista = differ.currentList[position]
        holder.itemView.apply {

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
                        holder.shimmerFrameLayout.stopShimmer()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.shimmerFrameLayout.visibility = View.GONE
                        return false
                    }

                })
                .into(holder.imageSlide)



        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}