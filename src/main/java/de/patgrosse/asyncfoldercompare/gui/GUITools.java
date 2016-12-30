package de.patgrosse.asyncfoldercompare.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public final class GUITools {

    private GUITools() {
    }

    public static void centerFrameInScreen(Window window) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation(dim.width / 2 - window.getSize().width / 2, dim.height / 2 - window.getSize().height / 2);
    }

}
