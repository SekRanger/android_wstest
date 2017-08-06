package ws.wuah.websockettest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends Activity {
    private Scc ws;
    private void log(String s) {
        System.out.println(s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void onConnected(WebSocket webSocket) {
        Scc scc = this.ws = new Scc(webSocket);
        scc.subscribe("mystack_5knxfOzcKDRsJfU6x1S8EKDLBra2", null, new SccSubscribing() {
            @Override
            public void onSubscribed(SccChannel sccChannel) {
                sccChannel.setSccMessageHandler(new SccMessageHandler() {
                    @Override
                    public void handle(String message) {

                    }

                    @Override
                    public void handle(JSONObject message) {
                        Date now = new Date();
                        System.out.println(now.getTime());
                        System.out.println("channel msg:" + message.toString());
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final TextView textView = (TextView) findViewById(R.id.textView);

        if (this.ws == null || !this.ws.isOpen()) {
            Date now = new Date();
            System.out.println(now.getTime());
            AsyncHttpClient.getDefaultInstance().websocket("ws://192.168.1.20:8000/socketcluster/", "", new AsyncHttpClient.WebSocketConnectCallback() {
                @Override
                public void onCompleted(Exception ex, final WebSocket webSocket) {
                    if (ex != null) {
                        ex.printStackTrace();
                        return;
                    }
                    MainActivity.this.onConnected(webSocket);

                    /*webSocket.send("{\"event\":\"#handshake\",\"data\":{\"authToken\":null},\"cid\":1}");
                    //webSocket.send("{\"event\":\"#subscribe\",\"data\":{\"channel\":\"mystack_5knxfOzcKDRsJfU6x1S8EKDLBra2\", \"data\":{\"uid\": \"5knxfOzcKDRsJfU6x1S8EKDLBra2\"}},\"cid\":2}");
                    log("Connected");
                    MainActivity.this.ws = webSocket;
                    webSocket.setStringCallback(new WebSocket.StringCallback() {
                        public void onStringAvailable(final String s) {
                            //log(s);
                            if (s.equals("#1")) {
                                webSocket.send("#2");
                            } else {
                                try {
                                    JSONObject data = new JSONObject(s);
                                    JSONObject eventData = data.getJSONObject("data");
                                    JSONObject channelData = eventData.getJSONObject("data");
                                    String channelDataEvent = channelData.getString("event");
                                    log(channelDataEvent);
                                } catch (JSONException e) {
                                    //e.printStackTrace();
                                }
                                textView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(s);
                                    }
                                });
                            }
                        }
                    });
                    webSocket.setDataCallback(new DataCallback() {
                        public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                            System.out.println("I got some bytes!");
                            // note that this data has been read
                            byteBufferList.recycle();
                        }
                    });
                    webSocket.setClosedCallback(new CompletedCallback() {
                        @Override
                        public void onCompleted(Exception ex) {
                            System.out.println("Closed!");
                        }
                    });*/
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.ws != null && this.ws.isOpen()) {
           this.ws.disconnect();
        }
    }
}
