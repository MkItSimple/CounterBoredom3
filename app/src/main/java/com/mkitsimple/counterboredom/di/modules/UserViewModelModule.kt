package com.mkitsimple.counterboredom.di.modules

import androidx.lifecycle.ViewModel
import com.mkitsimple.counterboredom.viewmodels.UserViewModel
import com.mkitsimple.counterboredom.viewmodels.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class UserViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    internal abstract fun bindUserViewModel(viewModel: UserViewModel): ViewModel
}