package org.guiapp.grpc.helloworld;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;

public class HelloWorldServer {

    private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());

    private static final int PORT = 50_051;
    private static final int TOTAL_THREADS = 2;
    private static final int SHUTDOWN_TIMEOUT_SECS = 30;

    private Server server;

    public HelloWorldServer() {}

    public void start() throws IOException {

        /*
         * By default gRPC uses a global, shared Executor.newCachedThreadPool() for gRPC callbacks into
         * your application. This is convenient, but can cause an excessive number of threads to be
         * created if there are many RPCs. It is often better to limit the number of threads your
         * application uses for processing and let RPCs queue when the CPU is saturated.
         * The appropriate number of threads varies heavily between applications.
         * Async application code generally does not need more threads than CPU cores.
         */
        ExecutorService executor = Executors.newFixedThreadPool(TOTAL_THREADS);

        server = Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
        .executor(executor)
        .addService(new GreeterImpl())
        .build()
        .start();

        logger.info(String.format("Server started, listening on port %d", PORT));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // user stderr since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");

                try {
                    HelloWorldServer.this.stop();
                } catch (InterruptedException ie) {
                    if (server != null) {
                        server.shutdownNow();
                    }
                    ie.printStackTrace(System.err);
                } finally {
                    executor.shutdown();
                }

                System.err.println("*** gRPC server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(SHUTDOWN_TIMEOUT_SECS, TimeUnit.SECONDS);
        }
    }

    public static int getPort() {
        return PORT;
    }

    /**
     * GreeterGrpc is automatically generated using helloworld.proto file
     */
    protected static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) {
        HelloWorldServer server = new HelloWorldServer();
        try {
            server.start();
            Thread.sleep(60_000);
            server.stop();
        } catch (Exception e) {

        }
    }
}
