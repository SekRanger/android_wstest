package ws.wuah.websockettest;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

public class Scc {
    private SccListener sccListener;
    private WebSocket webSocket;
    private Hashtable<Integer, SccMessageHandler> callers;
    private Hashtable<String, SccChannel> channels;
    private int cid = 1;
    private String id;
    private boolean isAuthenticated = false;
    private int pingTimeout = 200000;

    public Scc(WebSocket webSocket) {
        this.callers = new Hashtable<Integer, SccMessageHandler>();
        this.channels = new Hashtable<String, SccChannel>();
        //this.setSccListener(sccListener);
        this.webSocket = webSocket;
        this.init();
    }

    private int newCid() {
        return cid++;
    }

    public void disconnect() {
        send("{\"event\":\"#unsubscribe\",\"data\":\"#publisher\"}");
        send("{\"event\":\"#disconnect\",\"data\":{\"code\":1000}}");
        this.webSocket.close();
    }

    public boolean isOpen() {
        return this.webSocket.isOpen();
    }

    private int addMessageHandler(final SccMessageHandler handler) {
        int newCid = newCid();
        this.callers.put(newCid, handler);
        return newCid;
    }

    private void handleMessage(int rid, JSONObject data) {
        if (this.callers.containsKey(rid)) {
            this.callers.get(rid).handle(data);
        }
    }

    private void send(String message) {
        this.webSocket.send(message);
    }

    private void handshake() {
        int cid = addMessageHandler(new SccMessageHandler() {
            @Override
            public void handle(String message) {
                //System.out.println(message);
            }

            @Override
            public void handle(JSONObject message) {
                try {
                    JSONObject data = message.getJSONObject("data");
                    Scc.this.id = data.getString("id");
                    Scc.this.isAuthenticated = data.getBoolean("isAuthenticated");
                    Scc.this.pingTimeout = data.getInt("pingTimeout");
                } catch (JSONException e) {
                }
            }
        });
        send("{\"event\":\"#handshake\",\"data\":{\"authToken\":null},\"cid\":" + cid + "}");
    }

    private class SubscribeMessageHandler implements SccMessageHandler {
        private String channelName;
        private int cid;
        private SccSubscribing sccSubscribing;

        public SubscribeMessageHandler(String channelName, SccSubscribing sccSubscribing) {
            this.channelName = channelName;
            this.sccSubscribing = sccSubscribing;
        }

        public SubscribeMessageHandler setCid(int cid) {
            this.cid = cid;
            return this;
        }

        @Override
        public void handle(String message) {

        }

        @Override
        public void handle(JSONObject message) {
            SccChannel channel = null;
            if (message.has("rid")) {
                int rid = -1;
                try {
                    rid = message.getInt("rid");
                } catch (JSONException e) {
                }

                if (rid == this.cid) {
                    channel = new SccChannel();
                    Scc.this.channels.put(this.channelName, channel);
                }
            }
            this.sccSubscribing.onSubscribed(channel);
        }
    }

    public void subscribe(final String channelName, JSONObject options, final SccSubscribing sccSubscribing) {
        if (!this.channels.containsKey(channelName)) {
            SubscribeMessageHandler handler = new SubscribeMessageHandler(channelName, sccSubscribing);
            int cid = addMessageHandler(handler);
            handler.setCid(cid);
            send("{\"event\":\"#subscribe\",\"data\":{\"channel\":\"" + channelName + "\", \"data\":{\"uid\": \"5knxfOzcKDRsJfU6x1S8EKDLBra2\"}},\"cid\":" + cid + "}");
        }
    }

    private void init() {
        this.webSocket.setStringCallback(new WebSocket.StringCallback() {
            @Override
            public void onStringAvailable(String s) {
                //System.out.println(s);
                if (s.equals("#1")) {
                    send("#2");
                } else {
                    try {
                        JSONObject message = new JSONObject(s);
                        if (message.has("rid")) {
                            int rid = message.getInt("rid");
                            Scc.this.handleMessage(rid, message);
                        } else if (message.has("event")) {
                            String event = message.getString("event");
                            if (event.equals("#publish")) {
                                JSONObject data = message.getJSONObject("data");
                                String channelName = data.getString("channel");
                                if (Scc.this.channels.containsKey(channelName)) {
                                    Scc.this.channels.get(channelName).onMessage(data);
                                }
                            }
                        }
                    /*JSONObject eventData = data.getJSONObject("data");
                    JSONObject channelData = eventData.getJSONObject("data");
                    String channelDataEvent = channelData.getString("event");*/
                    } catch (JSONException e) {
                    }
                }
                //Scc.this.sccListener.onMessage(s);
            }
        });

        this.webSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                //Scc.this.sccListener.onClosed(ex);
            }
        });

        this.webSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                //Scc.this.sccListener.onEnd(ex);
            }
        });

        handshake();
    }

    public void setSccListener(SccListener sccListener) {
        this.sccListener = sccListener;
    }
}