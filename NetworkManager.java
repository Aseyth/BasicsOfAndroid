import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Aseith on 06/05/2017.
 */

public class NetworkManager {

    private static NetworkManager INSTANCE = null;
    private Retrofit mRetrofit;
    private NetworkServices.NetworkRequest networkServices;

    public static synchronized NetworkManager getInstance()
    {
        if (INSTANCE == null) {
            INSTANCE = new NetworkManager();
        }
        return INSTANCE;
    }

    private NetworkManager() {
        this.initRetrofit();
    }

    public interface ResultHandler<T> {
        void success(T result);
        void error(String error);
    }

    private void initRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Log options for debug (NONE, BASIC, HEADERS, BODY)

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(logging)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                        okhttp3.Request original = chain.request();

                        okhttp3.Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + "API_TOKEN")
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                });


        OkHttpClient client = okHttpClient.build();
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl("API_URL")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client);
        this.mRetrofit = retrofitBuilder.build();

        this.networkServices = mRetrofit.create(NetworkServices.NetworkRequest.class);
    }

    public void createUser(ContainerModel user, final ResultHandler<ContainerModel> callback) {
        Call<ContainerModel> call = this.networkServices.createUser("API_PATH", user);

        call.enqueue(new Callback<ContainerModel>() {
            @Override
            public void onResponse(Call<ContainerModel> call, Response<ContainerModel> response) {
                if (response.code() == 204) {
                    callback.success(response.body());
                } else {
                    callback.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<ContainerModel> call, Throwable t) {
                // network/server error
            }
        });
    }
}
