package com.mkitsimple.counterboredom.di.modules

import com.mkitsimple.counterboredom.data.network.Api
import com.mkitsimple.counterboredom.data.repositories.AuthRepository
import com.mkitsimple.counterboredom.data.repositories.MessageRepository
import com.mkitsimple.counterboredom.data.repositories.UserRepository
import com.mkitsimple.counterboredom.di.scopes.AppScope
import com.mkitsimple.counterboredom.utils.BASE_URL
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AppModule {
    @AppScope
    @Provides
    fun provideRetrofit() =
            Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
//    @AppScope
//    @Provides
//    fun provideFeedService(builder: Retrofit.Builder) =
//            builder.baseUrl(BASE_URL).build().create(Api::class.java)

    @AppScope
    @Provides
    fun provideApi() = Api::class.java

    @AppScope
    @Provides
    fun provideMessageRepository(api: Api) = MessageRepository(api)

    @AppScope
    @Provides
    fun provideAuthRepository() = AuthRepository()

    @AppScope
    @Provides
    fun provideUserRepository() = UserRepository()
}