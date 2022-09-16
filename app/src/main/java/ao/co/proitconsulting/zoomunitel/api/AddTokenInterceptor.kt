package ao.co.proitconsulting.zoomunitel.api

import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AddTokenInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        builder.addHeader("Content-type", "application/json")
        builder.addHeader("Accept", "application/json")

        if (AppPrefsSettings.getInstance().getAuthToken()!=null) {
            builder.header("Authorization", "Bearer "+AppPrefsSettings.getInstance().getAuthToken())
        }
        return chain.proceed(builder.build())
    }

}