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

import mm.com.sumyat.archiecture_sample.vo.Movie
import java.util.concurrent.ThreadLocalRandom


object TestUtil {

    fun createMovies(count: Int, title: String): List<Movie> {
        return (0 until count).map {
            createMovie(title)
        }
    }

    fun createMovie(title: String) = Movie(
        randomInt(), title, randomUuid(), randomUuid(), randomUuid(),
        randomUuid()
    )

    fun randomUuid(): String {
        return java.util.UUID.randomUUID().toString()
    }

    fun randomInt(): Int {
        return ThreadLocalRandom.current().nextInt(0, 1000 + 1)
    }
}
