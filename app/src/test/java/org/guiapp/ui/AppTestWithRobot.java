package org.guiapp.ui;


import javax.swing.JLabel;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.fixture.FrameFixture;
import org.guiapp.robot.FrameFactory;
import org.guiapp.robot.Robot;
import org.guiapp.robot.RobotFactory;
import org.guiapp.robot.RobotManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppTestWithRobot {

    RobotManager robotManager;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        FrameFactory frameFactory = () -> new App();

        RobotFactory robotFactory = (FrameFixture window) -> new Robot(window) {
            @Override
            public void execute() {
                logState("Initial condition:", window);
                window.label().requireText("Typing...");
                window.textBox().requireText("Insert text here...");
                String text = "New text";
                window.textBox().deleteText().enterText(text);
                window.textBox().requireText(text);
                window.button().click();

                JLabel label = window.label().target();
                Assert.assertTrue(
                    String.format("Preferred size %s smaller than actual size %s", label.getPreferredSize(), label.getSize()),
                    label.getSize().getWidth() >= label.getPreferredSize().getWidth() &&
                    label.getSize().getHeight() >= label.getPreferredSize().getHeight());

                window.label().requireText(text);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                logState("Final condition:", window);
            }

            @Override
            public void cleanUp() {}
        };

        robotManager = new RobotManager(frameFactory, robotFactory);
    }

    @Test
    public void copyTextTest() {
        robotManager.execute();
    }

    @After
    public void tearDown() {
        robotManager.cleanUp();
    }

    private void logState(String note, FrameFixture window) {
        System.out.println(">>>>>>>>>");
        System.out.println(note);
        System.out.println("\ttext box:" + window.textBox().text());
        System.out.println("\tbutton:" + window.button().text());
        System.out.println("\tlabel:" + window.label().text());
        System.out.println("<<<<<<<<<");
    }
}