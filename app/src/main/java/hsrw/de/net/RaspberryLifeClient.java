package hsrw.de.net;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import protobuf.RblProto.*;

/**
 * Created by Peter Mösenthin.
 */
public class RaspberryLifeClient {

    public static final String VERSION_ID = "v0.01";
    public static final int DEFAULT_PORT = 6666;
    public static final String CLIENT_ID = "android_testclient";


    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket serverSocket = null;

    private NetworkListener networkListener;
    
    public String logClientTag = "ClientSocket";
    
    public RaspberryLifeClient(String serverIp){
        setUpClient(serverIp);
    }

    private void setUpClient(final String ip){
        Thread setUpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(logClientTag, "RaspberryHomeClient " + VERSION_ID);
                try {
                    Log.d(logClientTag, "Connecting to RaspberryHomeServer on port "
                            + DEFAULT_PORT);
                    serverSocket = new Socket(ip,DEFAULT_PORT);
                    inputStream = new ObjectInputStream(serverSocket.getInputStream());
                    outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
                    if(networkListener != null){
                        networkListener.onConnected();
                    }
                    read();
                } catch (IOException e) {
                    Log.d(logClientTag, "Could not set up client");
                    e.printStackTrace();
                }
            }
        });
    setUpThread.start();
    }


    public void write(final Object message){
        Thread writethread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(outputStream != null){
                        outputStream.writeObject(message);
                    }
                } catch (IOException e) {
                    Log.d(logClientTag,
                            "Client could not write message");
                    close();
                    //e.printStackTrace();
                }
            }
        });
        writethread.start();
    }

    private void read(){
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
        RBLMessage message;
        boolean read = true;
        while (read) {
            try {
                if ((message = (RBLMessage) inputStream.readObject()) != null) {
                    String messageID = message.getId();
                    Log.d(logClientTag,
                            "Client received message from " + messageID + ": ");
                    if(networkListener != null){
                        networkListener.onMessageReceived(message);
                    }
                    }
            } catch (ClassNotFoundException e) {
                Log.d(logClientTag,
                        "Client could not cast message");
                e.printStackTrace();
            }catch(IOException e){
                Log.d(logClientTag,
                        "Client could not read message");
                read = false;
                //close();
                //e.printStackTrace();
            }
        }
            }
        });
        readThread.start();
    }

    public void close(){
        try {
            inputStream.close();
            outputStream.close();
            serverSocket.close();
            Log.d(logClientTag, 
                    "Client closed connection");
        } catch (IOException e) {
        	Log.d(logClientTag, 
                    "Client could not close connection");
            //e.printStackTrace();
        }
    }

    public NetworkListener getNetworkListener() {
        return networkListener;
    }

    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

}
