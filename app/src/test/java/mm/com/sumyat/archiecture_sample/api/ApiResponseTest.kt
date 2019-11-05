package mm.com.sumyat.archiecture_sample.api

import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class ApiResponseTest {

    @Test
    fun expection(){
        val exception = Exception("gg")
        val (errorMessage) = ApiResponse.create<String>(exception)
        MatcherAssert.assertThat<String>(errorMessage, CoreMatchers.`is`("gg"))
    }

    @Test
    fun success() {
        val apiResponse: ApiSuccessResponse<String> = ApiResponse
            .create<String>(Response.success("gg")) as ApiSuccessResponse<String>
        MatcherAssert.assertThat<String>(apiResponse.body, CoreMatchers.`is`("gg"))
    }

    @Test
    fun error() {
        val errorResponse = Response.error<String>(
            400,
            ResponseBody.create(MediaType.parse("application/txt"), "blah")
        )
        val (errorMessage) = ApiResponse.create<String>(errorResponse) as ApiErrorResponse<String>
        MatcherAssert.assertThat<String>(errorMessage, CoreMatchers.`is`("blah"))
    }
}