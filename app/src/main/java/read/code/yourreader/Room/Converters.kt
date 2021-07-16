//package read.code.yourreader.Room
//
//import androidx.room.TypeConverter
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//
//
//class Converters {
//
//    @TypeConverter
//    fun fromList(value: List<String>?): String = Json.encodeToString(value)
//
//    @TypeConverter
//    fun toList(value: String) = Json.decodeFromString<ArrayList<String>>(value)
//}