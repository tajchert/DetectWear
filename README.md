#DetectWear - simple detecting Android Wear device
=======

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/pl.tajchert/detectwear/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/pl.tajchert/detectwear)

Small library to detect if user has Android Wear device, and reacting to its getting connected/disconnected.

###How to use it?
Just add gradle dependency or copy class [DetectWear.java](https://raw.githubusercontent.com/tajchert/DetectWear/master/detectwear/src/main/java/pl/tajchert/detectwear/DetectWear.java) to your project, just remember to initialized it on app start, such as:
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

###Dependency
Gradle:
```gradle
    //library:
    compile 'pl.tajchert:detectwear:0.1.0'
```

Maven:
```xml
<dependency>
    <groupId>pl.tajchert</groupId>
    <artifactId>detectwear</artifactId>
    <version>0.1.0</version>
</dependency>
```

###Here be dragons!
Known disadvantage of `isConnected()` is that it will return false until connection to Nodes is checked, that is why it is better to use `getConnectionState` which in same situation will return `NodeConnectionStates.Undetermined` value.
