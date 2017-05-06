# Basics Of Android

A little memo of simple method & usage

## Retrofit 2.1

Type-safe HTTP client for Android and Java by Square, Inc.

### Prerequisites

Add thoses line to your build.gradle (App)

```
compile 'com.squareup.retrofit2:retrofit:2.1.0'
compile 'com.squareup.retrofit2:converter-gson:2.1.0'
```

You can also add a Http logger to help you in your debug

```
compile 'com.squareup.okhttp3:logging-interceptor:3.3.1'
```

### Usage

First create your network manager which will be a singleton

```java
public class NetworkManager {

    private static NetworkManager INSTANCE = null;

    public static synchronized NetworkManager getInstance()
    {
        if (INSTANCE == null) {
            INSTANCE = new NetworkManager();
        }
        return INSTANCE;
    }
}
```

Then add a method to initialize your Retrofit instance

```java
private NetworkManager() {
        this.initRetrofit();
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
    }
```

Implement a Retrofit interface which will contain your Http request

```java
public class NetworkServices {
    public interface NetworkRequest {

        @POST("{path}/user")
        Call<ContainerModel> createUser(
                @Path("path") String myPath,
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
                @Path("apiVersion") String myPath,
                @Path("id") String id
        );

        @PATCH("{path}/user/{id}")
        Call<ContainerModel> updateProfile(
                @Path("apiVersion") String myPath,
                @Path("id") int id,
                @Body ContainerModel user
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
```

Implement an interface to notify the state of your request top your view

```java
public interface ResultHandler<T> {
        void success(T result);
        void error(String error);
    }
```

Now that both your interfaces are implemented you can implement your calls methods

```java
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
```
