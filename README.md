#DetectWear - simple detecting Android Wear device
Small library to detect if Android Wear device is connected to users phone.

###How to use it?
Just copy class [DetectWear.java](https://github.com/tajchert/DetectWear/blob/master/detectwear/src/main/java/pl/tajchert/detectwear/DetectWear.java) to your project, and remember to initialized it on app start, such as:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    //...
    DetectWear.init(this);
}
```

specify listeners such as:

```java
DetectWear.setNodesListener(new DetectWear.NodesListener() {
    @Override
    public void nodesChanged(ArrayList<Node> nodes) {
      //List changed
    }

    @Override
    public void onNoConnectedNode() {
      //No connected Android Wear device
    }

    @Override
    public void onNewConnectedNode(Node node) {
      //Android Wear device just got connected
    }
});
```

or just call methods:

```java
NodeConnectionState nodeConnectionState = DetectWear.getConnectionState();
boolean isConnected = DetectWear.isConnected();
```


For more info take a look into sample project and see how it works in [MainActivity.java](https://github.com/tajchert/DetectWear/blob/master/sampleApp/src/main/java/pl/tajchert/detectwearsample/MainActivity.java)

###Here be dragons!
Known disadvantage of `isConnected()` is that it will return false until connection to Nodes is checked, that is why it is better to use `getConnectionState` which in same situation will return `NodeConnectionStates.Undetermined` value.
