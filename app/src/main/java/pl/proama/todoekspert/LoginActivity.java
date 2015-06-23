package pl.proama.todoekspert;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.usernameEditText)
    EditText usernameEditText;
    @InjectView(R.id.passwordEditText)
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.loginButton)
    public void tryToLogin() {

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean hasErrors = false;

        if(TextUtils.isEmpty(username)) {

            usernameEditText.setError(getString(R.string.empty_field));
            hasErrors = true;

        }
        if(TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.empty_field));
            hasErrors = true;
        }

        if(!hasErrors) {
            login(username, password);
        }



    }

    private void login(String username, String password) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
