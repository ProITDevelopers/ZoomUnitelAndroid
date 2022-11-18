package ao.co.proitconsulting.zoomunitel.ui.fragments.definicoes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ao.co.proitconsulting.zoomunitel.BuildConfig
import ao.co.proitconsulting.zoomunitel.models.DefinicoesModel

class DefinicoesViewModel : ViewModel() {


    private val _text = MutableLiveData<List<DefinicoesModel>>().apply {
        value = getMockData()
    }
    val getList: LiveData<List<DefinicoesModel>> = _text

    private fun getMockData(): List<DefinicoesModel> = listOf(
        DefinicoesModel.Header(
            title = "Sobre"
        ),
        DefinicoesModel.About(
            aboutTitle = "ZoOm Unitel",
            aboutDesc = "A ZoOm Magazine é uma publicação anual promovida pela Academia " +
                    "Unitel, dedicada à partilha de conhecimento sobre projectos (internos e externos), tecnologias e investigação."
        ),
        DefinicoesModel.About(
            aboutTitle = "Partilhar",
            aboutDesc = "Partilhe o link da app com os seus contactos."
        ),
        DefinicoesModel.About(
            aboutTitle = "Enviar feedback",
            aboutDesc = "Tem alguma dúvida? Estamos felizes em ajudar."
        ),
        DefinicoesModel.About(
            aboutTitle = "Desenvolvedor",
            aboutDesc = "Copyright © 2021 - Powered by Pro-IT Consulting"
        ),
        DefinicoesModel.About(
            aboutTitle = "Versão",
            aboutDesc = BuildConfig.VERSION_NAME
        )



    )
}