package ao.co.proitconsulting.zoomunitel.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import ao.co.proitconsulting.zoomunitel.R
import com.google.android.material.snackbar.Snackbar
import java.text.Normalizer
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.random.Random

@Suppress("DEPRECATION")

class MetodosUsados {

    companion object {



        fun transparentNavigationBar(activity: Activity){
            val window: Window = activity.window
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.navigationBarColor = ContextCompat.getColor(activity, R.color.white)
            }
        }

        //=====================================================================//
        //==============MOSTRAR_MENSAGENS=======================================================//
        fun mostrarMensagem(mContexto: Context, mensagem:Int) {
            Toast.makeText(mContexto,mensagem,Toast.LENGTH_SHORT).show()
        }

        fun mostrarMensagem(mContexto: Context, mensagem:String) {
            Toast.makeText(mContexto,mensagem,Toast.LENGTH_SHORT).show()
        }

        //======================GERAR_NUMEROS_ALEATORIOS_INTEIROS===============================================//
        fun getRandomNumbers(min: Int, max: Int): Int {
            if (min>=max){
                throw IllegalArgumentException("max must be greater than min")
            }

            val randomNumber = Random
            return randomNumber.nextInt((max - min) + 1) + min
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

                body = "\n\n-------------------------------------------------\nPor favor não remova essa informação\n SO do Dispositivo: Android \n Versão do SO do Dispositivo: " +
                        Build.VERSION.RELEASE + "\n Versão da Aplicação: " + body + "\n Marca do Dispositivo: " + Build.BRAND +
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
            }
        }



        //=====================================================================//
        //=====================================================================//

        //========================PARTILHAR_LINK_DA_APP=============================================//
        fun shareAppLink(context: Context){
            val appPackageName = context.packageName
            val appName = context.getString(R.string.app_name)
            val appCategory = "que é uma publicação anual promovida pela Academia UNITEL."

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