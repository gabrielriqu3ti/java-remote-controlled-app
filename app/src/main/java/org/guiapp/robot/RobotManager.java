package org.guiapp.robot;

import javax.swing.JFrame;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;

public class RobotManager {

    private final FrameFactory frameFactory;
    private final RobotFactory robotFactory;

    public RobotManager(FrameFactory frameFactory, RobotFactory robotFactory) {
        this.frameFactory = frameFactory;
        this.robotFactory = robotFactory;
    }

    public void execute() {
        JFrame frame = GuiActionRunner.execute(() -> frameFactory.createFrame());
        FrameFixture window = new FrameFixture(frame);
        window.show();
        window.moveToFront();

        robotFactory.createRobot(window).execute();

        window.cleanUp();
    }
}
