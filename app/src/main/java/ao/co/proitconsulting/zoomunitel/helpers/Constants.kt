package ao.co.proitconsulting.zoomunitel.helpers

import ao.co.proitconsulting.zoomunitel.models.BookmarkRevistaModel

class Constants {

    companion object{

        const val BASE_URL_ZOOM_UNITEL = "http://41.78.18.144:3000/"
        const val USER_IMAGE_PATH = BASE_URL_ZOOM_UNITEL+"user/image/"
        const val IMAGE_PATH = BASE_URL_ZOOM_UNITEL+"revista/image/"
        const val PDF_PATH = BASE_URL_ZOOM_UNITEL+"revista/view/"
        const val SHARE_URL_PLAYSTORE = "https://play.google.com/store/apps/details?id="

        const val GOOGLE_DRIVE_LINK = "https://drive.google.com/viewerng/viewer?embedded=true&url="

        const val SLIDE_TIME_DELAY:Long = 8000
        const val REQUEST_TIMEOUT:Long = 10000

        const val ToastSUCESS:Int = 0
        const val ToastALERTA:Int = 1
        const val ToastERRO:Int = 3

        val bookmarkList = ArrayList<BookmarkRevistaModel>()

        var SEND_EMAIL = "email"
        var PDF_FILE_NAME = ""

        var isNetworkAvailable:Boolean = false



    }
}
