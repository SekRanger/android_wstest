package ws.wuah.websockettest;

import org.json.JSONObject;

public class SccChannel {
    private SccMessageHandler sccMessageHandler = null;

    public SccChannel(SccMessageHandler sccMessageHandler) {
        this.setSccMessageHandler(sccMessageHandler);
    }

    public SccChannel() {
    }

    public void setSccMessageHandler(SccMessageHandler sccMessageHandler) {
        this.sccMessageHandler = sccMessageHandler;
    }

    public void onMessage(JSONObject message) {
        if (this.sccMessageHandler != null) {
            this.sccMessageHandler.handle(message);
        }
    }
}
