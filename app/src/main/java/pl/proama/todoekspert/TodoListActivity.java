package pl.proama.todoekspert;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;


public class TodoListActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 123;
    private AsyncTask<Void, Void, List<Todo>> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(List<Todo> todos) {
                    super.onPostExecute(todos);
                    Toast.makeText(getApplicationContext(), "Refeshed", Toast.LENGTH_SHORT).show();
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
