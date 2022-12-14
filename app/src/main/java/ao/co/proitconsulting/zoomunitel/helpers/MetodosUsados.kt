package ao.co.proitconsulting.zoomunitel.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import ao.co.proitconsulting.zoomunitel.R
import com.google.android.material.snackbar.Snackbar
import java.text.Normalizer
import java.text.ParseException
import java.text.SimpleDateFormat

@Suppress("DEPRECATION")
class MetodosUsados {

    companion object {

        fun hideSystemBars(activity: Activity) {
            val window: Window = activity.window
            // Enables regular immersive mode.
            // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
            // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
            }

        }

        // Shows the system bars by removing all the flags
        // except for the ones that make the content appear under the system bars.
        fun showSystemUI(activity: Activity) {
            val window: Window = activity.window
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }

        fun transparentStatusANDNavigationBar(activity: Activity){
            //<item name="android:enforceStatusBarContrast"  tools:targetApi="q">true</item>
            //<item name="android:enforceNavigationBarContrast"  tools:targetApi="q">true</item>
            val window: Window = activity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                window.statusBarColor = Color.TRANSPARENT
                window.navigationBarColor = Color.TRANSPARENT
            }

        }

        fun transparentNavigationBar(activity: Activity){
//            <item name="android:enforceStatusBarContrast"  tools:targetApi="q">true</item>
//            <item name="android:enforceNavigationBarContrast"  tools:targetApi="q">true</item>
            val window: Window = activity.window
            WindowCompat.setDecorFitsSystemWindows(window, false)
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                window.navigationBarColor = ContextCompat.getColor(activity, R.color.white)
                window.navigationBarColor = Color.TRANSPARENT
            }


        }







        //======================DIALOG_LAYOUT===============================================//
        fun handleDialogLayout(dialogLayout: Dialog, cardView: CardView){

            dialogLayout.window.let { window ->
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    cardView.preventCornerOverlap = false
                    try {
                        val context = dialogLayout.context
                        val dividerID = context.resources.getIdentifier("android:id/titleDivider", null, null)
                        val viewDivider:View = dialogLayout.findViewById(dividerID)
                        viewDivider.setBackgroundColor(Color.TRANSPARENT)
                    }catch (e:Exception){
                        //The above code is used to remove the blue line of the Holo theme
                        e.printStackTrace()
                    }
                }
            }

        }


        //===============================================================================================================
        //===============================================================================================================

        @SuppressLint("SimpleDateFormat")
        fun getTimeStamp(timeStamp: String) : String{
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormatter = SimpleDateFormat("d MMM, yyyy")

            try {
                val date = inputFormatter.parse(timeStamp)
                return outputFormatter.format(date!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return ""
        }


        //=====================================================================//
        //=====================================================================//


        //====================VALIDAR_EMAIL=================================================//

        fun validarEmail(email:String):Boolean {
            val pattern =  Patterns.EMAIL_ADDRESS
            return pattern.matcher(email).matches()
        }


        fun removeAcentos(text: String): String {
            return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        }

        fun showCustomSnackBar(view: View?,activity: Activity?, type: Int, message: String) {

            // Create the Snackbar
            val snackbar = Snackbar.make(view!!, "", Snackbar.LENGTH_LONG)
            // Get the Snackbar's layout view
            val layout = snackbar.view as Snackbar.SnackbarLayout
            // Hide the text
            val textView = layout.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.visibility = (View.INVISIBLE)

            // Inflate our custom view
            val snackView : View = activity?.layoutInflater!!.inflate(R.layout.snackbar_layout, null)
            // Configure the view
            val txtToast: TextView = snackView.findViewById(R.id.txtToast)
            val imgToast: ImageView = snackView.findViewById(R.id.imgToast)
            txtToast.text = message
            when (type){
                0 -> {
                    imgToast.setImageResource(R.drawable.ic_baseline_check_circle_24)
                }
                1 ->{
                    imgToast.setImageResource(R.drawable.ic_baseline_circle_info_24)
                }
                3 ->{
                    imgToast.setImageResource(R.drawable.ic_baseline_circle_close_24)
                }
            }
            //If the view is not covering the whole snackbar layout, add this line
            layout.setPadding(0,0,0,0)

            // Add the view to the Snackbar's layout
            layout.addView(snackView, 0)
            // Show the Snackbar
            snackbar.show()


        }




        //=======================================================================//
        //=====================================================================//


        fun sendFeedback(context: Context) {
            var body :String?
            val email = arrayOf("developer@proit-consulting.com")

            try {
                body = context.packageManager.getPackageInfo(context.packageName, 0).versionName

                body = "\n\n-------------------------------------------------\nPor favor n??o remova essa informa????o\n SO do Dispositivo: Android \n Vers??o do SO do Dispositivo: " +
                        Build.VERSION.RELEASE + "\n Vers??o da Aplica????o: " + body + "\n Marca do Dispositivo: " + Build.BRAND +
                        "\n Modelo do Dispositivo: " + Build.MODEL + "\n Fabricante do Dispositivo: " + Build.MANUFACTURER

                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:")
                intent.putExtra(Intent.EXTRA_EMAIL, email)
                intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta do aplicativo Android")
                intent.putExtra(Intent.EXTRA_TEXT, body)

                if (context!=null){
                    if (intent.resolveActivity(context.packageManager)!=null){
                        context.startActivity(intent)
                    }
                }

            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }



        //=====================================================================//
        //=====================================================================//

        //========================PARTILHAR_LINK_DA_APP=============================================//
        fun shareAppLink(context: Context){
            val appPackageName = context.packageName
            val appName = context.getString(R.string.app_name)
            val appCategory = "que ?? uma publica????o anual promovida pela Academia UNITEL."

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            val postData = "Obtenha o aplicativo " + appName +
            " para teres acesso a ZoOM Magazine, " + appCategory + "\n" +
                    Constants.SHARE_URL_PLAYSTORE + appPackageName


            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Baixar Agora!")
            shareIntent.putExtra(Intent.EXTRA_TEXT, postData)
            shareIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(shareIntent, "Partilhar App"))
        }


        //=============================================================================
        //=============================================================================

        fun hasInternetConnection(context: Context):Boolean {
            val connectivityManager = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> true
                }
            }
            return false
        }






    }





}