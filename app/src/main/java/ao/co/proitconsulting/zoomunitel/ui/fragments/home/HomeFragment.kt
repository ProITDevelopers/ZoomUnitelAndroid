package ao.co.proitconsulting.zoomunitel.ui.fragments.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ao.co.proitconsulting.zoomunitel.databinding.FragmentHomeBinding
import ao.co.proitconsulting.zoomunitel.helpers.network.ConnectionLiveData

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var connectionLiveData: ConnectionLiveData
    private var isNetworkAvailable: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectionLiveData = ConnectionLiveData(requireContext())
            connectionLiveData.observe(this) { isNetwork ->
                if (isNetwork) {
                    homeViewModel.setText("This is Home Fragment With Network")
                }else{
                    homeViewModel.setText("This is Home Fragment Without Network")
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}