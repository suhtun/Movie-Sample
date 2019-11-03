package mm.com.sumyat.archiecture_sample.api

import com.google.gson.annotations.SerializedName

data class PlayingMoviewsResponse(

    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("page")
    val page: Int = 0
)

data class Result(
    @field:SerializedName("popularity")
    val popularity: Double,
    @field:SerializedName("vote_count")
    val vote_count: Int,
    @field:SerializedName("video")
    val video: Boolean,
    @field:SerializedName("poster_path")
    val poster_path: String?,
    @field:SerializedName("id")
    val id: Int,
    @field:SerializedName("adult")
    val adult: Boolean,
    @field:SerializedName("backdrop_path")
    val backdrop_path: String,
    @field:SerializedName("original_language")
    val original_language: String,
    @field:SerializedName("original_title")
    val original_title: String,
    @field:SerializedName("genre_ids")
    val genre_ids: List<Int>,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("vote_average")
    val vote_average: Double,
    @field:SerializedName("overview")
    val overview: String,
    @field:SerializedName("release_date")
    val release_date: String
)