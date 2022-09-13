package ao.co.proitconsulting.zoomunitel.ui.fragments.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ao.co.proitconsulting.zoomunitel.helpers.Resource
import ao.co.proitconsulting.zoomunitel.models.RevistaResponse
import ao.co.proitconsulting.zoomunitel.ui.repository.RevistaRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel(val revistaRepository: RevistaRepository)
    : ViewModel() {



    val home: MutableLiveData<Resource<RevistaResponse>> = MutableLiveData()

    init {
        getHome()
    }
    fun getHome() = viewModelScope.launch {
        home.postValue(Resource.Loading())
        val response = revistaRepository.getRevistasHome()
        home.postValue(handleHomeResponse(response))
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