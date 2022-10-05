package ao.co.proitconsulting.zoomunitel.ui.fragments.definicoes

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.adapters.DefinicoesAdapter
import ao.co.proitconsulting.zoomunitel.databinding.FragmentDefinicoesBinding
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings
import ao.co.proitconsulting.zoomunitel.models.DefinicoesModel
import ao.co.proitconsulting.zoomunitel.ui.activities.MainActivity
import ao.co.proitconsulting.zoomunitel.ui.activities.SenhaActivity
import ao.co.proitconsulting.zoomunitel.ui.activities.SplashScreenActivity

class DefinicoesFragment : Fragment() {



    private var _binding: FragmentDefinicoesBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialogLayoutAlertDialog:Dialog


    private val definicoesAdapter: DefinicoesAdapter by lazy {
        DefinicoesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val definicoesViewModel =
            ViewModelProvider(this)[DefinicoesViewModel::class.java]

        val frameLayout = MainActivity.getFrameLayoutImgToolbar()
        if (frameLayout != null)
            frameLayout.visibility = View.GONE

        _binding = FragmentDefinicoesBinding.inflate(inflater, container, false)
        val root: View = binding.root




        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = definicoesAdapter



        definicoesViewModel.getList.observe(viewLifecycleOwner) { definicoesList->

            definicoesAdapter.setData(definicoesList)

        }

        definicoesAdapter.itemClickListener = { view,item,position ->

            val message = when(item){
                is DefinicoesModel.About -> "${item.aboutTitle} clicked"
                is DefinicoesModel.Settings -> "${item.settingsTitle} clicked"
                is DefinicoesModel.Header -> "${item.title} clicked"
            }
            goToOptionSelected(position)

        }

        //-------------------------------------------------------------//
        //-------------------------------------------------------------//
        //DIALOG_LAYOUT_ALERTA_GPS
        dialogLayoutAlertDialog = context?.let { Dialog(it) }!!
        dialogLayoutAlertDialog.setContentView(R.layout.layout_terminar_sessao)
        dialogLayoutAlertDialog.setCancelable(true)
        val dialog_card_view: CardView = dialogLayoutAlertDialog.findViewById(R.id.dialog_card_view)
        val dialog_btn_cancel:Button = dialogLayoutAlertDialog.findViewById(R.id.dialog_btn_cancel)
        val dialog_btn_ok:Button = dialogLayoutAlertDialog.findViewById(R.id.dialog_btn_ok)
        MetodosUsados.handleDialogLayout(dialogLayoutAlertDialog,dialog_card_view)

        dialog_btn_cancel.setOnClickListener {
            dialogLayoutAlertDialog.cancel()
        }

        dialog_btn_ok.setOnClickListener {
            dialogLayoutAlertDialog.cancel()
            binding.spinKitBottom.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                terminarSessao()
            }, 2000)

        }



        return root
    }

    private fun goToOptionSelected(position: Int) {
        when(position){
            //ZOOM
            1->{

            }
            //VERSAO
            2->{

            }
            //DESENVOLVEDOR
            3->{

            }
            //FEEDBACK
            4->{
                MetodosUsados.sendFeedback(requireContext())
            }
            //SHARE_APP_LINK
            5->{
                MetodosUsados.shareAppLink(requireContext())
            }
            //RECUPERAR_SENHA
            7->{
                startActivity(Intent(activity, SenhaActivity::class.java))
            }
            //LOGOUT
            8->{
                if (!dialogLayoutAlertDialog.isShowing)
                    dialogLayoutAlertDialog.show()

            }
        }
    }



    private fun terminarSessao() {
        AppPrefsSettings.getInstance().clearAppPrefs()
        binding.spinKitBottom.visibility = View.GONE
        val intent = Intent(activity, SplashScreenActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}