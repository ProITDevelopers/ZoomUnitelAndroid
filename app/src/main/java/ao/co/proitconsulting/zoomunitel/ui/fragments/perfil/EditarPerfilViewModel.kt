package ao.co.proitconsulting.zoomunitel.ui.fragments.perfil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ao.co.proitconsulting.zoomunitel.localDB.AppPrefsSettings
import ao.co.proitconsulting.zoomunitel.models.UsuarioModel

class EditarPerfilViewModel : ViewModel() {


    private val _usuario = MutableLiveData<UsuarioModel>().apply {
        value = getUsuario()
    }
    val getPerfil: LiveData<UsuarioModel> = _usuario

    private fun getUsuario(): UsuarioModel? {
        val usuario = AppPrefsSettings.getInstance().getUser()
        return if (usuario!=null)
            usuario
        else
            null
    }

}