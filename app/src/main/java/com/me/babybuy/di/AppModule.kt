package com.me.babybuy.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.me.babybuy.data.repository.IAuthRepository
import com.me.babybuy.data.repository.IItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * A [Module] class for dependencies of the application.
 * To be injected into the hierarchy of the application.
 */
@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    fun provideAuthRepository(authRepository: IAuthRepository): IAuthRepository = authRepository

    @Provides
    fun provideItemRepository(itemRepository: IItemRepository): IItemRepository = itemRepository
}