package ao.co.proitconsulting.zoomunitel.ui.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.databinding.ActivityMainBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.helpers.network.ConnectionLiveData
import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings
import ao.co.proitconsulting.zoomunitel.models.UsuarioModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val TAG = "TAG_MainActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var  imgUserPhoto:CircleImageView
    private lateinit var  txtUserNameInitial:TextView
    private lateinit var  txtUserName:TextView
    private lateinit var  txtUserEmail:TextView
    private lateinit var  spinKitBottom: SpinKitView


    private lateinit var dialogLayoutAlertDialog: Dialog



    companion object{

        @SuppressLint("StaticFieldLeak")
        private var frameLayoutImgToolbar: FrameLayout?=null
        fun getFrameLayoutImgToolbar() : FrameLayout? {
            return frameLayoutImgToolbar
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = Color.parseColor("#CC000000")

        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.appBarMain.toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val connectionLiveData = ConnectionLiveData(this)
            connectionLiveData.observe(this) { isNetwork ->
                Constants.isNetworkAvailable = isNetwork
            }
        }else{
            Constants.isNetworkAvailable = MetodosUsados.hasInternetConnection(this)
        }


        frameLayoutImgToolbar = binding.appBarMain.frameLayoutImgToolbar
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navView.itemIconTintList = null
        val view: View = navView.getHeaderView(0)
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_perfil, R.id.nav_definicoes
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        imgUserPhoto = view.findViewById(R.id.imgUserPhoto)
        txtUserNameInitial = view.findViewById(R.id.txtUserNameInitial)
        txtUserName = view.findViewById(R.id.txtUserName)
        txtUserEmail = view.findViewById(R.id.txtUserEmail)
        spinKitBottom = findViewById(R.id.spin_kit_bottom)

        carregarDadosLocal(AppPrefsSettings.getInstance().getUser())

        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_ALERTA_GPS
        dialogLayoutAlertDialog = Dialog(this)
        dialogLayoutAlertDialog.setContentView(R.layout.layout_terminar_sessao)
        dialogLayoutAlertDialog.setCancelable(true)
        val dialog_card_view: CardView = dialogLayoutAlertDialog.findViewById(R.id.dialog_card_view)
        val dialog_btn_cancel: Button = dialogLayoutAlertDialog.findViewById(R.id.dialog_btn_cancel)
        val dialog_btn_ok: Button = dialogLayoutAlertDialog.findViewById(R.id.dialog_btn_ok)
        MetodosUsados.handleDialogLayout(dialogLayoutAlertDialog,dialog_card_view)

        dialog_btn_cancel.setOnClickListener {
            dialogLayoutAlertDialog.cancel()
        }

        dialog_btn_ok.setOnClickListener {
            dialogLayoutAlertDialog.cancel()
            spinKitBottom.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                terminarSessao()
            }, 2000)

        }

    }

    private fun carregarDadosLocal(usuario: UsuarioModel?) {
        Log.d(TAG, "onCreate: ${usuario.toString()}")
        if (usuario!=null){
            txtUserName.text = usuario.userNome
            txtUserEmail.text = usuario.userEmail
            if (usuario.userPhoto.isNullOrEmpty() || usuario.userPhoto == "null"){
                txtUserNameInitial.visibility = View.VISIBLE
                if (!usuario.userNome.isNullOrEmpty()){

                    txtUserNameInitial.text = usuario.userNome[0].uppercase()


                }else {
                    txtUserName.text = ""

                    if (!usuario.userEmail.isNullOrEmpty()){
                        txtUserNameInitial.text = usuario.userEmail[0].uppercase()
                    }else{
                        txtUserEmail.text = ""
                        txtUserNameInitial.visibility = View.GONE

                        Glide.with(this)
                            .load(R.drawable.user_placeholder)
                            .centerCrop()
                            .placeholder(R.drawable.user_placeholder)
                            .into(imgUserPhoto)

                    }

                }
            }else{
                txtUserNameInitial.visibility = View.GONE
                Glide.with(this)
                    .load(Constants.USER_IMAGE_PATH + usuario.userPhoto)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.user_placeholder)
                    .into(imgUserPhoto)
            }
        }else{
            Glide.with(this)
                .load(R.drawable.user_placeholder)
                .centerCrop()
                .into(imgUserPhoto)

            txtUserNameInitial.visibility = View.GONE
            txtUserName.text = ""
            txtUserEmail.text = ""
        }
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (!Constants.isNetworkAvailable){
                Constants.isNetworkAvailable = MetodosUsados.hasInternetConnection(this)
            }
        }

        if (Constants.isNetworkAvailable)
            carregarMeuPerfil()


    }

    private fun carregarMeuPerfil() {
        val userUID = AppPrefsSettings.getInstance().getUser()!!.userId
        val userId = userUID!!.toInt()
        val retrofit = RetrofitInstance.api.userProfile(userId)
        retrofit.enqueue(object :
            Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body().let { responseResult ->

                        try {
                            val body = responseResult?.string()
                            if (!body.isNullOrEmpty()){
                                val jsonArray = JSONArray(body)
                                val jsonResponse = jsonArray.getJSONObject(0)
                                val usuario = UsuarioModel(
                                    jsonResponse.getInt("USERID"),
                                    jsonResponse.getString("NOME"),
                                    jsonResponse.getString("EMAIL"),
                                    jsonResponse.getString("TELEFONE"),
                                    jsonResponse.getString("FOTOKEY")
                                )
                                AppPrefsSettings.getInstance().saveUser(usuario)
                                carregarDadosLocal(usuario)
                            }
                            Log.d(TAG, "onResponse_success: $body")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } catch (e:JSONException){
                            e.printStackTrace()
                        }

                    }
                } else{

                    try {

                        Log.d(TAG, "onResponse_NOTsuccess: ${response.errorBody()?.string()}")
                    }catch (e: IOException){
                        Log.e(TAG, "onResponseIOException: ${e.message}")
                    }catch (e: JSONException){
                        Log.e(TAG, "onResponseJSONException: ${e.message}")
                    }
                }
            }



            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

        })
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.logOut) {
            if (!dialogLayoutAlertDialog.isShowing)
                dialogLayoutAlertDialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun terminarSessao() {
        AppPrefsSettings.getInstance().clearAppPrefs()
        spinKitBottom.visibility = View.GONE
        val intent = Intent(this, SplashScreenActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}