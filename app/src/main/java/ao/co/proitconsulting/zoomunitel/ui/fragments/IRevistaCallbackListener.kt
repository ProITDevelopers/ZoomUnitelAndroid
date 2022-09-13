package ao.co.proitconsulting.zoomunitel.ui.fragments


import ao.co.proitconsulting.zoomunitel.models.RevistaModel

interface IRevistaCallbackListener {
    fun onLoading(loadMessage:String)
    fun onSuccess(revistaList : List<RevistaModel>?)
    fun onError(errorMessage:String)
}