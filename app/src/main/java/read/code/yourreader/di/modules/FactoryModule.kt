package read.code.yourreader.di.modules


import dagger.Module
import dagger.Provides
import read.code.yourreader.mvvm.factory.ModelFactory
import read.code.yourreader.mvvm.repository.BaseRepository

@Module
class FactoryModule constructor(private var mRepository: BaseRepository) {

    @Provides
    fun provideModalFactory(): ModelFactory {
        return ModelFactory(mRepository)
    }

    @Provides
    fun providesRepository(): BaseRepository {
        return mRepository
    }
}