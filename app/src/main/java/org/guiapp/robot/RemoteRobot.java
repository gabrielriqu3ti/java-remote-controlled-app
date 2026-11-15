package org.guiapp.robot;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.image.ScreenshotTaker;

import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;

public final class RemoteRobot extends RobotGrpc.RobotImplBase {

    FrameFixture window;
    ScreenshotTaker screenshotTaker = new ScreenshotTaker();

    public RemoteRobot(FrameFixture window) {
        super();
        this.window = window;
    }

    @Override
    public void click(Component component, StreamObserver<GuiResponse> responseObserver) {
        System.out.println("Received click request:" + component);
        String buttonName = component.getName();

        window.button(buttonName).click();

        GuiResponse response = GuiResponse.newBuilder().setStatus(Status.STATUS_SUCCESS).build();
        System.out.println("Sending click response:" + response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void setText(ComponentEditor componentEditor, StreamObserver<GuiResponse> responseObserver) {
        System.out.println("Received setText request:" + componentEditor);
        Component component = componentEditor.getComponent();
        String componentName = component.getName();
        String text = componentEditor.getText();

        JTextComponentFixture textBoxFixture = window.textBox(componentName);
        textBoxFixture.deleteText().enterText(text);

        GuiResponse response = GuiResponse.newBuilder().setStatus(Status.STATUS_SUCCESS).setMessage(textBoxFixture.text()).build();
        System.out.println("Sending setText response:" + response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getText(Component component, StreamObserver<GuiResponse> responseObserver) {
        System.out.println("Received getText request:" + component);
        String componentName = component.getName();

        String text = window.label(componentName).text();

        GuiResponse response = GuiResponse.newBuilder().setStatus(Status.STATUS_SUCCESS).setMessage(text).build();
        System.out.println("Sending getText response:" + response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void takeScreenshot(ScreenshotRequest request, StreamObserver<ScreenshotResponse> responseObserver) {
        System.out.println("Received takeScreenshot request:" + request);
        String description = request.getDescription();

        BufferedImage buffImage = screenshotTaker.takeDesktopScreenshot();
        String imageFormat = "png";
        ByteString imageData = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(buffImage, imageFormat, baos);
            imageData = ByteString.copyFrom(baos.toByteArray());
        } catch (IOException ioe) {
            System.err.println("Failed to write screenshot into byte array");
        }

        ScreenshotResponse response;
        if (imageData == null) {
            response = ScreenshotResponse.newBuilder().build();
        } else {
            response = ScreenshotResponse.newBuilder()
            .setStatus(Status.STATUS_SUCCESS)
            .setImage(imageData)
            .setImageFormat(imageFormat)
            .setDescription(description)
            .build();
        }

        System.out.println("Sending takeScreenshot response:" + response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}