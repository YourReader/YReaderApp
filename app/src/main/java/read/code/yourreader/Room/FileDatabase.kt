package read.code.yourreader.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import read.code.yourreader.data.Files

@Database(entities = [Files::class], version = 1)
abstract class FileDatabase : RoomDatabase() {

    abstract fun filesDao(): FileDao

    companion object {
        @Volatile
        private var INSTANCE: FileDatabase? = null

        fun getDatabase(context: Context): FileDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        FileDatabase::class.java,
                        "files_Database"
                    ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}