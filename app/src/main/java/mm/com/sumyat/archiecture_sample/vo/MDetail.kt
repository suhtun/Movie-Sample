package mm.com.sumyat.archiecture_sample.vo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    primaryKeys = ["id"]
)
data class MDetail(
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("poster_path")
    val poster_path: String?,
    @field:SerializedName("release_date")
    val release_date: String,
    @field:SerializedName("movie_id")
    val movie_id: Int
)