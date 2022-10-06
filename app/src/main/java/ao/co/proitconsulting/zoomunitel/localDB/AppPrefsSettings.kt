package ao.co.proitconsulting.zoomunitel.localDB

import android.content.Context
import android.content.SharedPreferences
import ao.co.proitconsulting.zoomunitel.ZoOmUnitelApplication
import ao.co.proitconsulting.zoomunitel.models.BookmarkRevistaModel
import ao.co.proitconsulting.zoomunitel.models.UsuarioModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class AppPrefsSettings {



    companion object {

        private const val APP_SHARED_PREF_NAME : String = "ZM_UNITEL_REF"
        private const val KEY_AUTH_TOKEN : String = "ZM_AUTH_TOKEN"
        private const val KEY_USER : String = "ZM_USER_KEY"
        private const val KEY_BOOKMARK : String = "ZM_BOOKMARK_KEY"


        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private lateinit var gson: Gson

        private var mInstance :AppPrefsSettings?= null

        @Synchronized fun getInstance():AppPrefsSettings {
            if(mInstance == null){
                mInstance = AppPrefsSettings(ZoOmUnitelApplication.getInstance().applicationContext)
            }
            return mInstance as AppPrefsSettings
        }
    }


    private constructor(mContext: Context){
        sharedPreferences = mContext.getSharedPreferences(APP_SHARED_PREF_NAME,Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
        gson = Gson()
    }



    //SAVE TOKEN
    fun saveAuthToken(authToken:String){
        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(KEY_AUTH_TOKEN, authToken)
        editor.apply()
    }




    //GET TOKEN
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }


    //SAVE USER DATA
    fun saveUser(usuario: UsuarioModel){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val userData:String = gson.toJson(usuario)
        editor.putString(KEY_USER, userData)
        editor.apply()
    }


    //GET USER DATA
    fun getUser():UsuarioModel?{

        val userData:String? = sharedPreferences.getString(KEY_USER, null)
        return  gson.fromJson(userData,UsuarioModel::class.java)
    }


    //BOOKMARK_REVISTA_READING
    fun saveBookmark(bookmarkList: ArrayList<BookmarkRevistaModel>){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val bookmarkData: String = gson.toJson(bookmarkList)
        editor.putString(KEY_BOOKMARK, bookmarkData)
        editor.apply()
    }

    fun getBookmark(): ArrayList<BookmarkRevistaModel> ? {
        val bookmarkData:String? = sharedPreferences.getString(KEY_BOOKMARK, null)
        val type: Type = object : TypeToken<ArrayList<BookmarkRevistaModel>>(){}.type
        val bookmarkList = gson.fromJson(bookmarkData,type) as? ArrayList<BookmarkRevistaModel>
        return bookmarkList
    }



    //DELETE ALL DATA
    fun clearAppPrefs(){
        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


}

