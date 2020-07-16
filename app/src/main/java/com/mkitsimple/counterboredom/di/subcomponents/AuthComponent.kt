package com.mkitsimple.counterboredom.di.subcomponents

import com.mkitsimple.counterboredom.di.modules.AuthViewModelModule
import com.mkitsimple.counterboredom.di.modules.ViewModelFactoryModule
import com.mkitsimple.counterboredom.di.scopes.AuthScope
import com.mkitsimple.counterboredom.ui.auth.LoginActivity
import com.mkitsimple.counterboredom.ui.auth.RegisterActivity
import dagger.Subcomponent

@AuthScope
@Subcomponent(
    modules = [
        ViewModelFactoryModule::class,
        AuthViewModelModule::class
    ]
)
interface AuthComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
}