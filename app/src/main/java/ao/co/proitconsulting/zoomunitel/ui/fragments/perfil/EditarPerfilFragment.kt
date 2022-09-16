package ao.co.proitconsulting.zoomunitel.ui.fragments.perfil

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.databinding.FragmentEditarPerfilBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.helpers.network.ConnectionLiveData
import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings
import ao.co.proitconsulting.zoomunitel.models.UsuarioModel
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import ao.co.proitconsulting.zoomunitel.ui.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarPerfilFragment : Fragment() {

    private val TAG="TAG_EditarPerfil"
    private var _binding: FragmentEditarPerfilBinding? = null
    private val binding get() = _binding!!

    lateinit var  nome:String
    lateinit var  telefone:String
    lateinit var  email:String


    lateinit var connectionLiveData: ConnectionLiveData
    private var isNetworkAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectionLiveData = ConnectionLiveData(requireContext())
            connectionLiveData.observe(this) { isNetwork ->
                isNetworkAvailable = isNetwork
            }
        }else{
            isNetworkAvailable = MetodosUsados.hasInternetConnection(requireContext())
        }
        super.onCreate(savedInstanceState)
    }

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

        binding.editNome.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoNome(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.editTelefone.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoPhone(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.editEmail.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoEmail(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        val btnSalvarPerfil: Button = binding.btnSalvarPerfil
        btnSalvarPerfil.setOnClickListener {
            if (verificarCampos()){

                isNetworkAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isNetworkAvailable
                }else{
                    MetodosUsados.hasInternetConnection(requireContext())
                }

                if (isNetworkAvailable){
                    actualizarPerfil()

                }else{
                    MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_internet))
                }

            }
        }

        return root
    }

    private fun actualizarPerfil() {
        deActivateViews()
        binding.spinKitBottom.visibility = View.VISIBLE
        val usuario = AppPrefsSettings.getInstance().getUser()
        val userUpdateRequest = UsuarioRequest.UsuarioUpdateRequest(nome,telefone,email)
        val retrofit = RetrofitInstance.api.userProfileUpdate(userUpdateRequest)
        retrofit.enqueue(object :
            Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    binding.spinKitBottom.visibility = View.GONE
                    val userUpdated = UsuarioModel(
                        usuario!!.userId, nome,email,telefone,usuario.userPhoto
                    )
                    AppPrefsSettings.getInstance().saveUser(userUpdated)
                    MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastSUCESS,getString(R.string.perfil_atualizado_sucesso))
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigateUp()
                    }, 5000)

                } else{
                    binding.spinKitBottom.visibility = View.GONE
                    MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_servidor))
                }
                activateViews()
            }



            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.spinKitBottom.visibility = View.GONE
                activateViews()
                isNetworkAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isNetworkAvailable
                }else{
                    MetodosUsados.isConnected(Constants.REQUEST_TIMEOUT,TAG)
                }

                if (!isNetworkAvailable){
                    MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_internet))
                }else if (!t.message.isNullOrEmpty() && t.message!!.contains("timeout")){
                    MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_internet_timeout))
                }else{
                    MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_servidor))
                }
            }

        })
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

    private fun verificarCampoNome(nome: String) {
        if (nome.isEmpty()){
            binding.editNome.requestFocus()
            binding.editNome.error = getString(R.string.msg_erro_campo_vazio)
            return
        }

        if (nome.matches(".*\\d.*".toRegex())){
            binding.editNome.requestFocus()
            binding.editNome.error = getString(R.string.msg_erro_campo_com_letras)
            return
        }

        if (nome.length<2){
            binding.editNome.requestFocus()
            binding.editNome.error = getString(R.string.msg_erro_min_duas_letras)
            return
        }

        this.nome = nome
    }

    private fun verificarCampoPhone(telefone: String) {

        if (telefone.isEmpty()){
            binding.editTelefone.requestFocus()
            binding.editTelefone.error = getString(R.string.msg_erro_campo_vazio)
            return
        }


        if (!telefone.matches("^[0-9]*$".toRegex())){
            binding.editTelefone.requestFocus()
            binding.editTelefone.error = getString(R.string.msg_erro_num_telefone_invalido)
            return
        }else{
            if (telefone.length<9){
                binding.editTelefone.requestFocus()
                binding.editTelefone.error = getString(R.string.msg_erro_num_telefone_invalido)
                return
            }else{
                this.telefone = telefone
            }
        }
    }

    private fun verificarCampoEmail(email: String) {
        if (email.isEmpty()){
            binding.editEmail.requestFocus()
            binding.editEmail.error = getString(R.string.msg_erro_campo_vazio)
            return
        }

        if (!MetodosUsados.validarEmail(email)) {

            binding.editEmail.requestFocus()
            binding.editEmail.error = getString(R.string.msg_erro_email_invalido)

            return

        }else {
            this.email = email.lowercase()

        }
    }

    private fun verificarCampos():Boolean{

        nome = binding.editNome.text.toString().trim()
        telefone = binding.editTelefone.text.toString().trim()
        email = binding.editEmail.text.toString().trim()


        if (nome.isEmpty()){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editNome.requestFocus()
            binding.editNome.error = ""
            return false
        }

        if (nome.matches(".*\\d.*".toRegex())){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_com_letras))
            binding.editNome.requestFocus()
            binding.editNome.error = ""
            return false
        }

        if (nome.length<2){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_min_duas_letras))
            binding.editNome.requestFocus()
            binding.editNome.error = ""
            return false
        }

        if (telefone.isEmpty()){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editTelefone.requestFocus()
            binding.editTelefone.error =""
            return false
        }


        if (!telefone.matches("^[0-9]*$".toRegex())){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_num_telefone_invalido))
            binding.editTelefone.requestFocus()
            binding.editTelefone.error = ""
            return false
        }else{
            if (telefone.length<9){
                MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_num_telefone_invalido))
                binding.editEmail.requestFocus()
                binding.editEmail.error = ""
                return false
            }
        }

        if (email.isEmpty()){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editEmail.requestFocus()
            binding.editEmail.error = ""

            return false
        }

        if (!MetodosUsados.validarEmail(email)) {
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_email_invalido))
            binding.editEmail.requestFocus()
            binding.editEmail.error = ""
            return false
        }else{
            email = email.lowercase()

        }



        binding.editNome.error =null
        binding.editTelefone.error =null
        binding.editEmail.error =null


        binding.editNome.onEditorAction(EditorInfo.IME_ACTION_DONE)
        binding.editTelefone.onEditorAction(EditorInfo.IME_ACTION_DONE)
        binding.editEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)


        return true
    }

    private fun activateViews(){
        binding.editNome.isEnabled = true
        binding.editTelefone.isEnabled = true
        binding.editEmail.isEnabled = true
        binding.btnSalvarPerfil.isEnabled = true

    }

    private fun deActivateViews(){
        binding.editNome.isEnabled = false
        binding.editTelefone.isEnabled = false
        binding.editEmail.isEnabled = false
        binding.btnSalvarPerfil.isEnabled = false

    }

    override fun onResume() {
        binding.editNome.error = null
        binding.editTelefone.error = null
        binding.editEmail.error = null
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}