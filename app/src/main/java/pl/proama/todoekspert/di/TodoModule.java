package pl.proama.todoekspert.di;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.proama.todoekspert.TodoApi;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module
public class TodoModule {

    private final Context context;

    public TodoModule(Context context) {

        this.context = context;
    }


    @Provides
    public Context provideContext() {
        return context;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Url {

    }

    @Url
    @Provides
    public String provideUrl() {
        return "https://api.parse.com/1";
    }

    @Singleton
    @Provides
    public RestAdapter provideRestAdapter(@Url String url) {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();

        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint(url);
        builder.setConverter(new GsonConverter(gson));
        builder.setLogLevel(RestAdapter.LogLevel.FULL);
        return builder.build();

    }

    @Singleton
    @Provides
    public TodoApi provideTodoApi(RestAdapter restAdapter) {


        return restAdapter.create(TodoApi.class);
    }
}
