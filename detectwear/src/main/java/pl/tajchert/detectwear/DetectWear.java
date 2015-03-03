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

/*The MIT License (MIT)

Copyright (c) 2015 Michal Tajchert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

public class DetectWear {
    private static final String TAG = "DetectWear";
    private static ArrayList<Node> nodesList = new ArrayList<>();
    private static NodesListener nodesListener;
    private static GoogleApiClient mGoogleApiClient;
    private static NodeConnectionState connectionState = NodeConnectionState.Undetermined;
    public static enum NodeConnectionState {Connected, NotConnected, Undetermined}

    public static void init(final Context context) {
        initGoogleApiClient(context);
    }

    public static boolean isConnected() {
        return nodesList.size() > 0;
    }

    public static NodeConnectionState getConnectionState() {
        return connectionState;
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
                            connectionState = NodeConnectionState.NotConnected; //Lack of Google Play Services? (needs more testing)
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
                    connectionState = NodeConnectionState.Connected;
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
                            connectionState = NodeConnectionState.NotConnected;
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
                if(nodesList.size() > 0){
                    connectionState = NodeConnectionState.Connected;
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
