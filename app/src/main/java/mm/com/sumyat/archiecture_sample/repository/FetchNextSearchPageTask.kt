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

package mm.com.sumyat.archiecture_sample.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mm.com.sumyat.archiecture_sample.api.*
import mm.com.sumyat.archiecture_sample.cache.db.SampleDb
import mm.com.sumyat.archiecture_sample.vo.Movie
import mm.com.sumyat.archiecture_sample.vo.Resource
import java.io.IOException

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
class FetchNextSearchPageTask constructor(
    private val query: Int,
    private val service: SampleService,
    private val db: SampleDb
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {
            _liveData.postValue(null)

        val newValue = try {
            val response = service.getPlayingMovie(query).execute()
            val apiResponse = ApiResponse.create(response)
            when (apiResponse) {
                is ApiSuccessResponse -> {
                    db.runInTransaction {
                        db.repoDao().insertRepos(apiResponse.body.results.map { Movie(it.id,it.title,it.poster_path,it.overview,it.release_date) })
                    }
                    Resource.success(apiResponse.body != null)
                }
                is ApiEmptyResponse -> {
                    Resource.success(false)
                }
                is ApiErrorResponse -> {
                    Resource.error(apiResponse.errorMessage, true)
                }
            }

        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        _liveData.postValue(newValue)
    }
}
