package read.code.yourreader.di.modules


import dagger.Module
import dagger.Provides
import read.code.yourreader.mvvm.factory.ModelFactory
import read.code.yourreader.mvvm.repository.BaseRepository

@Module
class FactoryModule constructor(private var authRepository: BaseRepository) {

    @Provides
    fun provideModalFactory(): ModelFactory {
        return ModelFactory(authRepository)
    }

    @Provides
    fun providesRepository(): BaseRepository {
        return authRepository
    }
}