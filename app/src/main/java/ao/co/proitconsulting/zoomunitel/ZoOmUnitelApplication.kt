package ao.co.proitconsulting.zoomunitel

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

class ZoOmUnitelApplication : Application() {



    companion object{
        lateinit var mInstance : ZoOmUnitelApplication
        fun  getInstance():Context {
            return mInstance
        }
    }

    override fun onCreate() {
        mInstance = this
        super.onCreate()
    }

    /*
    //**FOR WORKING ON PRE LOLLIPOP DEVICES
    //
    */*/
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        MultiDex.install(this)
    }



}