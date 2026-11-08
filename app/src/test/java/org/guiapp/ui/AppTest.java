package org.guiapp.ui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppTest {

    FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        App frame = GuiActionRunner.execute(() -> new App());
        window = new FrameFixture(frame);
        window.show();
        window.moveToFront();
    }

    @Test
    public void copyTextTest() {
        logState("Initial condition:");
        window.label().requireText("Typing...");
        window.textBox().requireText("Insert text here...");
        String text = "New text";
        window.textBox().deleteText().enterText(text);
        window.textBox().requireText(text);
        window.button().click();
        window.label().requireText(text);
        logState("Final condition:");
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }

    private void logState(String note) {
        System.out.println(">>>>>>>>>");
        System.out.println(note);
        System.out.println("\ttext box:" + window.textBox().text());
        System.out.println("\tbutton:" + window.button().text());
        System.out.println("\tlabel:" + window.label().text());
        System.out.println("<<<<<<<<<");
    }
}
