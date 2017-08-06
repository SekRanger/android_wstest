package ws.wuah.websockettest;

import org.json.JSONObject;

public interface SccMessageHandler {
    void handle(String message);
    void handle(JSONObject jsonObject);
}