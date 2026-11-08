package org.guiapp.robot;

import org.assertj.swing.fixture.FrameFixture;

public interface RobotFactory {

    Robot createRobot(FrameFixture window);

}