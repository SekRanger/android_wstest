package ws.wuah.websockettest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class FcmMsgService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        /*Log.d("TAGTAGTAG", "onMessageReceived");
        System.out.println("getMessageId:" + remoteMessage.getMessageId());
        System.out.println("getMessageType:" + remoteMessage.getMessageType());
        System.out.println("getData:" + remoteMessage.getData());*/
        this.handleEvent(remoteMessage.getData());
        /*RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            System.out.println("notification title:" + notification.getTitle());
            System.out.println("notification tag:" + notification.getTag());
            System.out.println("notification body:" + notification.getBody());
        }*/
    }

    private JSONObject getJSONObject(String json) {
        JSONObject result = null;
        try {
            result = new JSONObject(json);
        } catch (JSONException e) {
        }

        return result;
    }

    private void handleEvent(Map<String, String> data) {
        if (!data.containsKey("event")) {
            return;
        }

        String event = data.get("event");
        switch (event) {
            case "#invite":
                this.handleInvite(getJSONObject(data.get("data")));
                break;
        }
    }

    private int id = 0;
    private int newId() {
        return this.id++;
    }

    private void handleInvite(JSONObject data) {
        if (data == null) {
            return;
        }
        Bitmap largeIcon;
        try {
            URL url = new URL("https://scontent.xx.fbcdn.net/v/t1.0-1/p100x100/20139658_10154509820086755_2397347161659098225_n.jpg?_nc_eui2=v1%3AAeF13khxeNmqJabFhuddQUC7H_ObjXzzE0HRUuUeIRzS191ckucbktPhHfIuF3i_CVkYDFZVt7h_KWDqkeNvUYs7&oh=7ad6086d0702e294d0a834630c2dce3f&oe=5A066837");
            largeIcon = BitmapFactory.decodeStream((InputStream) url.getContent());
        } catch (Exception e) {
            largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLights(Color.BLUE, 100, 2000)
                        .setSmallIcon(R.drawable.ic_run)
                        .setLargeIcon(largeIcon)
                        .setContentTitle("Invitation")
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentText("\"" + data.optString("by") + "\" invites you to \"" + data.optString("to") + "\"")
                        .setVibrate(new long[]{100, 100, 100, 100, 100, 100})
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("\"" + data.optString("by") + "\" invites you to \"" + data.optString("to") + "\""))
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.cat)));

        Intent intent = new Intent(this, MainActivity.class);
        mBuilder.setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT));
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(this.newId(), mBuilder.build());
    }
}
