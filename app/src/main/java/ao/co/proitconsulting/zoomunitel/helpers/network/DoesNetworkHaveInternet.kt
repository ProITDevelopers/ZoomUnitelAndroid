package ao.co.proitconsulting.zoomunitel.helpers.network

import android.util.Log
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

object DoesNetworkHaveInternet {

    private const val TAG="TAG_DoesNetHaveNet"
    // Make sure to execute this on a background thread.
    fun execute(socketFactory: SocketFactory): Boolean {
        return try{
            Log.d(TAG, "PINGING google.")
            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
            socket.connect(InetSocketAddress("8.8.8.8", 53), 3000)
            socket.close()
            Log.d(TAG, "PING success.")
            true
        }catch (e: IOException){
            Log.e(TAG, "No internet connection. ${e}")
            false
        }
    }
}