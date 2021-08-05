package read.code.yourreader.Room

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import read.code.yourreader.data.Files

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(files: Files)

    @Update
    suspend fun update(files: Files)

    @Delete
    suspend fun delete(files: Files)

    @Query("SELECT * FROM files_table WHERE favorites = :isFavorite or favorites = 1 ")
    fun getFiles(isFavorite: Boolean = false): Flow<List<Files>>

    @Query("SELECT * FROM files_table WHERE doneReading = 1")
    fun getDoneFiles(): Flow<List<Files>>

    @Query("DELETE FROM files_table")
    suspend fun deleteDatabase()

    @Query("SELECT * FROM files_table WHERE readingNow = 1")
    fun getCurrentReadingFiles(): Flow<List<Files>>

    @Query("SELECT * FROM files_table WHERE inTrash = 1 ")
    fun getTrashFiles(): Flow<List<Files>>
}
