package read.code.yourreader.Room

import androidx.lifecycle.LiveData
import androidx.room.*
import read.code.yourreader.data.Files

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(files: Files)

    @Update
    suspend fun update(files: Files)

    @Delete
    suspend fun delete(files: Files)

    @Query("SELECT * FROM files_table")
    fun getFiles(): LiveData<List<Files>>
}