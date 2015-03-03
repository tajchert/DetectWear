package pl.tajchert.detectwearsample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.google.android.gms.wearable.Node;

import java.util.ArrayList;
import java.util.Calendar;

import pl.tajchert.detectwear.DetectWear;


public class MainActivity extends ActionBarActivity implements DetectWear.NodesListener {
    private TextView textViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewMain = (TextView) findViewById(R.id.textViewMain);
        DetectWear.init(this);
        DetectWear.setNodesListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appendTextView("onResume connection state: " + DetectWear.getConnectionState());
    }

    @Override
    public void nodesChanged(ArrayList<Node> nodes) {
        appendTextView("List changed, current size: " + nodes.size());
    }

    @Override
    public void onNoConnectedNode() {
        appendTextView("No connected nodes.");
    }

    @Override
    public void onNewConnectedNode(Node node) {
        appendTextView("onNewConnectedNode: " + node.getDisplayName());
    }


    private void appendTextView(String textToAdd) {
        Calendar calendarNow = Calendar.getInstance();
        String currentTime = String.format("%02d:%02d:%02d", calendarNow.get(Calendar.HOUR_OF_DAY), calendarNow.get(Calendar.MINUTE), calendarNow.get(Calendar.SECOND));
        if(textViewMain != null) {
            textViewMain.setText(currentTime + " | " + textToAdd + "\n" + textViewMain.getText());
        }
    }
}
