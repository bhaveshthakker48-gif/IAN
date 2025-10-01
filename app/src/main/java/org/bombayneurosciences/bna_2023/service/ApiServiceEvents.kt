import org.bombayneurosciences.bna_2023.Model.sesssions.Data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceEvents {
    @GET("getsessions")
    fun getSessions(@Query("eid") eventId: Int): Call<List<Data>>


}
