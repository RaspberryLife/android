package hsrw.de.net;


import protobuf.RblProto.*;

/**
 * Created by Peter Mösenthin.
 */
public interface NetworkListener{

    void onMessageReceived(RBLMessage message);
    void onConnected();
}
