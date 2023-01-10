package com.dionis.escolinhajdb.di

import com.dionis.escolinhajdb.data.repository.AuthRepository
import com.dionis.escolinhajdb.data.repository.AuthRepositoryImpl
import com.dionis.escolinhajdb.data.repository.PlayerRepository
import com.dionis.escolinhajdb.data.repository.PlayerRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
//        appPreferences: SharedPreferences,
//        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImpl(auth, database//,appPreferences,gson
        )
    }

    @Provides
    @Singleton
    fun providePlayerRepository(
        database: FirebaseFirestore,
    ): PlayerRepository {
        return PlayerRepositoryImpl (database)
    }


}