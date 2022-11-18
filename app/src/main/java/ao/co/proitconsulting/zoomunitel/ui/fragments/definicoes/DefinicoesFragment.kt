package ao.co.proitconsulting.zoomunitel.ui.fragments.definicoes

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.adapters.DefinicoesAdapter
import ao.co.proitconsulting.zoomunitel.databinding.FragmentDefinicoesBinding
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.models.DefinicoesModel
import ao.co.proitconsulting.zoomunitel.ui.activities.MainActivity

class DefinicoesFragment : Fragment() {



    private var _binding: FragmentDefinicoesBinding? = null
    private val binding get() = _binding!!


    private val definicoesAdapter: DefinicoesAdapter by lazy {
        DefinicoesAdapter()
    }

    private var appHiddenFeatureCount=0
    private var versionHiddenFeatureCount=0


    private  var mediaPlayer: MediaPlayer?=null


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
                is DefinicoesModel.Header -> "${item.title} clicked"
            }
            goToOptionSelected(position)

        }


        return root
    }

    private fun goToOptionSelected(position: Int) {
        when(position){
            //ZOOM
            1->{
                appHiddenFeatureCount+=1

            }
            //SHARE_APP_LINK
            2->{
                MetodosUsados.shareAppLink(requireContext())
            }
            //FEEDBACK
            3->{
                MetodosUsados.sendFeedback(requireContext())
            }
            //DESENVOLVEDOR
            4->{
                showHiddenFeature()
            }
            //VERSAO
            5->{
                versionHiddenFeatureCount+=1

            }

        }
    }



    private fun showHiddenFeature() {

        if ((appHiddenFeatureCount == 16 && versionHiddenFeatureCount == 9) ||
            (appHiddenFeatureCount == 9 && versionHiddenFeatureCount == 16)){
            appHiddenFeatureCount = 0
            versionHiddenFeatureCount= 0
            Handler(Looper.getMainLooper()).postDelayed({

                playBeep()

                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(
                        R.id.action_nav_definicoes_to_userDocFragment
                    )

                }, 800)


            }, 500)
        }else{

            appHiddenFeatureCount = 0
            versionHiddenFeatureCount= 0
            return
        }



//        Handler(Looper.getMainLooper()).postDelayed({
//            appHiddenFeatureCount = 0
//            versionHiddenFeatureCount= 0
//            playBeep()
//
//
//            Handler(Looper.getMainLooper()).postDelayed({
//                findNavController().navigate(
//                    R.id.action_nav_definicoes_to_userDocFragment
//                )
//
//            }, 800)
//
//
//        }, 500)





    }

    private fun playBeep() {
        try {

            if (mediaPlayer == null){
                mediaPlayer = MediaPlayer.create(requireContext(),R.raw.ring_sound_effect)
                mediaPlayer?.setOnCompletionListener{
                    MediaPlayer.OnCompletionListener {
                        stopMediaPlayer()
                    }

                }
            }
            mediaPlayer?.setVolume(100f, 100f)
            mediaPlayer?.isLooping = false
            mediaPlayer?.start()

        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private fun stopMediaPlayer() {
        if (mediaPlayer!=null){
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }




    override fun onStop() {
        stopMediaPlayer()
        super.onStop()
    }

    override fun onDestroyView() {
        stopMediaPlayer()
        super.onDestroyView()
        _binding = null

    }


}