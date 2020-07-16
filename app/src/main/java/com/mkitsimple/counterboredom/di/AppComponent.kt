package com.mkitsimple.counterboredom.di

import com.mkitsimple.counterboredom.di.modules.AppModule
import com.mkitsimple.counterboredom.di.scopes.AppScope
import com.mkitsimple.counterboredom.di.subcomponents.AuthComponent
import com.mkitsimple.counterboredom.di.subcomponents.MainComponent
import dagger.Component

@AppScope
@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent {
    fun newMainComponent(): MainComponent
    fun newAuthComponent(): AuthComponent
}