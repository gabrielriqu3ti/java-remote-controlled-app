package org.guiapp.robot;

import org.assertj.swing.fixture.FrameFixture;

public abstract class Robot {

    protected FrameFixture window;

    public Robot(FrameFixture window) {
        this.window = window;
    }

    public abstract void execute();

    public abstract void cleanUp();
}
