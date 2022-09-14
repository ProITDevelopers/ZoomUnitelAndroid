package ao.co.proitconsulting.zoomunitel.ui.fragments.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ao.co.proitconsulting.zoomunitel.helpers.Constants
import ao.co.proitconsulting.zoomunitel.helpers.MetodosUsados
import ao.co.proitconsulting.zoomunitel.helpers.Resource
import ao.co.proitconsulting.zoomunitel.models.RevistaResponse
import ao.co.proitconsulting.zoomunitel.ui.repository.RevistaRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class HomeViewModel(val revistaRepository: RevistaRepository)
    : ViewModel() {

    val home: MutableLiveData<Resource<RevistaResponse>> = MutableLiveData()

    init {
        getHome()
    }
    fun getHome() = viewModelScope.launch {

        safeRevistasCall()

    }


    private suspend fun safeRevistasCall(){

        home.postValue(Resource.Loading())

        try {
            if (MetodosUsados.isConnected(Constants.REQUEST_TIMEOUT,"TAG_Homeview")){
                val response = revistaRepository.getRevistasHome()
                home.postValue(handleHomeResponse(response))
            }else{
                home.postValue(Resource.Error("Conecte-se á uma rede Wi-Fi ou active os dados móveis"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException -> home.postValue(Resource.Error("Falha na rede"))
                else -> home.postValue(Resource.Error("Erro de conversão"))
            }
        }
    }

    private fun handleHomeResponse(response: Response<RevistaResponse>) : Resource<RevistaResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->

                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

}