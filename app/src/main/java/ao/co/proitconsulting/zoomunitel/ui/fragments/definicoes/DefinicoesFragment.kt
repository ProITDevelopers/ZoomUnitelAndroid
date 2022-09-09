package ao.co.proitconsulting.zoomunitel.ui.fragments.definicoes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ao.co.proitconsulting.zoomunitel.adapters.DefinicoesAdapter
import ao.co.proitconsulting.zoomunitel.databinding.FragmentDefinicoesBinding

val TAG = "TAG_DefinFrag"
class DefinicoesFragment : Fragment() {

    private var _binding: FragmentDefinicoesBinding? = null
    private val binding get() = _binding!!


    private val definicoesAdapter: DefinicoesAdapter by lazy {
        DefinicoesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val definicoesViewModel =
            ViewModelProvider(this).get(DefinicoesViewModel::class.java)

        _binding = FragmentDefinicoesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = definicoesAdapter



        definicoesViewModel.getList.observe(viewLifecycleOwner) { definicoesList->

            definicoesAdapter.setData(definicoesList)

        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}