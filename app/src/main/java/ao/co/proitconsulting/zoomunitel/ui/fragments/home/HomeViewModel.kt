package ao.co.proitconsulting.zoomunitel.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Home Fragment"
    }
    val text: LiveData<String> = _text


    fun setText(text:String){
        _text.value = text
    }
}