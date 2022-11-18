package ao.co.proitconsulting.zoomunitel.models


sealed class DefinicoesModel {

    data class Header(
        val title: String
    ) : DefinicoesModel()

    data class About(
        val aboutTitle: String,
        val aboutDesc: String
    ) : DefinicoesModel()




}
