package read.code.yourreader.di.components



import dagger.Component
import read.code.yourreader.di.modules.FactoryModule
import read.code.yourreader.di.modules.RepositoryModule
import read.code.yourreader.mvvm.factory.ModelFactory

@Component(modules = [FactoryModule::class, RepositoryModule::class])
interface FactoryComponent {

    fun getFactory(): ModelFactory




}
