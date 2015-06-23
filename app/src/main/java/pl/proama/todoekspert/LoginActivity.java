package pl.proama.todoekspert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
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
    private AsyncTask<String, Integer, UserResponse> asyncTask;

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
        asyncTask = new AsyncTask<String, Integer, UserResponse>() {
            @Override
            protected UserResponse doInBackground(String... strings) {
                String usernameArg = strings[0];
                String passwordArg = strings[1];

                RestAdapter.Builder builder = new RestAdapter.Builder();
                builder.setEndpoint("https://api.parse.com/1");
                builder.setLogLevel(RestAdapter.LogLevel.FULL);
                RestAdapter restAdapter = builder.build();

                TodoApi todoApi = restAdapter.create(TodoApi.class);


                try {

                    return todoApi.getLogin(usernameArg, passwordArg);
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
            protected void onPostExecute(UserResponse result) {
                super.onPostExecute(result);
                loginButton.setEnabled(true);
                asyncTask = null;
                if(result != null) {

                    App.LoginManager loginManager = ((App) getApplication()).getLoginManager();

                    loginManager.save(result.objectId, result.sessionToken);




                    Intent intent = new Intent(LoginActivity.this, TodoListActivity.class);
                    startActivity(intent);

                    finish();


                }
            }
        };
    }




}
