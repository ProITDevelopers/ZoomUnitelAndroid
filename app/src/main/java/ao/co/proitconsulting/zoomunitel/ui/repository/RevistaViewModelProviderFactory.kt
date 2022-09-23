package ao.co.proitconsulting.zoomunitel.ui.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ao.co.proitconsulting.zoomunitel.ui.fragments.home.HomeViewModel

class RevistaViewModelProviderFactory(
    private val revistaRepository: RevistaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(revistaRepository) as T
    }
}