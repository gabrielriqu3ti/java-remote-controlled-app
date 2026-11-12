package org.guiapp.robot;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JTextComponentFixture;

import io.grpc.stub.StreamObserver;

public final class RemoteRobot extends RobotGrpc.RobotImplBase {

    FrameFixture window;

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
}