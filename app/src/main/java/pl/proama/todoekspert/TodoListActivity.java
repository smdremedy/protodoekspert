package pl.proama.todoekspert;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import timber.log.Timber;


public class TodoListActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 123;
    private AsyncTask<Void, Void, List<Todo>> asyncTask;

    @Inject
    LoginManager loginManager;
    @Inject
    TodoApi todoApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.getTodoComponent().inject(this);


        if(loginManager.needsLogin()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        setContentView(R.layout.activity_todo_list);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_new:
                showAddNew();
                return true;

            case R.id.action_logout:
                showLogoutDialog();
                return true;
            case R.id.action_refresh:

                performRefresh();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void performRefresh() {


        if(asyncTask == null) {
            asyncTask = new AsyncTask<Void, Void, List<Todo>>() {
                @Override
                protected List<Todo> doInBackground(Void... voids) {


                    try {

                        return todoApi.getTodos(loginManager.getToken()).results;
                    } catch (final RetrofitError error) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ApiError apiError = (ApiError) error.getBodyAs(ApiError.class);

                                Toast.makeText(getApplicationContext(), apiError.error, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(List<Todo> todos) {
                    super.onPostExecute(todos);
                    Toast.makeText(getApplicationContext(), "Refeshed", Toast.LENGTH_SHORT).show();
                    for (Todo todo : todos) {
                        Timber.d(todo.toString());
                    }

                }
            };
            asyncTask.execute();
        }
    }


    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.are_you_sure));
        builder.setMessage(getString(R.string.want_to_quit));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loginManager.logout();
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showAddNew() {
        Intent intent = new Intent(getApplicationContext(), AddTodoActivity.class);
        intent.putExtra("key", "value");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {

            if(resultCode == RESULT_OK) {
                Todo todo = (Todo) data.getSerializableExtra(AddTodoActivity.TODO);

                Toast.makeText(getApplicationContext(), "Result code : OK, todo:" + todo, Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "Result code:" + resultCode, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
