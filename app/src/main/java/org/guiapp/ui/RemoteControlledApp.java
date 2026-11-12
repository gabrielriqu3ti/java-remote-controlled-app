package org.guiapp.ui;

import org.assertj.swing.fixture.FrameFixture;

import org.guiapp.robot.FrameFactory;
import org.guiapp.robot.RemoteRobotServer;
import org.guiapp.robot.RobotFactory;
import org.guiapp.robot.RobotManager;

public class RemoteControlledApp extends App {

    public static void main(String[] args) {
        FrameFactory frameFactory = () -> new RemoteControlledApp();
        RobotFactory robotFactory = (FrameFixture frame) -> new RemoteRobotServer(frame);
        RobotManager robotManager = new RobotManager(frameFactory, robotFactory);

        robotManager.execute();

        try {
            Thread.currentThread().sleep(60_000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();               
        }

        robotManager.cleanUp();
    }
}
