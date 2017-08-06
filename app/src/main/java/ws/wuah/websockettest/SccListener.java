package ws.wuah.websockettest;

public interface SccListener {
    void onMessage(String message);
    void onClosed(Exception ex);
    void onEnd(Exception ex);
}