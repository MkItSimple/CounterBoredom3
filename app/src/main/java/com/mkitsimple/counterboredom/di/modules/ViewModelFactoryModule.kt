package com.mkitsimple.counterboredom.di.modules

import androidx.lifecycle.ViewModelProvider
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}