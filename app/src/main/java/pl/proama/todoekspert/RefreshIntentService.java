package pl.proama.todoekspert;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import pl.proama.todoekspert.db.TodoDao;
import pl.proama.todoekspert.db.TodoProvider;
import retrofit.RetrofitError;
import timber.log.Timber;

public class RefreshIntentService extends IntentService {

    public static final int WHAT_REFRESH = 134;

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager notificationManager;

    public static final String ACTION = "pl.proama.todoekspert.REFRESH_ACTION";
    @Inject
    TodoApi todoApi;
    @Inject
    LoginManager loginManager;
    @Inject
    TodoDao todoDao;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_REFRESH:
                    Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    public RefreshIntentService() {
        super(RefreshIntentService.class.getSimpleName());


    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getTodoComponent().inject(this);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Timber.d("refresh started");

        try {
            List<Todo> todos = todoApi.getTodos(loginManager.getToken()).results;
            for (Todo todo : todos) {
                Timber.d(todo.toString());
                ContentValues values = new ContentValues();
                values.put(TodoDao.C_ID, todo.getObjectId());
                values.put(TodoDao.C_CONTENT, todo.getContent());
                values.put(TodoDao.C_DONE, todo.isDone());
                values.put(TodoDao.C_CREATED_AT, todo.getCreatedAt().getTime());
                values.put(TodoDao.C_UPDATED_AT, todo.getUpdatedAt().getTime());
                values.put(TodoDao.C_USER_ID, todo.getUser().getObjectId());
                getContentResolver().insert(TodoProvider.CONTENT_URI, values);
                //todoDao.insertOrUpdate(todo);
            }
            sendTimelineNotification(todos.size());

        } catch (final RetrofitError error) {
                    ApiError apiError = (ApiError) error.getBodyAs(ApiError.class);
                    if(apiError == null) {
                        Timber.e(error.getMessage());
                    } else {
                        Timber.e(apiError.error);
                    }
        }

        handler.sendEmptyMessage(WHAT_REFRESH);




        Intent broadcast = new Intent(ACTION);

        sendBroadcast(broadcast);





    }

    private void sendTimelineNotification(int timelineUpdateCount) {

        if (notificationManager == null) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notificationManager.cancel(NOTIFICATION_ID);
        String notificationSummary = this.getString(
                R.string.notification_message, timelineUpdateCount, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());


        builder.setContentTitle(getText(R.string.notification_title));
        builder.setContentText(notificationSummary);

        builder.setSmallIcon(R.drawable.ic_launcher);

        Intent backIntent = new Intent(this, TodoListActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, backIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(contentIntent);
        builder.addAction(R.drawable.ic_action_new, "New", contentIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
