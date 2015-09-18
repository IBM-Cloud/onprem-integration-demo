package org.vaadin.presentation;

import com.vaadin.server.Page;

public enum ScreenSize {

    SMALL, MEDIUM, LARGE;

    /**
     * A helper method to get the categorized size for the currently active
     * client.
     *
     * @return the screen size category for currently active client
     */
    public static ScreenSize getScreenSize() {
        int width = Page.getCurrent().getBrowserWindowWidth();
        if (width < 600) {
            return SMALL;
        } else if (width > 1050) {
            return LARGE;
        } else {
            return MEDIUM;
        }
    }
}
