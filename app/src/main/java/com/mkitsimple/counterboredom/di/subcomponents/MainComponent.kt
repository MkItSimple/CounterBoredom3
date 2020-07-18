package com.mkitsimple.counterboredom.di.subcomponents

import com.mkitsimple.counterboredom.di.modules.MainViewModelModule
import com.mkitsimple.counterboredom.di.modules.ViewModelFactoryModule
import com.mkitsimple.counterboredom.di.scopes.MainScope
import com.mkitsimple.counterboredom.ui.main.*
import dagger.Subcomponent

@MainScope
@Subcomponent(
    modules = [
        ViewModelFactoryModule::class,
        MainViewModelModule::class
    ]
)
interface MainComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(profileActivity: ProfileActivity)
    fun inject(friendsListFragment: FriendsListFragment)
    fun inject(latestChatsFragment: LatestChatsFragment)
    fun inject(chatLogActivity: ChatLogActivity)
}