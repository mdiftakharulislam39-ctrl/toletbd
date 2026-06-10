package com.pronaycoding.toletapp.di

import com.google.firebase.auth.FirebaseUser
import com.pronaycoding.toletapp.data.FirebaseDatabaseProvider
import com.pronaycoding.toletapp.data.model.ToletListing
import com.pronaycoding.toletapp.data.model.UserProfile
import com.pronaycoding.toletapp.data.repository.AccountRepositoryImpl
import com.pronaycoding.toletapp.data.repository.AuthRepositoryImpl
import com.pronaycoding.toletapp.data.repository.ChatRepositoryImpl
import com.pronaycoding.toletapp.data.repository.ToletRepositoryImpl
import com.pronaycoding.toletapp.data.repository.UserRepositoryImpl
import com.pronaycoding.toletapp.domain.repository.AccountRepository
import com.pronaycoding.toletapp.domain.repository.AuthRepository
import com.pronaycoding.toletapp.domain.repository.ChatRepository
import com.pronaycoding.toletapp.domain.repository.ToletRepository
import com.pronaycoding.toletapp.domain.repository.UserRepository
import com.pronaycoding.toletapp.ui.add.AddToletViewModel
import com.pronaycoding.toletapp.ui.chat.ChatListViewModel
import com.pronaycoding.toletapp.ui.chat.ChatRouteViewModel
import com.pronaycoding.toletapp.ui.chat.ChatViewModel
import com.pronaycoding.toletapp.ui.detail.ListingDetailLoaderViewModel
import com.pronaycoding.toletapp.ui.detail.ListingDetailViewModel
import com.pronaycoding.toletapp.ui.home.HomeViewModel
import com.pronaycoding.toletapp.ui.phone.PhoneViewModel
import com.pronaycoding.toletapp.ui.profile.MyListingsViewModel
import com.pronaycoding.toletapp.ui.profile.ProfileViewModel
import com.pronaycoding.toletapp.ui.saved.SavedViewModel
import com.pronaycoding.toletapp.ui.session.SessionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single { FirebaseDatabaseProvider.database }
    single<ToletRepository> { ToletRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(androidContext()) }
}

val viewModelModule = module {
    viewModel { SessionViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { (userId: String) -> SavedViewModel(get(), get(), userId) }
    viewModel { (userId: String) -> ChatListViewModel(get(), get(), userId) }
    viewModel { (currentUserId: String, otherUser: UserProfile) ->
        ChatViewModel(get(), currentUserId, otherUser)
    }
    viewModel { (listingId: String) -> ListingDetailLoaderViewModel(get(), listingId) }
    viewModel { (listing: ToletListing, currentUserId: String) ->
        ListingDetailViewModel(get(), listing, currentUserId)
    }
    viewModel { ProfileViewModel(get()) }
    viewModel { (userId: String) -> MyListingsViewModel(get(), userId) }
    viewModel { (user: FirebaseUser, listingToEdit: ToletListing?) ->
        AddToletViewModel(get(), user, listingToEdit)
    }
    viewModel { (user: FirebaseUser) -> PhoneViewModel(get(), user) }
    viewModel { (otherUserId: String) -> ChatRouteViewModel(get(), otherUserId) }
}

val appModules = listOf(dataModule, viewModelModule)
