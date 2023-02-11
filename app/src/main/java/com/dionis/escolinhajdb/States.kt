package com.dionis.escolinhajdb

abstract class States {

    sealed class ValidateRegisterPlayer {
        object PlayerNameEmpty : ValidateRegisterPlayer()
        object ResponsibleNameEmpty : ValidateRegisterPlayer()
        object PlayersBirthEmpty : ValidateRegisterPlayer()
        object PlayerGenreEmpty : ValidateRegisterPlayer()
        object FieldsDone : ValidateRegisterPlayer()
    }


}