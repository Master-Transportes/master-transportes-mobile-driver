package com.master.transportes.driver.di.auth

import com.master.transportes.driver.feature.auth.data.api.AuthApi
import com.master.transportes.driver.feature.auth.data.datasource.AuthRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    @Named("refreshAuthApi")
    fun provideRefreshAuthApi(
        @Named("refreshRetrofit") retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    @Named("refreshAuthDataSource")
    fun provideRefreshAuthRemoteDataSource(
        @Named("refreshAuthApi") api: AuthApi
    ): AuthRemoteDataSource {
        return AuthRemoteDataSource(api)
    }
}
