package ao.co.proitconsulting.zoomunitel.models

import com.google.gson.annotations.SerializedName

data class BookmarkRevistaModel(

    @SerializedName("IDREVISTA")
    val revistaId:Int,

    @SerializedName("CurrentPage")
    val currentPage:Int,

    @SerializedName("LastPage")
    val lastPageNumber:Int
)
