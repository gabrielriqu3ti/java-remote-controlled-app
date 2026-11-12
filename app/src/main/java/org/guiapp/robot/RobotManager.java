package org.guiapp.robot;

import javax.swing.JFrame;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;

public class RobotManager {

    private final FrameFactory frameFactory;
    private final RobotFactory robotFactory;

    private FrameFixture window;
    private Robot robot;

    public RobotManager(FrameFactory frameFactory, RobotFactory robotFactory) {
        this.frameFactory = frameFactory;
        this.robotFactory = robotFactory;
    }

    public void execute() {
        JFrame frame = GuiActionRunner.execute(() -> frameFactory.createFrame());
        window = new FrameFixture(frame);
        window.show();
        window.moveToFront();

        robot = robotFactory.createRobot(window);
        robot.execute();
    }

    public void cleanUp() {
        if (window != null) {
            window.cleanUp();
        }

        if (robot != null) {
            robot.cleanUp();
        }
    }
}
