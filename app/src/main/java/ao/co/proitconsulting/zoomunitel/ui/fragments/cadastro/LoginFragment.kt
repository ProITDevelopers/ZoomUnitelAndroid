package ao.co.proitconsulting.zoomunitel.ui.fragments.cadastro

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Drawable
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
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.databinding.FragmentLoginBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings
import ao.co.proitconsulting.zoomunitel.models.UsuarioModel
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import ao.co.proitconsulting.zoomunitel.ui.activities.CadastroActivity
import ao.co.proitconsulting.zoomunitel.ui.activities.MainActivity
import ao.co.proitconsulting.zoomunitel.ui.activities.SenhaActivity
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@Suppress("DEPRECATION")
class LoginFragment : Fragment() {
val TAG = "TAG_LoginFrag"


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!



    private lateinit var emailTelefone :String
    private lateinit var password :String

    //NORMAL_DRAWABLE_COLORS
    private lateinit var editPasswordDrawable: Drawable


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view: View = binding.root

        setEditTextTint_Pre_lollipop()
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



                if (Constants.isNetworkAvailable){
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
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setEditTextTint_Pre_lollipop() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            context.let {
                editPasswordDrawable = resources.getDrawable(R.drawable.ic_baseline_lock_24)
                editPasswordDrawable = DrawableCompat.wrap(editPasswordDrawable)

                DrawableCompat.setTint(editPasswordDrawable,resources.getColor(R.color.orange_unitel))
                DrawableCompat.setTintMode(editPasswordDrawable, PorterDuff.Mode.SRC_IN)

                binding.editPassword.setCompoundDrawablesWithIntrinsicBounds(editPasswordDrawable,null,null,null)
                binding.editPassword.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray_color))
            }

        }
    }

    private fun verificarCampoPass(password: String) {
        if (password.isEmpty()) {
            binding.editPassword.requestFocus()
            binding.editPassword.error = getString(R.string.msg_erro_campo_vazio)
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
            return if (emailTelefone.matches("^[0-9]*$".toRegex())){
                emailTelefone = emailTelefone
                true
            } else {
                MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_campo_com_email_telefone))
                binding.editEmail.requestFocus()
                binding.editEmail.error = ""
                binding.editPassword.error = null
                false
            }
        }

        if (password.isEmpty()) {
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
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

        Log.d(TAG, "autenticar: $loginRequest")

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






    private fun launchHomescreen() {
        if (activity!=null){
            val intent = Intent(activity, MainActivity::class.java)
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