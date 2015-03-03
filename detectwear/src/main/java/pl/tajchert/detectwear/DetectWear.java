package pl.tajchert.detectwear;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class DetectWear {
    private static final String TAG = "DetectWear";
    private static ArrayList<Node> nodesList = new ArrayList<>();
    private static NodesListener nodesListener;
    private static GoogleApiClient mGoogleApiClient;

    public static void init(final Context context) {
        initGoogleApiClient(context);
    }

    public static boolean isConnected(){
        return nodesList.size() > 0;
    }

    public static ArrayList<Node> getNodes(){
        return nodesList;
    }

    private static void initGoogleApiClient(Context context){
        nodesList = new ArrayList<>();
        if(context == null) {
            return;
        }
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle connectionHint) {
                            Log.d(TAG, "onConnected ");
                        }
                        @Override
                        public void onConnectionSuspended ( int cause){
                            Log.d(TAG, "onConnectionSuspended");
                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.d(TAG, "onConnectionFailed ");
                        }
                    }).addApi(Wearable.API).build();
            Wearable.NodeApi.addListener(mGoogleApiClient, new NodeApi.NodeListener() {
                @Override
                public void onPeerConnected(Node node) {
                    if(!nodesList.contains(node)){
                        nodesList.add(node);
                    }
                    if(nodesListener != null) {
                        nodesListener.nodesChanged(nodesList);
                        nodesListener.onNewConnectedNode(node);
                    }
                }

                @Override
                public void onPeerDisconnected(Node node) {
                    if(nodesList.contains(node)){
                        nodesList.remove(node);
                    }
                    if(nodesListener != null) {
                        nodesListener.nodesChanged(nodesList);
                        if (nodesList.size() == 0) {
                            nodesListener.onNoConnectedNode();
                        }
                    }
                }
            });

        }
        if(mGoogleApiClient != null && !mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                nodesList = new ArrayList<>();
                for (Node node : getConnectedNodesResult.getNodes()) {
                    nodesList.add(node);
                    nodesListener.onNewConnectedNode(node);
                }
                nodesListener.nodesChanged(nodesList);
            }
        });
    }

    public static void setNodesListener(NodesListener nodesListener) {
        DetectWear.nodesListener = nodesListener;
    }

    public interface NodesListener {
        void nodesChanged(ArrayList<Node> nodes);
        void onNoConnectedNode();
        void onNewConnectedNode(Node node);
    }
}
