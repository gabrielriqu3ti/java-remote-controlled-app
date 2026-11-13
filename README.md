# Java Remote Controlled App

This project consists of a simple app with a GUI that allows a remote client to update the GUI using a gRPC service.
This can be very useful to automate GUI tests.

## Build

This application uses the Gradle Build system. So, execute the following command to build it and run its tests:

```[bash]
gradlew build
```

## Execution

Execute the following command to run this application:

```[bash]
gradlew run
```

The graphical user interface of the application should be displayed.

The gRPC server should also be running on the port 50051. To communicate with it, run a gRPC client that implements the protobuff src\main\proto\guicomponent.proto.
