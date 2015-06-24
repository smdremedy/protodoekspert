package pl.proama.todoekspert;

import android.app.Application;

import pl.proama.todoekspert.di.DaggerTodoComponent;
import pl.proama.todoekspert.di.TodoComponent;
import pl.proama.todoekspert.di.TodoModule;
import retrofit.RestAdapter;
import timber.log.Timber;

public class App extends Application {


    private static TodoComponent todoComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        todoComponent = DaggerTodoComponent.builder()
                .todoModule(new TodoModule(getApplicationContext()))
                .build();

        Timber.plant(new Timber.DebugTree());

    }

    public static TodoComponent getTodoComponent() {
        return todoComponent;
    }

}
