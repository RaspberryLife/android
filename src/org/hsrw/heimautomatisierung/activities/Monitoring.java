package org.hsrw.heimautomatisierung.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.hsrw.heimautomatisierung.R;
import org.hsrw.heimautomatisierung.networking.RaspberryHomeClient;
import protobuf.NetworkListener;
import protobuf.ProtoFactory;
import protobuf.RBHproto;

public class Monitoring extends Activity {
	
	private GraphicalView mChart;

    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

    private XYSeries mCurrentSeries;

    private XYSeriesRenderer mCurrentRenderer;

    private RaspberryHomeClient client;
    public String logClientTag = "Monitoring";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up notitle
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.monitoring);
        connectToServer();
    }

    protected void onResume() {
        super.onResume();

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.chart);
        layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        //layout.setBackground(new ColorDrawable(Color.parseColor("#FFFFFF")));
        if (mChart == null) {
            initChart();
            addSampleData();
            mChart = ChartFactory.getLineChartView(this, mDataset, mRenderer);
            mChart.setBackground(new ColorDrawable(Color.parseColor("#FFFFFF")));
            layout.addView(mChart);
        } else {
            mChart.repaint();
        }
    }

    private void initChart() {
        mCurrentSeries = new XYSeries("Monitoring: Temperature");
        mDataset.addSeries(mCurrentSeries);
        mCurrentRenderer = new XYSeriesRenderer();
        
        // setup renderer for graph
        mCurrentRenderer.setColor(Color.parseColor("#33B5E5"));
        mCurrentRenderer.setShowLegendItem(false);
        mCurrentRenderer.setLineWidth(10f);
        
        // setup renderer for axis and other surroundings
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.WHITE);
        mRenderer.setGridColor(Color.WHITE);
        mRenderer.setMarginsColor(Color.WHITE);
        mRenderer.setChartTitle("Monitoring: Temperature");
        mRenderer.setChartTitleTextSize(30f);
        mRenderer.setLabelsTextSize(30f);
        mRenderer.isAntialiasing();
        mRenderer.addSeriesRenderer(mCurrentRenderer);
    }

    private void addSampleData() {
    	mCurrentSeries.add(0, 20);
        mCurrentSeries.add(1, 20);
        mCurrentSeries.add(2, 19);
        mCurrentSeries.add(3, 21);
        mCurrentSeries.add(4, 21);
        mCurrentSeries.add(5, 21);
        mCurrentSeries.add(6, 21);
        mCurrentSeries.add(7, 21);
        mCurrentSeries.add(8, 21);
        mCurrentSeries.add(9, 22);
        mCurrentSeries.add(10, 21);
        mCurrentSeries.add(11, 21);
        mCurrentSeries.add(12, 20);
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
	


    private void connectToServer(){
            client = new RaspberryHomeClient(getServerIP());
            //client = new RaspberryHomeClient("10.10.10.10");
            client.setNetworkListener(new NetworkListener() {
                @Override
                public void onMessageReceived(RBHproto.RBHMessage message) {
                    handleMessage(message);
                }

                @Override
                public void onConnected() {
                    authenticate();
                }
            });
    }

    private void getDataSet(){
        RBHproto.RBHMessage m = ProtoFactory.buildGetDataSetMessage(
                RaspberryHomeClient.CLIENT_ID,
                "livingroom_sensormodule",
                "temp",
                100,
                "geht noch nicht",
                "geht noch nicht"
        );
        client.write(m);
    }

    private void authenticate() {
        RBHproto.RBHMessage m = ProtoFactory.buildAuthRequestMessage(
                RaspberryHomeClient.CLIENT_ID, "abc12345");
        client.write(m);
    }

    private void handleMessage(RBHproto.RBHMessage message){
        if(message.getMType() == RBHproto.RBHMessage.MessageType.AUTH_ACCEPT){
            String acceptMessage = message.getPlainText().getText();
            Log.d(logClientTag, "Authentication successful. " +  acceptMessage);
            getDataSet();
        }else if(message.getMType() == RBHproto.RBHMessage.MessageType.DATA_SET){
            RBHproto.RBHMessage.DataSet dataSet = message.getDataSet();
            mCurrentSeries.clear();
            int currentPosition = 0;
            for(RBHproto.RBHMessage.Data d : dataSet.getDataList()){
                if(d.hasFloatData()){
                    Log.d(logClientTag, "temp: " + d.getFloatData());
                    mCurrentSeries.add(currentPosition,d.getFloatData());
                    currentPosition++;
                }
            }
            mChart.repaint();
        }
    }
}
