package org.guiapp.robot;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.assertj.swing.fixture.FrameFixture;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;

public class RemoteRobotServer extends Robot {

    private static final Logger logger = Logger.getLogger(RemoteRobotServer.class.getName());
    private static final int PORT = 50_051;
    private static final int TOTAL_THREADS = 2;
    private static final int SHUTDOWN_TIMEOUT_SECS = 30;

    private Server server;
    private final ExecutorService executor = Executors.newFixedThreadPool(TOTAL_THREADS);

    public RemoteRobotServer(FrameFixture window) {
        super(window);
    }

    @Override
    public void execute() {

        if (server != null) {
            return;
        }

        try {
            server = Grpc.newServerBuilderForPort(PORT, InsecureServerCredentials.create())
            .executor(executor)
            .addService(new RemoteRobot(window))
            .build()
            .start();

            logger.info(String.format("Server started, listening on port %d", PORT));

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    if (server == null) {
                        return;
                    }

                    System.out.println("Shutting down gRPC robot since JVM is shutting down...");
                    try {
                        server.shutdownNow().awaitTermination(SHUTDOWN_TIMEOUT_SECS, TimeUnit.SECONDS);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace(System.err);
                    } finally {
                        executor.shutdown();
                    }
                    System.out.println("gRPC robot shut down");
                }
            });
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Failed to start gRPC server", ioe.getCause());
        }
    }

    public void stop() {
        if (server == null) {
            return;
        }

        logger.info("Shutting down gRPC robot...");
        try {
            server.shutdown().awaitTermination(SHUTDOWN_TIMEOUT_SECS, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            logger.log(Level.WARNING, "Shutdown of gRPC server interrupted", ie.getCause());
        } finally {
            executor.shutdown();
        }
        logger.info("gRPC robot shut down");
    }

    @Override
    public void cleanUp() {
        stop();
    }

    public static int getPort() {
        return PORT;
    }

}