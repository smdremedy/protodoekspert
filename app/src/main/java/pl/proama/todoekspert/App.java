package pl.proama.todoekspert;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import retrofit.RestAdapter;
import timber.log.Timber;

public class App extends Application {


    private TodoApi todoApi;

    public TodoApi getTodoApi() {
        return todoApi;
    }

    static class LoginManager {

        public static final String TOKEN_PREFS_KEY = "token";
        public static final String USER_ID_PREFS_KEY = "userId";

        private SharedPreferences sharedPreferences;

        private String token;
        private String userId;

        public LoginManager(Context context) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            token = sharedPreferences.getString(TOKEN_PREFS_KEY, "");
            userId = sharedPreferences.getString(USER_ID_PREFS_KEY, "");

        }

        public boolean needsLogin() {
            return TextUtils.isEmpty(token) || TextUtils.isEmpty(userId);
        }


        public void logout() {

            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.clear();
            edit.apply();
            token = null;
            userId = null;


        }

        public void save(String objectId, String sessionToken) {


            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(TOKEN_PREFS_KEY, sessionToken);
            editor.putString(USER_ID_PREFS_KEY, objectId);

            if(BuildConfig.VERSION_CODE >= Build.VERSION_CODES.GINGERBREAD) {
                editor.apply();
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        editor.commit();
                    }
                }.start();
            }

            token = sessionToken;
            userId = objectId;
        }

        public String getToken() {
            return token;
        }
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    private LoginManager loginManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        loginManager = new LoginManager(this);

        createApi();


    }

    private void createApi() {
        RestAdapter.Builder builder = new RestAdapter.Builder();
        builder.setEndpoint("https://api.parse.com/1");
        builder.setLogLevel(RestAdapter.LogLevel.FULL);
        RestAdapter restAdapter = builder.build();

        todoApi = restAdapter.create(TodoApi.class);

    }


}
