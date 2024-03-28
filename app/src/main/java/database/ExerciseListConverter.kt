package database

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ExerciseListConverter {
    @TypeConverter
    fun fromList(value : MutableList<Exercise>) : String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toList(value:String) : MutableList<Exercise>{
        val listType = object : TypeToken<List<Exercise>>() {}.type
        return Gson().fromJson(value,listType)
    }

}