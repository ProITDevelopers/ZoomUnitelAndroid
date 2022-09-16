package ao.co.proitconsulting.zoomunitel.helpers

class Constants {

    companion object{

        const val SHARE_URL_PLAYSTORE = "https://play.google.com/store/apps/details?id="
        const val BASE_URL_ZOOM_UNITEL = "http://41.78.18.144:3000/"
        const val USER_IMAGE_PATH = BASE_URL_ZOOM_UNITEL+"user/image/"
        const val IMAGE_PATH = BASE_URL_ZOOM_UNITEL+"revista/image/"
        const val PDF_PATH = BASE_URL_ZOOM_UNITEL+"revista/view/"
        var SEND_EMAIL = "email"
        const val SLIDE_TIME_DELAY:Long = 8000
        const val REQUEST_TIMEOUT:Long = 10000

        const val ToastSUCESS:Int = 0
        const val ToastALERTA:Int = 1
        const val ToastERRO:Int = 3
    }
}