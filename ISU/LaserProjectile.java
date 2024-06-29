package ISU;

import java.awt.*;

public class LaserProjectile extends Projectile { 
    public void render(Graphics2D g) {
        g.setStroke(new BasicStroke(10));
        g.setColor(getColour());
        g.drawLine((int)hitbox.getMinX(), (int)hitbox.getMinY(), (int)hitbox.getMaxX(), (int)hitbox.getMaxY());
    }
    public void evolve(double delta) {
        evolution += delta;
        if (evolution > decay) {
            destroy();
        }
    }

    public void damage(Moose m) {
        if (!touchedMooses.keySet().contains(m)) {
            if (immune(m)) {
                destroy();
                return;
            } else {
                addTouchedMoose(m);
                m.damage(damage, this);
            }
            pierce -= 1;
            if (pierce == 0) {
                destroy();
            }
        }
    }
    
    public boolean intersects(Rectangle r) {
        return r.intersectsLine(hitbox.getMinX(), hitbox.getMinY(), hitbox.getMaxX(), hitbox.getMaxY());
    }
    public LaserProjectile(Rectangle r, Color c, TileMap p, int a, int b, double d, Vector2 v) {
        super(r, c, p, a, b, d, v);
    }
}
