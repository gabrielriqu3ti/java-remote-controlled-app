package org.guiapp.robot;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

public class RemoteRobotClient {

    private static final Logger logger = Logger.getLogger(RemoteRobotClient.class.getName());

    private RobotGrpc.RobotBlockingStub blockingStub;

    public RemoteRobotClient(Channel channel) {
        blockingStub = RobotGrpc.newBlockingStub(channel);
    }

    public boolean clickButton(String buttonName) {
        logger.info(String.format("Trying to click button %s...", buttonName));
        Component component = Component.newBuilder().setName(buttonName).build();
        GuiResponse response = null;
        try {
            response = blockingStub.click(component);
            logger.info(String.format("Successfully button %s clicked. See %s", buttonName, response.toString()));
        } catch (StatusRuntimeException sre) {
            logger.log(Level.WARNING, "RPC failed {0}", sre.getStatus());
        }
        return response != null && response.getStatus() == Status.STATUS_SUCCESS;
    }

    public String setTextBoxText(String textBoxName, String text) {
        logger.info(String.format("Trying to enter text to text box %s...", textBoxName));
        Component component = Component.newBuilder().setName(textBoxName).build();
        ComponentEditor componentEditor = ComponentEditor.newBuilder().setComponent(component).setText(text).build();
        GuiResponse response;
        String componentText = null;
        try {
            response = blockingStub.setText(componentEditor);
            if (response != null && response.getStatus() == Status.STATUS_SUCCESS) {
                componentText = response.getMessage();
                logger.info(String.format("successfully typed %s into text box %s. See %s",
                text, textBoxName, response.toString()));
            }
        } catch (StatusRuntimeException sre) {
            logger.log(Level.WARNING, "RPC failed {0}", sre.getStatus());
        }
        return componentText;
    }


    public String getLabelText(String labelName) {
        logger.info(String.format("Trying to get text of label %s...", labelName));
        Component component = Component.newBuilder().setName(labelName).build();
        GuiResponse response = null;
        String text = null;
        try {
            response = blockingStub.getText(component);
            if (response != null && response.getStatus() == Status.STATUS_SUCCESS) {
                text = response.getMessage();
                logger.info(String.format("successfully got text %s from label %s. See %s",
                text, labelName, response.toString()));
            }
        } catch (StatusRuntimeException sre) {
            logger.log(Level.WARNING, "RPC failed {0}", sre.getStatus());
        }
        return text;
    }
}