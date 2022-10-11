package ao.co.proitconsulting.zoomunitel.api.prelollipop

import android.os.Build
import okhttp3.OkHttpClient
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class GetUnsafeOkHttpClientSecurity {

    companion object{
        fun getUnsafeOkHttpClient() : OkHttpClient.Builder{
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }
                })

                val builder = OkHttpClient.Builder()
                /*
                ** RETROFIT REQUEST FOR WORKING ON PRE LOLLIPOP DEVICES
                 */

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    try {

                        builder.sslSocketFactory(TLSSocketFactory(trustAllCerts))
                    } catch (e: KeyManagementException) {
                        e.printStackTrace()
                    } catch (e: NoSuchAlgorithmException) {
                        e.printStackTrace()
                    }

                }else{
                    // Install the all-trusting trust manager
                    val sslContext = SSLContext.getInstance("SSL")
                    sslContext.init(null, trustAllCerts, java.security.SecureRandom())

                    // Create an ssl socket factory with our all-trusting manager
                    val sslSocketFactory = sslContext.socketFactory
                    builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                }
                builder.hostnameVerifier { s, sslSession ->
                    return@hostnameVerifier true
                }
                return builder
            }catch (e:Exception){
                throw RuntimeException(e)
            }
        }
    }
}