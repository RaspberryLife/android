package protobuf;

/**
 * Created by Peter Mösenthin.
 */
public interface NetworkListener{

    void onMessageReceived(RBHproto.RBHMessage message);
    void onConnected();
}
