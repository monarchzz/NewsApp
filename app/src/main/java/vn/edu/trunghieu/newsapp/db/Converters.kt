package vn.edu.trunghieu.newsapp.db

import androidx.room.TypeConverter
import vn.edu.trunghieu.newsapp.model.Article
import vn.edu.trunghieu.newsapp.model.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source) : String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String) : Source {
        return Source(name,name)
    }

}