import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Aseith on 06/05/2017.
 */

public class NetworkServices {
    public interface NetworkRequest {

        @POST("{path}/user")
        Call<ContainerModel> createUser(
                @Path("path") String myPath,
                @Body ContainerModel user
        );
        
        @PATCH("{path}/user/{id}")
        Call<ContainerModel> updateUser(
                @Path("path") String myPath,
                @Path("id") int id,
                @Body ContainerModel user
        );

        @PUT("{path}/user/{id}")
        Call<ContainerModel> updateUser(
                @Path("path") String myPath,
                @Path("id") int userId,
                @Body ContainerModel user
        );

        @GET("{path}/user/{id}")
        Call<ContainerModel> getUser(
                @Path("path") String myPath,
                @Path("id") String id
        );

        @GET("{path}/user/search")
        Call<ContainerModel> searchUser(
          @Path("path") String myPath,
          @Query("firstName") String firstName,
          @Query("lastName") String lastName,
          @Query("age") int age
        );
    }
}
