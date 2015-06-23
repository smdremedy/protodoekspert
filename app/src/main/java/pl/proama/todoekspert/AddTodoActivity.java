package pl.proama.todoekspert;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class AddTodoActivity extends ActionBarActivity {


    public static final String TODO = "todo";
    @InjectView(R.id.saveButton)
    Button saveButton;
    @InjectView(R.id.contentEditText)
    EditText contentEditText;
    @InjectView(R.id.doneCheckBox)
    CheckBox doneCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        ButterKnife.inject(this);

    }


    @OnClick(R.id.saveButton)
    public void save() {

        String content = contentEditText.getText().toString();
        boolean isDone = doneCheckBox.isChecked();

        if(TextUtils.isEmpty(content)) {
            contentEditText.setError(getString(R.string.empty_field));
        } else {

            Intent intent = new Intent();
            Todo value = new Todo(content, isDone);
            intent.putExtra(TODO, value);
            setResult(RESULT_OK, intent);
            finish();
        }


    }
}
