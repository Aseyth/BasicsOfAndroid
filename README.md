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
                @Path("path") String myPath,
                @Path("id") String id
        );

        @PATCH("{path}/user/{id}")
        Call<ContainerModel> updateProfile(
                @Path("path") String myPath,
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

Implement an interface to notify the state of your request to your view

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

### Custom Gson Serializer

First create your custom serializer

```java
public class CustomSerializer implements JsonSerializer<ContainerModel> {

    @Override
    public JsonElement serialize(final ContainerModel containerModel, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        if (containerModel.getFirstName() != null) {
            jsonObject.addProperty("firstName", containerModel.getFirstName());
        }
        if (containerModel.getLastName() != null) {
            jsonObject.addProperty("lastName", containerModel.getLastName());
        }
        if (containerModel.getAge() != null) {
            jsonObject.addProperty("age", containerModel.getAge());
        }

        if (containerModel.getChildObject() != null) {
            final JsonElement jsonChildObject = context.serialize(containerModel.getChildObject());
            jsonObject.add("childObject", jsonChildObject);
        }

        return jsonObject;
    }
}
```

Your custom serializer can contain an object that can also be customized ChildObject in this example

```java
public class ChildObjectSerializer implements JsonSerializer<ChildObject> {

    @Override
    public JsonElement serialize(ChildObject childObject, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("address", childObject.getAddress());
        jsonObject.addProperty("phoneNumber", childObject.getPhoneNumber());

        return jsonObject;
    }
}
```

Then you need to register your custom serialize

```java
Gson customSerializer = new GsonBuilder()
                .registerTypeAdapter(ContainerModel.class, new CustomSerializer())
                .registerTypeAdapter(ChildObject.class, new ChildObjectSerializer())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
                
OkHttpClient client = okHttpClient.build();
Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl("API_URL")
                .addConverterFactory(GsonConverterFactory.create(customSerializer))
                .client(client);
Retrofit mRetrofit = retrofitBuilder.build();
```

## Recycler View

Here is an example of a fragment implementing a simple Recycler View

```java
public class RecyclerViewFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerViewAdapter;
    List<Data> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerViewAdapter = new RecyclerViewAdapter(mList, R.layout.item_template, getContext());
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
```

```java
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewAdapterViewHolder> {

    List<Data> mDataList;
    int mLayout;

    public RecyclerViewAdapter(List<Data> dataList, int layout) {
        this.mDataList = dataList;
        this.mLayout = layout;
    }

    @Override
    public RecyclerViewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false);
        return new RecyclerViewAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.RecyclerViewAdapterViewHolder holder, int position) {
        holder.mItem = mDataList.get(position);
        holder.mName.setText(holder.mItem.getName());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class RecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder {

        private Data mItem;
        private TextView mName;

        RecyclerViewAdapterViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.name_template);
        }
    }
}
```


## Fragments & tips


### Fragment transactions

First you need to identify the layout in your activity which will contain your fragment (R.id.fragment in this example), then create your new Fragment and replace or add the new fragment to your existing activity
Little tip: When you use .add, make your new Fragment clickable to avoid clicking widget of your previous Fragment. When a new Fragment is added the previous Fragment is still there and the new one is above the previous Fragment.

```java
NewFragment myNewFragment = new NewFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_slide_in, R.anim.fragment_slide_out, R.anim.fragment_pop_in, R.anim.fragment_pop_out)
                .replace(R.id.fragment, myNewFragment, NewFragment.class.getSimpleName())
                .addToBackStack(NewFragment.class.getSimpleName())
                .commit();
```

