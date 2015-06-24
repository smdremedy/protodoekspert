package pl.proama.todoekspert.di;

import javax.inject.Singleton;

import dagger.Component;
import pl.proama.todoekspert.AddTodoActivity;
import pl.proama.todoekspert.LoginActivity;
import pl.proama.todoekspert.TodoApi;
import pl.proama.todoekspert.TodoListActivity;

@Singleton
@Component(modules = {
        TodoModule.class
})
public interface TodoComponent {

    void inject(TodoListActivity todoListActivity);

    void inject(LoginActivity loginActivity);

    void inject(AddTodoActivity addTodoActivity);

    TodoApi getTodoApi();
}
