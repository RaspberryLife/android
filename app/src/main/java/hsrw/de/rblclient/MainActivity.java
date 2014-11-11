package hsrw.de.rblclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import hsrw.de.net.NetworkListener;
import hsrw.de.net.RaspberryLifeClient;
import hsrw.de.protobuf.ProtoFactory;
import protobuf.RblProto.*;


public class MainActivity extends ActionBarActivity {

    private EditText ipAddressInput;
    private Button connectToServerButton;
    private Button authButton;
    private Button getDataSetButton;
    private RaspberryLifeClient client;
    private TextView outputTextview;
    private ScrollView outputScrollView;
    private EditText plainTextInput;
    private Button sendButton;

    private static int outCount = 0;

    public String logClientTag = "ProtobufTest";

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protobuftest);
        handler = new Handler();
        outCount = 0;

        ipAddressInput = (EditText) findViewById(R.id.editText_serverIP);
        ipAddressInput.setText(getServerIP());
        connectToServerButton = (Button) findViewById(R.id.button_ConnectToServer);
        connectToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToServer();
            }
        });
        outputTextview = (TextView) findViewById(R.id.textView_Output);
        getDataSetButton = (Button) findViewById(R.id.button_GetDataSet);
        getDataSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataSet();
            }
        });
        outputScrollView = (ScrollView) findViewById(R.id.scrollView);
        plainTextInput = (EditText) findViewById(R.id.editTextModulId);
        sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPlainText();
            }
        });
    }


    private void connectToServer(){
        if(ipAddressInput.getText().toString().isEmpty()){
            addToOutput("Keine IP adresse festgelegt");
        } else {
            addToOutput("Verbinde mit IP " + ipAddressInput.getText().toString());
            setServerIp(ipAddressInput.getText().toString());
            client = new RaspberryLifeClient(
                    ipAddressInput.getText().toString());
            client.setNetworkListener(new NetworkListener() {
                @Override
                public void onMessageReceived(RBLMessage message) {
                    handleMessage(message);
                }

                public void onConnected() {
                   authenticate();
                }
            });
        }
    }

    /**
     * Writes the server ip to the shared preferences
     * @param ip
     */
    private void setServerIp(String ip){
        SharedPreferences sharedPreferences =
                getApplicationContext().getSharedPreferences("SERVER_CONNECTION", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pref_key_server_ip", ip);
        editor.commit();
    }

    /**
     * Loads the server ip from shared preferences. the ip is set in the ProtoBuf Activity
     */
    private String getServerIP(){
        SharedPreferences sharedPreferences =
                getApplicationContext().getSharedPreferences("SERVER_CONNECTION", Context.MODE_PRIVATE);
        String ip = sharedPreferences.getString("pref_key_server_ip","");
        if(ip.equals("")){
            Toast.makeText(this,
                    "No IP set. Set the server ip in ProtobufTest",
                    Toast.LENGTH_SHORT)
                    .show();
        }
        return ip;
    }


    private void sendPlainText() {
        String text = plainTextInput.getText().toString();
        addToOutput("Sende: " + text);
        RBLMessage m  =
                ProtoFactory.buildPlainTextMessage(RaspberryLifeClient.CLIENT_ID, text);
        client.write(m);
    }

    private void authenticate() {
        addToOutput("Sende Authentifizierung");
        RBLMessage m = ProtoFactory.buildAuthRequestMessage(
                RaspberryLifeClient.CLIENT_ID, "abc12345");
        client.write(m);
    }

    private void getDataSet(){
        addToOutput("Sende GetDataSet Message");
        RBLMessage m = ProtoFactory.buildGetDataSetMessage(
                RaspberryLifeClient.CLIENT_ID,
                "livingroom_sensormodule",
                "temp",
                100,
                "geht noch nicht",
                "geht noch nicht"
        );
        client.write(m);
    }

    private void handleMessage(RBLMessage message){
        if(message.getMType() == RBLMessage.MessageType.PLAIN_TEXT){
            String text = message.getPlainText().getText();
            Log.d(logClientTag, text);
        }else if(message.getMType() == RBLMessage.MessageType.AUTH_DENIED){
            String denyMessage = message.getPlainText().getText();
            Log.d(logClientTag, "Authentication denied. Reason: " + denyMessage);
            addToOutput("Authentication denied. Reason: " + denyMessage);
        } else if(message.getMType() == RBLMessage.MessageType.AUTH_ACCEPT){
            String acceptMessage = message.getPlainText().getText();
            Log.d(logClientTag, "Authentication successful. " +  acceptMessage);
            addToOutput("Authentication successful. " +  acceptMessage);
        } else if(message.getMType() == RBLMessage.MessageType.DATA_SET){
            RBLMessage.DataSet dataSet = message.getDataSet();
            for(RBLMessage.Data d : dataSet.getDataList()){
                if(d.hasFloatData()){
                    Log.d(logClientTag, "temp: " + d.getFloatData());
                    addToOutput("temp: " + d.getFloatData());
                }
            }
        }

    }

    private void addToOutput(final String text){
        handler.post(new Runnable() {
            @Override
            public void run() {
                outputTextview.append("[" + outCount + "] " +  text + "\n");
                outputScrollView.fullScroll(View.FOCUS_DOWN);
                outCount++;
            }
        });
    }
}
