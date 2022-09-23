package ao.co.proitconsulting.zoomunitel.ui.activities


import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.adapters.ViewPagerFragmentsAdapter
import ao.co.proitconsulting.zoomunitel.databinding.ActivityCadastroBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.helpers.network.ConnectionLiveData
import ao.co.proitconsulting.zoomunitel.ui.fragments.cadastro.LoginFragment
import ao.co.proitconsulting.zoomunitel.ui.fragments.cadastro.RegistroFragment

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var connectionLiveData: ConnectionLiveData


    companion object{
        private var mViewPager: ViewPager2? = null

        fun getViewPager(): ViewPager2? {
            return mViewPager
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showFrags()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectionLiveData = ConnectionLiveData(this)
            connectionLiveData.observe(this) { isNetwork ->
                Constants.isNetworkAvailable = isNetwork
            }
        }else{
            Constants.isNetworkAvailable = MetodosUsados.hasInternetConnection(this)
        }
    }

    private fun showFrags() {

        mViewPager = binding.viewPager
        mViewPager?.isUserInputEnabled = false
        val adapter = ViewPagerFragmentsAdapter(this)

        // add your fragments
        adapter.addFrag(LoginFragment(), getString(R.string.login))
        adapter.addFrag(RegistroFragment(), getString(R.string.registro))

        // set adapter on viewpager
        mViewPager?.adapter = adapter
        mViewPager?.currentItem = 0




    }



    override fun onBackPressed() {
        if (mViewPager?.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            mViewPager?.currentItem = mViewPager?.currentItem!! - 1
        }
    }

}