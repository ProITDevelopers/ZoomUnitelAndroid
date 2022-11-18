package ao.co.proitconsulting.zoomunitel.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import ao.co.proitconsulting.zoomunitel.databinding.ActivitySplashScreenBinding
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings

class SplashScreenActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySplashScreenBinding

    private var mainHandler: Handler?=null
    private var mainRunnable: Runnable?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        MetodosUsados.hideSystemBars(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        mainHandler = Handler(Looper.getMainLooper())
        mainRunnable = Runnable {


            run {
                if(AppPrefsSettings.getInstance().getAuthToken()!=null){
                    launchHomeScreen()
                }else{
                    val intent = Intent(this, CadastroActivity::class.java)
                    intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }

        }
        mainHandler?.postDelayed(mainRunnable!!, 2000)
    }

    private fun launchHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mainHandler != null && mainRunnable != null) {
            mainHandler?.removeCallbacks(mainRunnable!!)
        }
    }




}