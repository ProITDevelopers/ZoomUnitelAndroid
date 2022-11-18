

package ao.co.proitconsulting.zoomunitel.ui.fragments.definicoes

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import ao.co.proitconsulting.zoomunitel.R
import ao.co.proitconsulting.zoomunitel.databinding.FragmentUserDocBinding
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.ui.activities.MainActivity
import com.github.ybq.android.spinkit.SpinKitView

@SuppressLint("SetJavaScriptEnabled")
class UserDocFragment : Fragment() {

    private val TAG = "TAG_UserDocFrag"
    private var _binding: FragmentUserDocBinding? = null
    private val binding get() = _binding!!




    private lateinit var coordinatorLayout: ConstraintLayout
    private lateinit var errorLayout: RelativeLayout
    private lateinit var imgErro: ImageView
    private lateinit var txtErro: TextView


    private lateinit var webView: WebView
    private lateinit var progressBar: SpinKitView
    private lateinit var txtProgress: TextView
    private var currentProgress = 0
    private val pdfUrl = Uri.parse(Constants.GOOGLE_DRIVE_LINK)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState!=null)
            webView.restoreState(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserDocBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val frameLayout = MainActivity.getFrameLayoutImgToolbar()
        if (frameLayout != null)
            frameLayout.visibility = View.GONE


        coordinatorLayout = root.findViewById(R.id.coordinatorLayout)
        errorLayout = root.findViewById(R.id.erroLayout)
        imgErro = root.findViewById(R.id.imgErro)
        txtErro = root.findViewById(R.id.txtErro)

        webView = binding.webView
        progressBar = binding.spinKitBottom
        txtProgress = binding.txtProgress

        webView.settings.javaScriptEnabled  = true
        webView.settings.domStorageEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false

        webView.webChromeClient = (object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, progress: Int) {
                super.onProgressChanged(view, progress)

                if (progress < 100){
                    txtProgress.text = StringBuilder("").append(progress).append("%").toString()
                    txtProgress.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    currentProgress = progress
                }


                if(progress == 100) {
                    txtProgress.text = StringBuilder("").append(progress).append("%").toString()
                    txtProgress.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    currentProgress = progress

                }
            }


        })


        webView.webViewClient = (object : WebViewClient() {

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
            }


            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if (currentProgress < 100){
                    view?.loadUrl(pdfUrl.toString())
                }else{
                    view?.loadUrl("javascript:(function() { " +
                            "document.getElementsByClassName('ndfHFb-c4YZDc-GSQQnc-LgbsSe ndfHFb-c4YZDc-to915-LgbsSe VIpgJd-TzA9Ye-eEGnhe ndfHFb-c4YZDc-LgbsSe')[0].style.display='none'; })()")
                }
            }


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }



        })





        webView.setOnClickListener {
            showAndHideViews()
        }



        return root
    }

    private fun showAndHideViews() {
        if (((activity as MainActivity)).supportActionBar != null){
            if (((activity as MainActivity)).supportActionBar!!.isShowing){
                ((activity as MainActivity)).supportActionBar!!.hide()

            }else{
                ((activity as MainActivity)).supportActionBar!!.show()

            }

        }
    }



    private fun showErrorScreen(msg:String){

        if (msg.contains("Wi-Fi")){
            imgErro.setImageResource(R.drawable.ic_baseline_wifi_off_24)
            txtErro.text = msg
        }else{
            imgErro.setImageResource(R.drawable.ic_baseline_error_outline_24)
            txtErro.text = msg
        }


        if (errorLayout.visibility == View.GONE){
            errorLayout.visibility = View.VISIBLE
            coordinatorLayout.visibility = View.GONE
        }
    }


    override fun onResume() {
        if (Constants.isNetworkAvailable){

            if (errorLayout.visibility == View.VISIBLE){
                errorLayout.visibility = View.GONE
                coordinatorLayout.visibility = View.VISIBLE
            }
            webView.loadUrl(pdfUrl.toString())

        }else{
            showErrorScreen(getString(R.string.msg_erro_internet))

        }
        super.onResume()
    }


    override fun onDestroyView() {

        if (((activity as MainActivity)).supportActionBar != null){
            if (!((activity as MainActivity)).supportActionBar!!.isShowing){
                ((activity as MainActivity)).supportActionBar!!.show()
            }
        }

        super.onDestroyView()
        _binding = null
    }
}


