package ao.co.proitconsulting.zoomunitel.ui.fragments.senha

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
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.databinding.FragmentRecuperarBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.helpers.network.ConnectionLiveData
import ao.co.proitconsulting.zoomunitel.models.UsuarioRequest
import ao.co.proitconsulting.zoomunitel.ui.SenhaActivity
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


private const val ARG_PARAM1 = "email"
class RecuperarFragment : Fragment(){

    val TAG = "TAG_RecupFrag"
    private var _binding: FragmentRecuperarBinding?=null
    private val binding get() = _binding!!

    private var email: String? = null
    lateinit var connectionLiveData: ConnectionLiveData
    private var isNetworkAvailable: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            RecuperarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectionLiveData = ConnectionLiveData(requireContext())
            connectionLiveData.observe(this) { isNetwork ->
                isNetworkAvailable = isNetwork
            }
        }else{
            isNetworkAvailable = MetodosUsados.isConnected(Constants.REQUEST_TIMEOUT, TAG)
        }
        super.onCreate(savedInstanceState)
        arguments?.let {
            email = it.getString(ARG_PARAM1)
        }
    }

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

                isNetworkAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isNetworkAvailable
                }else{
                    MetodosUsados.isConnected(Constants.REQUEST_TIMEOUT,TAG)
                }

                if (isNetworkAvailable){
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
        Log.d(TAG, "enviarEmail: $passSendEmail")

        val retrofit = RetrofitInstance.api.sendUserEmail(passSendEmail)
        retrofit.enqueue(object :
            Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    binding.spinKitBottom.visibility = View.GONE


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
                        }catch (e:IOException){

                        }catch (e:JSONException){

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
                    }catch (e:IOException){

                    }catch (e:JSONException){

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

    private fun goToNextFragment() {

        binding.editEmail.error = null
        val viewPager: ViewPager2? = SenhaActivity.getViewPager()
        viewPager?.currentItem = 1
        /*
        *if (activity!=null){
            val fragmentManager = (activity as SenhaActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            transaction.replace(R.id.frame_layout_senha, InserirCodigoFragment.newInstance(email.toString()), null)
            transaction.commit()
        }**/
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