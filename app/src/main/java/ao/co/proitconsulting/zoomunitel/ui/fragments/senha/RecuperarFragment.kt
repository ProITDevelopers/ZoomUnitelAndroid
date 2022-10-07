package ao.co.proitconsulting.zoomunitel.ui.fragments.senha

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.databinding.FragmentRecuperarBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import ao.co.proitconsulting.zoomunitel.ui.activities.SenhaActivity
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class RecuperarFragment : Fragment(){

    private val TAG = "TAG_RecupFrag"
    private var _binding: FragmentRecuperarBinding?=null
    private val binding get() = _binding!!

    private var email: String? = null





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecuperarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.editEmail.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                verificarCampoEmail(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        val btnContinuar : Button = binding.btnContinuar
        btnContinuar.setOnClickListener {

            if (verificarCampos()){

                if (Constants.isNetworkAvailable){
                    enviarEmail()
                }else{
                    MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_internet))
                }

            }


        }
        return root
    }

    private fun verificarCampoEmail(email:String){

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

    private fun verificarCampos(): Boolean {
        email = binding.editEmail.text.toString().trim()
        if (email.isNullOrEmpty()){
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            binding.editEmail.requestFocus()
            binding.editEmail.error = ""

            return false
        }

        if (!MetodosUsados.validarEmail(email!!)) {
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_email_invalido))
            binding.editEmail.requestFocus()
            binding.editEmail.error = ""
            return false
        }else{
            email = email!!.lowercase()

        }

        return true

    }

    private fun enviarEmail() {
        deActivateViews()
        binding.spinKitBottom.visibility = View.VISIBLE
        val passSendEmail = UsuarioRequest.PassSendEmail(email.toString())

        Constants.SEND_EMAIL = email.toString()


        val retrofit = RetrofitInstance.api.sendUserEmail(passSendEmail)
        retrofit.enqueue(object :
            Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    binding.spinKitBottom.visibility = View.GONE
                    activateViews()

                    if (response.body()!=null){

                        try {
                            val dataResponse = response.body()?.string()
                            if (!dataResponse.isNullOrEmpty()){
                                Log.d(TAG, "onResponse_success: $dataResponse")
                                val jsonResponse = JSONObject(dataResponse)
                                val mensagem = jsonResponse.get("mensagem")
                                MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastSUCESS,mensagem.toString())


                                Handler(Looper.getMainLooper()).postDelayed({
                                    goToNextFragment()
                                }, 5000)

                            }
                        }catch (e: IOException){
                            Log.e(TAG, "onResponseIOException: ${e.message}")
                        }catch (e: JSONException){
                            Log.e(TAG, "onResponseJSONException: ${e.message}")
                        }

                    }else{
                        goToNextFragment()
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

    private fun goToNextFragment() {

        binding.editEmail.error = null
        val viewPager: ViewPager2? = SenhaActivity.getViewPager()
        viewPager?.currentItem = 1

    }

    private fun activateViews(){
        binding.editEmail.isEnabled = true
        binding.btnContinuar.isEnabled = true

    }

    private fun deActivateViews(){
        binding.editEmail.isEnabled = false
        binding.btnContinuar.isEnabled = false

    }

    override fun onResume() {
        binding.editEmail.error = null
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}