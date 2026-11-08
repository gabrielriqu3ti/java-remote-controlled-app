package org.guiapp.grpc.helloworld;

import java.io.IOException;
import java.lang.System.Logger.Level;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;


public class HelloWorldGrpcTest {

    HelloWorldServer server;

    @Before
    public void setUp() {
        try {
            server = new HelloWorldServer();
            server.start();
        } catch (IOException ioe) {
            System.getLogger(HelloWorldGrpcTest.class.getName()).log(Level.ERROR, "Failed to start server", ioe);
        }
    }

    @Test
    public void greetTest() {
        String servicePath = String.format("localhost:%d", HelloWorldServer.getPort());
        ManagedChannel channel = Grpc.newChannelBuilder(servicePath, InsecureChannelCredentials.create()).build();

        HelloWorldClient client = new HelloWorldClient(channel);

        String user = "World";
        String serverMessage = client.greet(user);
        String expectedMessage = String.format("Hello %s", user);

        Assert.assertEquals(expectedMessage, serverMessage);
    }

    @After
    public void tearDown() {
        if (server == null) {
            return;
        }

        try {
            server.stop();
            server = null;
        } catch (InterruptedException ex) {
            System.getLogger(HelloWorldGrpcTest.class.getName()).log(Level.ERROR, "Failed to stop server", ex);
        }
    }
}
