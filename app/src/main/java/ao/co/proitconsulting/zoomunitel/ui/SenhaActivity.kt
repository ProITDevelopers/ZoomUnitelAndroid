package ao.co.proitconsulting.zoomunitel.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.databinding.ActivitySenhaBinding
import ao.co.proitconsulting.zoomunitel.ui.fragments.senha.RecuperarFragment

class SenhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySenhaBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        goToFragment(RecuperarFragment())

    }

    private fun goToFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_senha, fragment, null)
        transaction.commit()



    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}


