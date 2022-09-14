package ao.co.proitconsulting.zoomunitel.ui.fragments.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.databinding.FragmentEditarPerfilBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.models.UsuarioModel
import ao.co.proitconsulting.zoomunitel.ui.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

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

        val frameLayout = MainActivity.getFrameLayoutImgToolbar()
        if (frameLayout != null)
            frameLayout.visibility = View.GONE

        _binding = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        editarPerfilViewModel.getPerfil.observe(viewLifecycleOwner) { usuario ->
            carregarDadosLocal(usuario)
        }

        return root
    }


    private fun carregarDadosLocal(usuario: UsuarioModel?) {

        if (usuario!=null){
            binding.editNome.setText(usuario.userNome)
            binding.editEmail.setText(usuario.userEmail)
            binding.editTelefone.setText(usuario.userPhone)
            if (usuario.userPhoto.isNullOrEmpty()){
                binding.txtUserNameInitial.visibility = View.VISIBLE
                if (!usuario.userNome.isNullOrEmpty()){

                    binding.txtUserNameInitial.text = usuario.userNome[0].uppercase()


                }else {
                    binding.editNome.text?.clear()

                    if (!usuario.userEmail.isNullOrEmpty()){
                        binding.txtUserNameInitial.text = usuario.userEmail[0].uppercase()
                    }else{
                        binding.editEmail.text?.clear()
                        binding.txtUserNameInitial.visibility = View.GONE

                        Glide.with(this)
                            .load(R.drawable.user_placeholder)
                            .centerCrop()
                            .placeholder(R.drawable.user_placeholder)
                            .into(binding.userPhoto)

                    }

                }
            }else{
                binding.txtUserNameInitial.visibility = View.GONE
                Glide.with(this)
                    .load(Constants.USER_IMAGE_PATH + usuario.userPhoto)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.user_placeholder)
                    .into(binding.userPhoto)
            }
        }else{
            Glide.with(this)
                .load(R.drawable.user_placeholder)
                .centerCrop()
                .into(binding.userPhoto)

            binding.txtUserNameInitial.visibility = View.GONE
            binding.editNome.text?.clear()
            binding.editEmail.text?.clear()
            binding.editTelefone.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}