package ao.co.proitconsulting.zoomunitel.ui.fragments.senha

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
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
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.api.RetrofitInstance
import ao.co.proitconsulting.zoomunitel.databinding.FragmentInserirCodigoBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.helpers.network.ConnectionLiveData
import ao.co.proitconsulting.zoomunitel.models.PasswordRequest
import ao.co.proitconsulting.zoomunitel.ui.SenhaActivity
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "email"
class InserirCodigoFragment : Fragment() {

    val TAG="TAG_CodigoFrag"
    private var _binding: FragmentInserirCodigoBinding?=null
    private val binding get() = _binding!!

    private var email: String? = null
    private var codigo: String? = null
    lateinit var connectionLiveData: ConnectionLiveData
    private var isNetworkAvailable: Boolean = false

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
            InserirCodigoFragment().apply {
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
        _binding = FragmentInserirCodigoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val txtEmail : TextView = binding.txtEmail
        txtEmail.text = email.toString()


        val txtReenviarCode: TextView = binding.txtReenviarCode
        val spannableStringCode = SpannableString(getString(R.string.nao_recebeu_clique_aqui))
        val fcsOrange = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.orange_unitel))
        spannableStringCode.setSpan(fcsOrange,13,24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringCode.setSpan(StyleSpan(Typeface.BOLD),13,24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtReenviarCode.text = spannableStringCode
        txtReenviarCode.setOnClickListener {
            MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,txtReenviarCode.text.toString())
        }

        val btnContinuar : Button = binding.btnContinuar
        btnContinuar.setOnClickListener {
            if (verificarCampos()){
                isNetworkAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isNetworkAvailable
                }else{
                    MetodosUsados.isConnected(Constants.REQUEST_TIMEOUT,TAG)
                }

                if (isNetworkAvailable){
                    enviarCodigo()
                }else{
                    MetodosUsados.showCustomSnackBar(view,activity, Constants.ToastALERTA,getString(R.string.msg_erro_internet))
                }
            }

        }
        return root
    }

    private fun enviarCodigo() {
        deActivateViews()
        binding.spinKitBottom.visibility = View.VISIBLE
        val passwordRequest = PasswordRequest.Code(email.toString(),codigo.toString())

        val retrofit = RetrofitInstance.api.sendVerificationCode(passwordRequest)
        retrofit.enqueue(object :
            Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {

                    binding.spinKitBottom.visibility = View.GONE

                    goToNextFragment()
//                    if (response.body()!=null){
//                        val dataResponse = response.body()?.string()
//                        if (!dataResponse.isNullOrEmpty()){
//                            Log.d(TAG, "onResponse_success: $dataResponse")
//                            val jsonResponse = JSONObject(dataResponse)
//
//
//                            binding.spinKitBottom.visibility = View.GONE
//
//                            goToNextFragment()
//                        }
//                    }else{
//                        binding.spinKitBottom.visibility = View.GONE
//                        activateViews()
//                    }


                } else{
                    binding.spinKitBottom.visibility = View.GONE
                    activateViews()
                    val responseBodyError = response.errorBody()?.string()
                    if (!responseBodyError.isNullOrEmpty()){
                        val jsonResponseBodyError = JSONObject(responseBodyError)
                        val jsorError = jsonResponseBodyError.get("erro")
                        val jsonBodyError = JSONObject(jsorError.toString())
                        val errorMessage = jsonBodyError.get("mensagem")
                        MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastERRO,errorMessage.toString())
                    }
                    Log.d(TAG, "onResponse_NOTsuccess: ${response.errorBody()?.string()}")
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

    private fun verificarCampos(): Boolean {
        codigo = binding.firstPinView.text.toString().trim()
        if (codigo.isNullOrEmpty()){
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_campo_vazio))
            return false
        }

        if (codigo!!.length<5) {
            MetodosUsados.showCustomSnackBar(view,activity,Constants.ToastALERTA,getString(R.string.msg_erro_codigo_invalido))
            return false
        }

        binding.firstPinView.onEditorAction(EditorInfo.IME_ACTION_DONE)
        return true

    }

    private fun goToNextFragment() {
        if (activity!=null){
            val fragmentManager = (activity as SenhaActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            transaction.replace(R.id.frame_layout_senha, NovaPassFragment(), null)
            transaction.commit()
        }
    }

    private fun activateViews(){
        binding.firstPinView.isEnabled = true
        binding.btnContinuar.isEnabled = true

    }

    private fun deActivateViews(){
        binding.firstPinView.isEnabled = false
        binding.btnContinuar.isEnabled = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}