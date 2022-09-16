package ao.co.proitconsulting.zoomunitel.ui.fragments.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.databinding.FragmentPerfilBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings
import ao.co.proitconsulting.zoomunitel.models.UsuarioModel
import ao.co.proitconsulting.zoomunitel.ui.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!


    lateinit var perfilViewModel:PerfilViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        perfilViewModel =
            ViewModelProvider(this).get(PerfilViewModel::class.java)

        val frameLayout = MainActivity.getFrameLayoutImgToolbar()
        if (frameLayout != null)
            frameLayout.visibility = View.GONE

        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var bundle: Bundle? = null
        val btnEditPerfil:Button = binding.btnEditPerfil
        btnEditPerfil.setOnClickListener {
            if (bundle!=null){
                findNavController().navigate(
                    R.id.action_nav_perfil_to_editarPerfilFragment,
                    bundle
                )
            }
        }

        perfilViewModel.getPerfil.observe(viewLifecycleOwner) { usuario ->
            bundle = Bundle()
            bundle?.putSerializable("usuario",usuario)
            carregarDadosLocal(usuario)
        }
        return root
    }

    private fun carregarDadosLocal(usuario: UsuarioModel?) {

        if (usuario!=null){
            binding.txtUserName.text = usuario.userNome
            binding.txtUserEmail.text = usuario.userEmail
            binding.txtUserTelefone.text = usuario.userPhone
            if (usuario.userPhoto.isNullOrEmpty()){
                binding.txtUserNameInitial.visibility = View.VISIBLE
                if (!usuario.userNome.isNullOrEmpty()){

                    binding.txtUserNameInitial.text = usuario.userNome[0].uppercase()


                }else {
                    binding.txtUserName.text = ""

                    if (!usuario.userEmail.isNullOrEmpty()){
                        binding.txtUserNameInitial.text = usuario.userEmail[0].uppercase()
                    }else{
                        binding.txtUserEmail.text = ""
                        binding.txtUserNameInitial.visibility = View.GONE

                        Glide.with(this)
                            .load(R.drawable.user_placeholder)
                            .centerCrop()
                            .placeholder(R.drawable.user_placeholder)
                            .into(binding.imgUserPhoto)

                    }

                }
            }else{
                binding.txtUserNameInitial.visibility = View.GONE
                Glide.with(this)
                    .load(Constants.USER_IMAGE_PATH + usuario.userPhoto)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.user_placeholder)
                    .into(binding.imgUserPhoto)
            }
        }else{
            Glide.with(this)
                .load(R.drawable.user_placeholder)
                .centerCrop()
                .into(binding.imgUserPhoto)

            binding.txtUserNameInitial.visibility = View.GONE
            binding.txtUserName.text = ""
            binding.txtUserEmail.text = ""
            binding.txtUserTelefone.text = ""
        }
    }

    override fun onResume() {
        perfilViewModel.setUsuario(AppPrefsSettings.getInstance().getUser())
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}