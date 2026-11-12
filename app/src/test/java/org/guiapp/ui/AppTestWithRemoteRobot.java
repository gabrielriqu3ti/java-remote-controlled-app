package org.guiapp.ui;


import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.guiapp.robot.FrameFactory;
import org.guiapp.robot.RemoteRobotClient;
import org.guiapp.robot.RemoteRobotServer;
import org.guiapp.robot.RobotFactory;
import org.guiapp.robot.RobotManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public class AppTestWithRemoteRobot {

    RobotManager robotManager;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        FrameFactory frameFactory = () -> new App();

        RobotFactory robotFactory = (FrameFixture window) -> new RemoteRobotServer(window);

        robotManager = new RobotManager(frameFactory, robotFactory);
    }

    @Test
    public void copyTextTest() {
        robotManager.execute();

        String servicePath = String.format("localhost:%d", RemoteRobotServer.getPort());
        ManagedChannel channel = Grpc.newChannelBuilder(servicePath, InsecureChannelCredentials.create()).build();
        RemoteRobotClient client = new RemoteRobotClient(channel);

        String newText = "New text";
        String newTextBoxText = client.setTextBoxText(String.join(".", "App", "JTextField", "inputText"), newText);
        Assert.assertEquals("Failed to type text into text box", newText, newTextBoxText);

        boolean clicked = client.clickButton(String.join(".", "App", "JButton", "copyText"));
        Assert.assertTrue("Failed to click button", clicked);

        String newTextLabel = client.getLabelText(String.join(".", "App", "JLabel", "copiedText"));
        Assert.assertEquals("Failed to update text of label", newText, newTextLabel);
    }

    @After
    public void tearDown() {
        robotManager.cleanUp();
    }
}