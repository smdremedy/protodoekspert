package pl.proama.todoekspert;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;


public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.usernameEditText)
    EditText usernameEditText;
    @InjectView(R.id.passwordEditText)
    EditText passwordEditText;
    @InjectView(R.id.loginButton)
    Button loginButton;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    private AsyncTask<String, Integer, Boolean> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(asyncTask != null) {
            asyncTask.cancel(false);
        }
    }

    @OnClick(R.id.loginButton)
    public void tryToLogin() {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean hasErrors = false;

        if (TextUtils.isEmpty(username)) {

            usernameEditText.setError(getString(R.string.empty_field));
            hasErrors = true;

        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.empty_field));
            hasErrors = true;
        }

        if (!hasErrors) {
            login(username, password);
        }


    }

    private void login(final String username, String password) {


        if (asyncTask == null) {
            prepareTask();
            asyncTask.execute(username, password);
        }


    }

    private void prepareTask() {
        asyncTask = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... strings) {
                String usernameArg = strings[0];
                String passwordArg = strings[1];


                for (int i = 0; i < 100; i++) {

                    if(isCancelled())
                        return false;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(i);

                }

                return "test".equals(usernameArg) && "test".equals(passwordArg);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressBar.setProgress(values[0]);
                String format = String.format("%s%%", values[0]);
                loginButton.setText(format);
                Timber.d(format);


            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loginButton.setEnabled(false);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                loginButton.setEnabled(true);
                asyncTask = null;
                if(result) {

                    Intent intent = new Intent(LoginActivity.this, TodoListActivity.class);
                    startActivity(intent);

                    finish();


                }
            }
        };
    }


}
