package ao.co.proitconsulting.zoomunitel.ui.fragments.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ao.co.proitconsulting.zoomunitel.databinding.FragmentEditarPerfilBinding

class EditarPerfilFragment : Fragment() {

    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editarPerfilViewModel =
            ViewModelProvider(this).get(EditarPerfilViewModel::class.java)

        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}