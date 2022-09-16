package ao.co.proitconsulting.zoomunitel.ui.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ao.co.proitconsulting.zoomunitel.ui.repository.RevistaRepository

class HomeViewModel(revistaRepository: RevistaRepository) : ViewModel() {

    val home = revistaRepository.getRevistasHome().asLiveData()

}