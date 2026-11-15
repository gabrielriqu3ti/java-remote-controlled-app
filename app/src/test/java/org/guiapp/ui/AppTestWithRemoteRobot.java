package org.guiapp.ui;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

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
    List<File> generatedFiles = new ArrayList<>();

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
        String imageFormat = "png";

        BufferedImage imageBefore = client.takeScreenshot("Screenshot before text was typed");
        saveImage(imageBefore, imageFormat, "appAtBeginning.png");

        String newText = "New text";
        String newTextBoxText = client.setTextBoxText(String.join(".", "App", "JTextField", "inputText"), newText);
        Assert.assertEquals("Failed to type text into text box", newText, newTextBoxText);

        boolean clicked = client.clickButton(String.join(".", "App", "JButton", "copyText"));
        Assert.assertTrue("Failed to click button", clicked);

        String newTextLabel = client.getLabelText(String.join(".", "App", "JLabel", "copiedText"));
        Assert.assertEquals("Failed to update text of label", newText, newTextLabel);

        BufferedImage imageAfter = client.takeScreenshot("Screenshot after copy text was copyed");
        saveImage(imageAfter, imageFormat, "appAtEnd.png");
    }

    private boolean saveImage(BufferedImage image, String imageFormat, String imageFilename) {
        boolean result = false;
        try {
            File imageFile = new File(imageFilename);
            generatedFiles.add(imageFile);
            result = ImageIO.write(image, imageFormat, imageFile);
        } catch (IOException ioe) {
            Assert.fail(String.format("Failed to save image %s with error %s", imageFilename, ioe.getMessage()));
        }
        return result;
    }

    private void deleteGeneratedFiles() {
        for (File file : generatedFiles) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException ioe) {
                System.err.println(String.format("Failed to delete file %s with error %s", file.toString(), ioe.getMessage()));
            }
        }
    }

    @After
    public void tearDown() {
        deleteGeneratedFiles();
        robotManager.cleanUp();
    }
}