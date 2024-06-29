// implements buttons for this game

package ISU;

import java.util.*;
import java.util.concurrent.*;
import java.awt.*;

public class ButtonComponent extends RenderComponent {
    private static ConcurrentSkipListSet<ButtonComponent> buttons;

    // this is what happens when the button is pressed yay!
    public void onPressed() {}

    // method that is fired from main 
    public static void processClick(int x, int y) {
        if (buttons == null) { // initialize the buttons map
            buttons = new ConcurrentSkipListSet<ButtonComponent>(Collections.reverseOrder());
        }
        Rectangle mouseHitbox = new Rectangle(x, y, 1, 1);
        for (ButtonComponent button: buttons) {
            if (button.intersects(mouseHitbox) && button.getVisible()) {
                button.onPressed();
                return;
            }
        }
    }

    // adding extra destroy code so that the button is no longer being tracked in the button hashmap
    public void destroy() {
        super.destroy();
        buttons.remove(this);
    }

    public void setRenderPriority(int i) {
        buttons.remove(this);
        super.setRenderPriority(i);
        buttons.add(this);
    }

    public ButtonComponent(Rectangle r, Color c) {
        super(r, c);
        if (buttons == null) { // initialize the buttons map
            buttons = new ConcurrentSkipListSet<ButtonComponent>(Collections.reverseOrder());
        }
        buttons.add(this);
    }
}
