package pl.proama.todoekspert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.proama.todoekspert.db.TodoDao;
import retrofit.RetrofitError;
import timber.log.Timber;


public class TodoListActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 123;
    private AsyncTask<Void, Void, List<Todo>> asyncTask;

    @Inject
    LoginManager loginManager;
    @Inject
    TodoApi todoApi;
    @Inject
    TodoDao todoDao;

    @InjectView(R.id.todosListView)
    ListView todosListView;
    private TodoAdapter adapter;
    private SimpleCursorAdapter cursorAdapter;
    private String[] from = new String[]{TodoDao.C_CONTENT, TodoDao.C_DONE};
    private int[] to = new int[]{R.id.listDoneCheckBox, R.id.listDoneCheckBox};


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            refresh();

        }
    };

    private void refresh() {
        cursorAdapter.swapCursor(todoDao.query(loginManager.getUserId(), true));
    }

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
        ButterKnife.inject(this);

        adapter = new TodoAdapter(LayoutInflater.from(getApplicationContext()));

        cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.list_item, null, from, to, 0);
        cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int index) {

                if(index == cursor.getColumnIndex(TodoDao.C_DONE)) {

                    CheckBox checkBox = (CheckBox) view;
                    checkBox.setChecked(cursor.getInt(index) > 0);
                    return true;
                }

                return false;
            }
        });

        todosListView.setAdapter(cursorAdapter);

    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(RefreshIntentService.ACTION));
    }

    static class TodoAdapter extends BaseAdapter {

        private final LayoutInflater layoutInflater;
        private ArrayList<Todo> todos = new ArrayList<>();

        public TodoAdapter(LayoutInflater layoutInflater) {

            this.layoutInflater = layoutInflater;
        }

        @Override
        public int getCount() {
            return todos.size();
        }

        @Override
        public Todo getItem(int i) {
            return todos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return position % 2;
        }



        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            Timber.d("Item :" + i + " view:" + convertView);
            if(getItemViewType(i) == 0) {
                return getView0(i, convertView, viewGroup);
            } else {
                return getView1(i, convertView, viewGroup);
            }
        }

        private View getView1(int i, View convertView, ViewGroup viewGroup) {
            View inflatedView = convertView;
            if(inflatedView == null) {
                inflatedView = layoutInflater.inflate(R.layout.list_item, viewGroup, false);
            }

            ViewHolder viewHolder = (ViewHolder) inflatedView.getTag();
            if(viewHolder == null) {
                viewHolder = new ViewHolder();

                viewHolder.checkBox = (CheckBox) inflatedView.findViewById(R.id.listDoneCheckBox);
                viewHolder.textView = (TextView) inflatedView.findViewById(R.id.listContentTextView);
                inflatedView.setTag(viewHolder);
            }

            Todo todo = getItem(i);

            viewHolder.checkBox.setChecked(todo.isDone());
            viewHolder.textView.setText(todo.getContent());


            return inflatedView;
        }

        private View getView0(int i, View convertView, ViewGroup viewGroup) {
            View inflatedView = convertView;
            if(inflatedView == null) {
                inflatedView = layoutInflater.inflate(R.layout.list_item_1, viewGroup, false);
            }

            ViewHolder viewHolder = (ViewHolder) inflatedView.getTag();
            if(viewHolder == null) {
                viewHolder = new ViewHolder();

                viewHolder.checkBox = (CheckBox) inflatedView.findViewById(R.id.listDoneCheckBox);
                viewHolder.textView = (TextView) inflatedView.findViewById(R.id.listContentTextView);
                inflatedView.setTag(viewHolder);
            }

            Todo todo = getItem(i);

            viewHolder.checkBox.setChecked(todo.isDone());
            viewHolder.textView.setText(todo.getContent());


            return inflatedView;
        }

        public void addAll(List<Todo> todos) {
            this.todos.clear();
            this.todos.addAll(todos);

            if(this.todos.isEmpty()) {
                notifyDataSetInvalidated();
            } else {
                notifyDataSetChanged();
            }
        }
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView textView;

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


        Intent intent = new Intent(getApplicationContext(), RefreshIntentService.class);
        startService(intent);
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
