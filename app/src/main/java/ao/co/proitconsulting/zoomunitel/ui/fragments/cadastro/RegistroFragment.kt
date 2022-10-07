package ao.co.proitconsulting.zoomunitel.ui.fragments.cadastro

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.databinding.FragmentRegistroBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings
import ao.co.proitconsulting.zoomunitel.models.UsuarioModel
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import ao.co.proitconsulting.zoomunitel.ui.activities.CadastroActivity
import ao.co.proitconsulting.zoomunitel.ui.activities.MainActivity
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RegistroFragment : Fragment() {
    private val TAG = "TAG_RegistFrag"

    private var _binding: FragmentRegistroBinding? = null
    private val binding get() = _binding!!

    private var nome:String?=null
    private var telefone:String?=null
    private var email:String?=null
    private var senha:String?=null
    private var confirmSenha:String?=null





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentRegistroBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setEditTextTint_Pre_lollipop()
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

        binding.editPass.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoPass(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.editConfirmPass.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoConfirmPass(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })



        val btnRegistro: Button = binding.btnRegistro
        btnRegistro.setOnClickListener {
            if (verificarCampos()){

                if (Constants.isNetworkAvailable){
                    registrar()
                }else{
                    MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_internet))
                }

            }
        }

        val spannableStringLogin = SpannableString(getString(R.string.ja_possui_conta_login))
        val fcsOrange = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.orange_unitel))
        spannableStringLogin.setSpan(fcsOrange,20,26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringLogin.setSpan(StyleSpan(Typeface.BOLD),20,26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val txtLogin : TextView = binding.txtLogin
        txtLogin.text = spannableStringLogin
        txtLogin.setOnClickListener {

            binding.editNome.error = null
            binding.editTelefone.error = null
            binding.editEmail.error = null
            binding.editPass.error = null
            binding.editConfirmPass.error = null

            val viewPager: ViewPager2? = CadastroActivity.getViewPager()
            viewPager?.currentItem = 0
        }

        return root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setEditTextTint_Pre_lollipop() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            context.let {

                binding.editPass.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray_color))
                binding.editConfirmPass.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray_color))
            }

        }
    }

    private fun registrar() {
        deActivateViews()
        binding.spinKitBottom.visibility = View.VISIBLE
        val registerRequest =  UsuarioRequest.RegisterRequest(
            nome,telefone,email,confirmSenha
        )
        val retrofit = RetrofitInstance.api.userRegister(registerRequest)
        retrofit.enqueue(object :
            Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    binding.editNome.setText("")
                    binding.editTelefone.setText("")
                    binding.editEmail.setText("")
                    binding.editPass.setText("")
                    binding.editConfirmPass.setText("")

                    AppPrefsSettings.getInstance().clearAppPrefs()

                    try {
                        var mensagem = response.body()?.string()
                        if (!mensagem.isNullOrEmpty()){
                            val jsonObject = JSONObject(mensagem)

                            mensagem = jsonObject.getString("msg")

                            showMessage(mensagem)

                        }


                        Log.d(TAG, "ResponseBody: $mensagem")
                    }catch (e: IOException){
                        Log.e(TAG, "onResponseIOException: ${e.message}")
                    }catch (e: JSONException){
                        Log.e(TAG, "onResponseJSONException: ${e.message}")
                    }

                } else{
                    binding.spinKitBottom.visibility = View.GONE
                    activateViews()

                    try {
                        val responseBodyError = response.errorBody()?.string()
                        if (!responseBodyError.isNullOrEmpty()){
                            val jsonResponseBodyError = JSONObject(responseBodyError)
                            val jsorError = jsonResponseBodyError.get("erro")
                            val jsonBodyError = JSONObject(jsorError.toString())
                            val errorMessage = jsonBodyError.get("mensagem")
                            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastERRO,errorMessage.toString())
                        }
                        Log.d(TAG, "onResponse_NOTsuccess: ${response.errorBody()?.string()}")
                    }catch (e: IOException){
                        Log.e(TAG, "onResponseIOException: ${e.message}")
                    }catch (e: JSONException){
                        Log.e(TAG, "onResponseJSONException: ${e.message}")
                    }
                }
            }



            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.spinKitBottom.visibility = View.GONE
                activateViews()


                if (!Constants.isNetworkAvailable){
                    MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_internet))
                }else if (!t.message.isNullOrEmpty() && t.message!!.contains("timeout")){
                    MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_internet_timeout))
                }else{
                    MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_servidor))
                }
            }

        })
    }

    private fun showMessage(mensagem: String) {
        MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastSUCESS,mensagem)


        Handler(Looper.getMainLooper()).postDelayed({
            autenticar()
        }, 3000)
    }

    private fun autenticar() {

        val loginRequest = UsuarioRequest.LoginRequest(email,confirmSenha)

        val retrofit = RetrofitInstance.api.userLogin(loginRequest)
        retrofit.enqueue(object :
            Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    if (response.body()!=null){
                        try {
                            val userData = response.body()?.string()
                            if (!userData.isNullOrEmpty()){
                                Log.d(TAG, "onResponse_success: $userData")
                                val jsonResponse = JSONObject(userData)
                                val usuario = UsuarioModel(
                                    jsonResponse.getInt("userid"),
                                    jsonResponse.getString("nome"),
                                    jsonResponse.getString("email"),
                                    jsonResponse.getString("telefone"),
                                    jsonResponse.getString("imagem")

                                )

                                binding.spinKitBottom.visibility = View.GONE
                                AppPrefsSettings.getInstance().saveUser(usuario)
                                AppPrefsSettings.getInstance().saveAuthToken(jsonResponse.getString("token"))
                                launchHomescreen()
                            }
                        }catch (e: IOException){
                            Log.e(TAG, "onResponseIOException: ${e.message}")
                        }catch (e: JSONException){
                            Log.e(TAG, "onResponseJSONException: ${e.message}")
                        }

                    }else{
                        binding.spinKitBottom.visibility = View.GONE
                        activateViews()
                        val viewPager: ViewPager2? = CadastroActivity.getViewPager()
                        viewPager?.currentItem = 0
                    }


                } else{
                    binding.spinKitBottom.visibility = View.GONE
                    activateViews()
                    val viewPager: ViewPager2? = CadastroActivity.getViewPager()
                    viewPager?.currentItem = 0
                }
            }



            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                binding.spinKitBottom.visibility = View.GONE
                activateViews()
                val viewPager: ViewPager2? = CadastroActivity.getViewPager()
                viewPager?.currentItem = 0
            }

        })
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

    private fun verificarCampoPass(senha: String) {
        if (senha.isEmpty()) {
            binding.editPass.requestFocus()
            binding.editPass.error = getString(R.string.msg_erro_campo_vazio)
            return
        }

        if (senha.length <=5){

            binding.editPass.requestFocus()
            binding.editPass.error = getString(R.string.msg_erro_password_fraca)
            return
        }


        this.senha = senha
    }

    private fun verificarCampoConfirmPass(confirmSenha: String) {
        if (confirmSenha.isEmpty()) {
            binding.editConfirmPass.requestFocus()
            binding.editConfirmPass.error = getString(R.string.msg_erro_campo_vazio)
            return
        }

        if (confirmSenha != senha){
            binding.editConfirmPass.requestFocus()
            binding.editConfirmPass.error = getString(R.string.msg_erro_password_diferentes)

            return
        }





        this.confirmSenha = confirmSenha
    }

    private fun verificarCampos():Boolean{

        nome = binding.editNome.text.toString().trim()
        telefone = binding.editTelefone.text.toString().trim()
        email = binding.editEmail.text.toString().trim()
        senha = binding.editPass.text.toString().trim()
        confirmSenha = binding.editConfirmPass.text.toString().trim()

        if (nome.isNullOrEmpty()){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editNome.requestFocus()
            binding.editNome.error = ""
            return false
        }

        if (nome!!.matches(".*\\d.*".toRegex())){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_com_letras))
            binding.editNome.requestFocus()
            binding.editNome.error = ""
            return false
        }

        if (nome!!.length<2){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_min_duas_letras))
            binding.editNome.requestFocus()
            binding.editNome.error = ""
            return false
        }

        if (telefone.isNullOrEmpty()){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editTelefone.requestFocus()
            binding.editTelefone.error =""
            return false
        }


        if (!telefone!!.matches("^[0-9]*$".toRegex())){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_num_telefone_invalido))
            binding.editTelefone.requestFocus()
            binding.editTelefone.error = ""
            return false
        }else{
            if (telefone!!.length<9){
                MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_num_telefone_invalido))
                binding.editEmail.requestFocus()
                binding.editEmail.error = ""
                return false
            }
        }

        if (email.isNullOrEmpty()){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editEmail.requestFocus()
            binding.editEmail.error = ""

            return false
        }

        if (!MetodosUsados.validarEmail(email!!)) {
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_email_invalido))
            binding.editEmail.requestFocus()
            binding.editEmail.error = ""
            return false
        }else{
            email = email!!.lowercase()

        }

        if (senha.isNullOrEmpty()) {
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editPass.requestFocus()
            binding.editPass.error = ""
            return false
        }

        if (senha!!.length <=5){
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_password_fraca))
            binding.editPass.requestFocus()
            binding.editPass.error = ""
            return false
        }

        if (confirmSenha.isNullOrEmpty()) {
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editConfirmPass.requestFocus()
            binding.editConfirmPass.error = ""
            return false
        }

        if (confirmSenha != senha){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_password_diferentes))
            binding.editConfirmPass.requestFocus()
            binding.editConfirmPass.error = ""

            return false
        }

        binding.editNome.error =null
        binding.editTelefone.error =null
        binding.editEmail.error =null
        binding.editPass.error =null
        binding.editConfirmPass.error =null

        binding.editNome.onEditorAction(EditorInfo.IME_ACTION_DONE)
        binding.editTelefone.onEditorAction(EditorInfo.IME_ACTION_DONE)
        binding.editEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)
        binding.editPass.onEditorAction(EditorInfo.IME_ACTION_DONE)
        binding.editConfirmPass.onEditorAction(EditorInfo.IME_ACTION_DONE)

        return true
    }

    private fun launchHomescreen() {
        if (activity!=null){
            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun activateViews(){
        binding.editNome.isEnabled = true
        binding.editTelefone.isEnabled = true
        binding.editEmail.isEnabled = true
        binding.editPass.isEnabled = true
        binding.editConfirmPass.isEnabled = true
        binding.btnRegistro.isEnabled = true
        binding.txtLogin.isEnabled = true
    }

    private fun deActivateViews(){
        binding.editNome.isEnabled = false
        binding.editTelefone.isEnabled = false
        binding.editEmail.isEnabled = false
        binding.editPass.isEnabled = false
        binding.editConfirmPass.isEnabled = false
        binding.btnRegistro.isEnabled = false
        binding.txtLogin.isEnabled = false
    }

    override fun onResume() {
        binding.editNome.error = null
        binding.editTelefone.error = null
        binding.editEmail.error = null
        binding.editPass.error = null
        binding.editConfirmPass.error = null
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}