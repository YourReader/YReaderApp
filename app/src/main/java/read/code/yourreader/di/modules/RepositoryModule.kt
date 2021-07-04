package read.code.yourreader.di.modules

import android.content.Context

import dagger.Module
import dagger.Provides
import read.code.yourreader.mvvm.repository.AuthRepository
import read.code.yourreader.mvvm.repository.BaseRepository
import read.code.yourreader.mvvm.repository.MainRepository

@Module
class RepositoryModule constructor(private var context: Context){

    @Provides
    fun provideRepository(): BaseRepository {
        return AuthRepository(context = context)
    }
    @Provides
    fun provideMainRepository(): BaseRepository {
        return MainRepository(context = context)
    }



}