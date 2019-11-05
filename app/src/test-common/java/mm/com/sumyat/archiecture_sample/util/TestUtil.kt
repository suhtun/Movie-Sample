/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mm.com.sumyat.archiecture_sample.util

import mm.com.sumyat.archiecture_sample.api.MovieResult
import mm.com.sumyat.archiecture_sample.vo.Movie
import java.util.concurrent.ThreadLocalRandom


object TestUtil {

    fun createMovies(count: Int, title: String): List<MovieResult> {
        return (0 until count).map {
            createMovieResponse(title)
        }
    }

    fun movieResultToMovie(list: List<MovieResult>): List<Movie> {
        return list.map {
            Movie(
                it.id,
                it.title,
                it.poster_path,
                it.vote_average.toString(),
                it.overview,
                it.release_date
            )
        }
    }

    fun createMovie(title: String) = Movie(
        randomInt(), title, randomUuid(), randomUuid(), randomUuid(),
        randomUuid()
    )

    fun createMovieResponse(title: String) = MovieResult(
        43.929,
        7271,
        false,
        "/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg",
        randomInt(),
        false,
        "/8QXGNP0Vb4nsYKub59XpAhiUSQN.jpg",
        "en",
        "Blade Runner 2049",
        arrayListOf<Int>(),
        title,
        7.4,
        "Thirty years after the events of the first film, a new blade runner, LAPD Officer K, unearths a long-buried secret that has the potential to plunge what's left of society into chaos. K's discovery leads him on a quest to find Rick Deckard, a former LAPD blade runner who has been missing for 30 years.",
        "/gajva2L0rPYkEWjzgFlBXCAVBE5.jpg"
    )

    fun randomUuid(): String {
        return java.util.UUID.randomUUID().toString()
    }

    fun randomInt(): Int {
        return ThreadLocalRandom.current().nextInt(0, 1000 + 1)
    }
}
