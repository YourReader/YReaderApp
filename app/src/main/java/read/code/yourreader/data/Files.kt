package read.code.yourreader.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "files_table")
@Parcelize
data class Files(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val path: String,
    val type: String,
    val favorites: Boolean = false,
    val readingNow: Boolean = false
) : Parcelable