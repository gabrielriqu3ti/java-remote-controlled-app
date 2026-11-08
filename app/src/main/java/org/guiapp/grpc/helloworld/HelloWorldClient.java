package org.guiapp.grpc.helloworld;


import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public class HelloWorldClient {

    private static final int SHUTDOWN_TIMEOUT_SECS = 5;

    private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public HelloWorldClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public String greet(String name) {
        logger.info(String.format("Will try to greet %s ...", name));
        String message = null;
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
            message = response.getMessage();
            logger.info(String.format("Greeeting: %s", message));
        } catch (StatusRuntimeException sre) {
            logger.log(Level.WARNING, "RPC failed: {0}", sre.getStatus());
        }
        return message;
    }

    public static void main(String[] args) throws Exception {
        String user = "world";
        // Access running server on the local machine on port 50051.
        String target = "localhost:50051";

        // Allow passing in the user and the target strings as command line arguments.
        if (args.length > 0) {
            if (args[0].equals("--help")) {
                System.err.println("User: [name [target]]");
                System.err.println("");
                System.err.println("name    The name you wish to be greeted by");
                System.err.println(String.format("target  The server to connect to. Defaults to %s", target));
                System.exit(1);
            }
            user = args[0];
        }

        if (args.length > 1) {
            target = args[1];
        }

        // Create a communication channel with the server
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();

        try {
            HelloWorldClient client = new HelloWorldClient(channel);
            client.greet(user);
        } finally {
            channel.shutdownNow().awaitTermination(SHUTDOWN_TIMEOUT_SECS, TimeUnit.SECONDS);
        }
    }
}
