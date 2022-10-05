package ao.co.proitconsulting.zoomunitel.ui.fragments.senha

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.databinding.FragmentNovaPassBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NovaPassFragment : Fragment() {
    private val TAG="TAG_NovaPassFrag"
    private var _binding: FragmentNovaPassBinding?=null
    private val binding get() = _binding!!

    private var senha:String?=null
    private var confirmSenha:String?=null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNovaPassBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setEditTextTint_Pre_lollipop()
        binding.editPassword.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoPass(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.editConfirmPassword.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoConfirmPass(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        val btnContinuar : Button = binding.btnContinuar
        btnContinuar.setOnClickListener {
            if (verificarCampos()){

                if (Constants.isNetworkAvailable){
                    enviarNovaPass()
                }else{
                    MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(
                        R.string.msg_erro_internet))
                }
            }

        }
        return root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setEditTextTint_Pre_lollipop() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            context.let {

                binding.editPassword.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray_color))
                binding.editConfirmPassword.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.gray_color))
            }

        }
    }

    private fun verificarCampoPass(senha: String) {
        if (senha.isEmpty()) {
            binding.editPassword.requestFocus()
            binding.editPassword.error = getString(R.string.msg_erro_campo_vazio)
            return
        }

        if (senha.length <=5){

            binding.editPassword.requestFocus()
            binding.editPassword.error = getString(R.string.msg_erro_password_fraca)
            return
        }


        this.senha = senha
    }

    private fun verificarCampoConfirmPass(confirmSenha: String) {
        if (confirmSenha.isEmpty()) {
            binding.editConfirmPassword.requestFocus()
            binding.editConfirmPassword.error = getString(R.string.msg_erro_campo_vazio)
            return
        }


        if (confirmSenha != senha){
            binding.editConfirmPassword.requestFocus()
            binding.editConfirmPassword.error = getString(R.string.msg_erro_password_diferentes)

            return
        }




        this.confirmSenha = confirmSenha
    }

    private fun verificarCampos(): Boolean {
        senha = binding.editPassword.text.toString().trim()
        confirmSenha = binding.editConfirmPassword.text.toString().trim()

        if (senha.isNullOrEmpty()) {
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editPassword.requestFocus()
            binding.editPassword.error = ""
            return false
        }

        if (senha!!.length <=5){
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_password_fraca))
            binding.editPassword.requestFocus()
            binding.editPassword.error = ""
            return false
        }

        if (confirmSenha.isNullOrEmpty()) {
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editConfirmPassword.requestFocus()
            binding.editConfirmPassword.error = ""
            return false
        }

        if (confirmSenha != senha){
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_password_diferentes))
            binding.editConfirmPassword.requestFocus()
            binding.editConfirmPassword.error = ""

            return false
        }


        binding.editPassword.error =null
        binding.editConfirmPassword.error =null

        binding.editPassword.onEditorAction(EditorInfo.IME_ACTION_DONE)
        binding.editConfirmPassword.onEditorAction(EditorInfo.IME_ACTION_DONE)


        return true
    }

    private fun enviarNovaPass() {
        deActivateViews()
        binding.spinKitBottom.visibility = View.VISIBLE
        val passSendNewPass = UsuarioRequest.PassSendNewPass(confirmSenha)

        val retrofit = RetrofitInstance.api.resetPassWord(passSendNewPass)
        retrofit.enqueue(object :
            Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    binding.spinKitBottom.visibility = View.GONE
                    MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastSUCESS,getString(R.string.sucesso))

                    Handler(Looper.getMainLooper()).postDelayed({
                        activity?.finish()
                    }, 5000)

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
                    }catch (e:IOException){

                    }catch (e:JSONException){

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

    private fun activateViews(){
        binding.editPassword.isEnabled = true
        binding.editConfirmPassword.isEnabled = true
        binding.btnContinuar.isEnabled = true

    }

    private fun deActivateViews(){
        binding.editPassword.isEnabled = false
        binding.editConfirmPassword.isEnabled = false
        binding.btnContinuar.isEnabled = false

    }

    override fun onResume() {
        binding.editPassword.error = null
        binding.editConfirmPassword.error = null
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}