package ao.co.proitconsulting.zoomunitel.ui.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.adapters.ViewPagerFragmentsAdapter
import ao.co.proitconsulting.zoomunitel.databinding.ActivitySenhaBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.helpers.network.ConnectionLiveData
import ao.co.proitconsulting.zoomunitel.ui.fragments.senha.InserirCodigoFragment
import ao.co.proitconsulting.zoomunitel.ui.fragments.senha.NovaPassFragment
import ao.co.proitconsulting.zoomunitel.ui.fragments.senha.RecuperarFragment

class SenhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySenhaBinding


    companion object{
        private var mViewPager: ViewPager2? = null

        fun getViewPager(): ViewPager2? {
            return mViewPager
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = Color.TRANSPARENT
        }
        super.onCreate(savedInstanceState)
        binding = ActivitySenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showFrags()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val connectionLiveData = ConnectionLiveData(this)
            connectionLiveData.observe(this) { isNetwork ->
                Constants.isNetworkAvailable = isNetwork
            }
        }else{
            Constants.isNetworkAvailable = MetodosUsados.hasInternetConnection(this)
        }

    }

    private fun showFrags() {
        val imgBack : ImageView = binding.imgBack
        imgBack.setOnClickListener {
            onBackPressed()
        }
        mViewPager = binding.viewPager
        mViewPager?.isUserInputEnabled = false
        val adapter = ViewPagerFragmentsAdapter(this)

        // add your fragments
        adapter.addFrag(RecuperarFragment(), getString(R.string.recuperar_senha))
        adapter.addFrag(InserirCodigoFragment(), getString(R.string.inserir_codigo))
        adapter.addFrag(NovaPassFragment(), getString(R.string.nova_senha))


        // set adapter on viewpager
        mViewPager?.adapter = adapter
        mViewPager?.currentItem = 0


    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (!Constants.isNetworkAvailable){
                Constants.isNetworkAvailable = MetodosUsados.hasInternetConnection(this)
            }
        }
        super.onResume()
    }


}


