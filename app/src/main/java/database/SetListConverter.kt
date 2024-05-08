package database

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class SetListConverter {
    @TypeConverter
    fun fromList(value : MutableList<SetRep>) : String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toList(value:String) : MutableList<SetRep>{
        val listType = object : TypeToken<List<SetRep>>() {}.type
        return Gson().fromJson(value,listType)
    }

}