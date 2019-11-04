package mm.com.sumyat.archiecture_sample.vo

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.google.gson.annotations.SerializedName

@Entity(
    primaryKeys = ["movie_id"]
)
data class Next(
    @field:SerializedName("movie_id")
    val movie_id: Int,
    @field:SerializedName("next")
    val next: Int
)