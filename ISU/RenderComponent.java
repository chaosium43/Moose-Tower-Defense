package ISU;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.image.*;
import java.util.*;
import java.util.concurrent.*;

// god class for all graphical objects in this game
public class RenderComponent implements Comparable<RenderComponent> {
    protected Rectangle hitbox;
    private static long totalComponents = 0;
    private long componentID;
    private static ConcurrentSkipListSet<RenderComponent> renderComponents;
    private Color colour;
    private Color textColour;
    private String text;
    private Font font;
    private Vector2 textAnchor;
    private int renderPriority;
    private boolean backgroundVisible;
    private BufferedImage image;
    private boolean visible;

    // getters
    public Rectangle getHitbox() {
        return hitbox;
    }

    public Color getColour() {
        return colour;
    }

    public Color getTextColour() {
        return textColour;
    }

    public Font getFont() {
        return font;
    }

    public String getText() {
        return text;
    }

    public Vector2 getTextAnchor() {
        return textAnchor;
    }

    public boolean getBackgroundVisible() {
        return backgroundVisible;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getRenderPriority() {
        return renderPriority;
    }

    public boolean getVisible() {
        return visible;
    }

    // setters
    public void setHitbox(Rectangle r) {
        hitbox = r;
    }

    public void setColour(Color c) {
        colour = c;
    }

    public void setTextColour(Color c) {
        textColour = c;
    }

    public void setFont(Font f) {
        font = f;
    }

    public void setText(String s) {
        text = s;
    }

    public void setTextAnchor(Vector2 v) {
        textAnchor = v;
    }

    public void setBackgroundVisible(boolean b) {
        backgroundVisible = b;
    }

    public void setImage(BufferedImage img) {
        image = img;
    }

    public void setRenderPriority(int i) {
        renderComponents.remove(this);
        renderPriority = i;
        renderComponents.add(this);
    }

    public void setVisible(boolean b) {
        visible = b;
    }

    // destroys element
    public void destroy() {
        renderComponents.remove(this);
    }

    // draws element
    public void render(Graphics2D g) {
        if (!visible) return;
        if (backgroundVisible) {
            g.setColor(colour);
            g.fill(hitbox);
        }
        if (text != null) { // render some text if text field is not null
            g.setFont(font);
            g.setColor(textColour);
            
            FontMetrics fontInfo = g.getFontMetrics();
            LineMetrics metrics = fontInfo.getLineMetrics(text, g);
            double wiggleX = hitbox.getWidth() - fontInfo.stringWidth(text);
            double wiggleY = hitbox.getHeight() - metrics.getAscent() + metrics.getDescent();
            int xPos = (int)(hitbox.getMinX() + wiggleX * textAnchor.getX());
            int yPos = (int)(hitbox.getMinY() + metrics.getAscent() - metrics.getDescent() + wiggleY * textAnchor.getY());
            String[] lines = text.split("\n");
            for (int i = 0; i < lines.length; i++) {
                g.drawString(lines[i], xPos, yPos);
                yPos += metrics.getHeight();
            }
        }
        if (image != null) { // image needs to be drawn
            Image img = image;
            g.drawImage(img, (int) hitbox.getMinX(), (int) hitbox.getMinY(), (int) hitbox.getWidth(),
                    (int) hitbox.getHeight(), null);
        }
    }

    // hitreg
    public boolean intersects(Rectangle r) {
        return hitbox.intersects(r);
    }

    // draws all the Render components
    public static void drawComponents(Graphics2D g) {
        if (renderComponents == null) {
            renderComponents = new ConcurrentSkipListSet<RenderComponent>();
        }
        for (RenderComponent component : renderComponents) {
            component.render(g);
        }
    }

    public RenderComponent(Rectangle r, Color c) {
        hitbox = r;
        renderPriority = 0;
        componentID = totalComponents++; // giving component an ID
        if (renderComponents == null) { // initialize the render components map
            renderComponents = new ConcurrentSkipListSet<RenderComponent>();
        }
        text = "";
        font = new Font("Segoe UI", Font.PLAIN, 16);
        textColour = new Color(0);
        textAnchor = new Vector2(0.5, 0.5);
        renderComponents.add(this);
        colour = c;
        backgroundVisible = true;
        visible = true;
    }

    // for sorting render priorities
    public int compareTo(RenderComponent r) {
        int prioDiff = renderPriority - r.renderPriority;
        if (prioDiff == 0) {
            return Long.compare(componentID, r.componentID);
        }
        return prioDiff;
    }

    public String toString() {
        return String.format("%d %d", renderPriority, componentID);
    }

    public int hashCode() {
        return Long.hashCode(componentID);
    }
}
