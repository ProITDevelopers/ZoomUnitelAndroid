package ao.co.proitconsulting.zoomunitel.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.adapters.ViewPagerFragmentsAdapter
import ao.co.proitconsulting.zoomunitel.databinding.ActivitySenhaBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
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
        super.onCreate(savedInstanceState)
        binding = ActivitySenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        goToFragment(RecuperarFragment())

        showFrags()

    }

    private fun showFrags() {
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



        mViewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {



            override fun onPageSelected(position: Int) {
                if(position == 1)
                    adapter.updateFrag(InserirCodigoFragment.newInstance(Constants.SEND_EMAIL),position)
            }

        })

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    /*
    *private fun goToFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_senha, fragment, null)
        transaction.commit()



    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }**/
}


