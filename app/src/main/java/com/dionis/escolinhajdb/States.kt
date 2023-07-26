package com.dionis.escolinhajdb

abstract class States {

    sealed class ValidateRegisterPlayer {
        object PlayerNameEmpty : ValidateRegisterPlayer()
        object ResponsibleNameEmpty : ValidateRegisterPlayer()
        object PlayersBirthEmpty : ValidateRegisterPlayer()
        object PlayerGenreEmpty : ValidateRegisterPlayer()
        object PlayerCategoryEmpty : ValidateRegisterPlayer()
        object FieldsDone : ValidateRegisterPlayer()
    }

    sealed class ValidateRegisterEvent() {
        object EventTitleEmpty: ValidateRegisterEvent()
        object EventDescriptionEmpty: ValidateRegisterEvent()
        object FieldsDone: ValidateRegisterEvent()
    }


}