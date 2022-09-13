package ao.co.proitconsulting.zoomunitel.ui.fragments.cadastro

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
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
import ao.co.proitconsulting.zoomunitel.databinding.FragmentLoginBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.helpers.network.ConnectionLiveData
import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings
import ao.co.proitconsulting.zoomunitel.models.UsuarioModel
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import ao.co.proitconsulting.zoomunitel.ui.CadastroActivity
import ao.co.proitconsulting.zoomunitel.ui.MainActivity
import ao.co.proitconsulting.zoomunitel.ui.SenhaActivity
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LoginFragment : Fragment() {
val TAG = "TAG_LoginFrag"


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!



    lateinit var emailTelefone :String
    lateinit var password :String


    lateinit var connectionLiveData: ConnectionLiveData
    private var isNetworkAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectionLiveData = ConnectionLiveData(requireContext())
            connectionLiveData.observe(this) { isNetwork ->
               isNetworkAvailable = isNetwork
            }
        }else{
            isNetworkAvailable = MetodosUsados.isConnected(Constants.REQUEST_TIMEOUT,TAG)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view: View = binding.root


        binding.editEmail.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoEmail(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.editPassword.addTextChangedListener(object : TextWatcher{

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoPass(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        val btnLogin: Button = binding.btnLogin
        btnLogin.setOnClickListener {
            if (verificarCampos()){

                isNetworkAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isNetworkAvailable
                }else{
                    MetodosUsados.isConnected(Constants.REQUEST_TIMEOUT,TAG)
                }

                if (isNetworkAvailable){

                    autenticar()
                }else{
                    MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_internet))
                }

            }
        }

        val spannableString = SpannableString(getString(R.string.hint_registe_se))
        val spannableStringSenha = SpannableString(getString(R.string.hint_forgot_password))

        val fcsWhite = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.white))
        val fcsBlue = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue_unitel))
        spannableString.setSpan(fcsWhite,0,18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.BOLD),19,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(fcsBlue,19,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringSenha.setSpan(fcsWhite,0,spannableStringSenha.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val txtForgotPassword: TextView = binding.txtForgotPassword
        txtForgotPassword.text = spannableStringSenha
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(activity, SenhaActivity::class.java))

        }

        val txtRegister: TextView = binding.txtRegister
        txtRegister.text = spannableString
        txtRegister.setOnClickListener {

            binding.editEmail.error = null
            binding.editPassword.error = null

            val viewPager: ViewPager2? = CadastroActivity.getViewPager()
            viewPager?.currentItem = 1


        }

        return view
    }

    private fun verificarCampoPass(password: String) {
        if (password.isEmpty()) {
            binding.editPassword.requestFocus()
            binding.editPassword.error = getString(R.string.msg_erro_campo_vazio)
            return
        }

        if (password.length <=5){

            binding.editPassword.requestFocus()
            binding.editPassword.error = getString(R.string.msg_erro_password_fraca)
            return
        }


        this.password = password

    }

    private fun verificarCampoEmail(emailTelefone:String){

        if (emailTelefone.isEmpty()){
            binding.editEmail.requestFocus()
            binding.editEmail.error = getString(R.string.msg_erro_campo_vazio)
            binding.editPassword.error = null
            return
        }

        if (MetodosUsados.validarEmail(emailTelefone)) {
            this.emailTelefone = emailTelefone.lowercase()


        }else {
            if (emailTelefone.matches("^[0-9]*$".toRegex())){

                if (emailTelefone.length<9){
                    binding.editEmail.requestFocus()
                    binding.editEmail.error = getString(R.string.msg_erro_num_telefone_invalido)
                    binding.editPassword.error = null
                    return
                }else{
                    this.emailTelefone = emailTelefone
                }

            } else {

                binding.editEmail.requestFocus()
                binding.editEmail.error = getString(R.string.msg_erro_campo_com_email_telefone)
                binding.editPassword.error = null
                return
            }
        }



    }

    private fun verificarCampos(): Boolean {
        emailTelefone = binding.editEmail.text.toString().trim()
        password = binding.editPassword.text.toString().trim()

        if (emailTelefone.isEmpty()){
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editEmail.requestFocus()
            binding.editEmail.error = ""
            binding.editPassword.error = null
            return false
        }

        if (MetodosUsados.validarEmail(emailTelefone)) {
            emailTelefone = emailTelefone.lowercase()


        }else {
            if (emailTelefone.matches("^[0-9]*$".toRegex())){
                emailTelefone = emailTelefone
                return true
            } else {
                MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_campo_com_email_telefone))
                binding.editEmail.requestFocus()
                binding.editEmail.error = ""
                binding.editPassword.error = null
                return false
            }
        }

        if (password.isEmpty()) {
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editPassword.requestFocus()
            binding.editPassword.error = ""
            return false
        }

        if (password.length <=5){
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_password_fraca))
            binding.editPassword.requestFocus()
            binding.editPassword.error = ""
            return false
        }




        binding.editEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)
        binding.editPassword.onEditorAction(EditorInfo.IME_ACTION_DONE)



        return true

    }

    private fun autenticar() {
        deActivateViews()
        binding.spinKitBottom.visibility = View.VISIBLE
        val loginRequest = UsuarioRequest.LoginRequest(emailTelefone,password)

        Log.d(TAG, "autenticar: ${loginRequest.toString()}")

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
                                    jsonResponse.getLong("userid"),
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
                        }catch (e:IOException){

                        }catch (e: JSONException){

                        }
                    }else{
                        binding.spinKitBottom.visibility = View.GONE
                        activateViews()
                    }


                } else{
                    binding.spinKitBottom.visibility = View.GONE
                    activateViews()

                    try{
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

                    }catch (e: JSONException){

                    }

                }
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






    private fun launchHomescreen() {
        if (activity!=null){
            val intent = Intent(activity,MainActivity::class.java)
            intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            activity?.finish()
        }



    }

    private fun activateViews(){
        binding.editEmail.isEnabled = true
        binding.editPassword.isEnabled = true
        binding.txtForgotPassword.isEnabled = true
        binding.btnLogin.isEnabled = true
        binding.txtRegister.isEnabled = true
    }

    private fun deActivateViews(){
        binding.editEmail.isEnabled = false
        binding.editPassword.isEnabled = false
        binding.txtForgotPassword.isEnabled = false
        binding.btnLogin.isEnabled = false
        binding.txtRegister.isEnabled = false
    }

    override fun onResume() {
        binding.editEmail.error =null
        binding.editPassword.error =null
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}