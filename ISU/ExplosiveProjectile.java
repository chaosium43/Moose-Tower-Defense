package ISU;

import java.awt.*;

public class ExplosiveProjectile extends Projectile {
    protected Rectangle radius;

    public void createExplosion(Rectangle explosion) {
        new Explosion(explosion, new Color(0xffff00), parent, pierce, damage, 0.2, velocity);
    }

    public void damage(Moose m) {
        Point p = radius.getLocation();
        p.translate((int)((hitbox.getWidth() - radius.getWidth()) / 2 + hitbox.getMinX()), (int)((hitbox.getHeight() - radius.getHeight()) / 2 + hitbox.getMinY()));
        createExplosion(new Rectangle(p, radius.getSize()));
        destroy();
    }

    public ExplosiveProjectile(Rectangle r, Color c, TileMap p, int a, int b, double d, Vector2 v, Rectangle rad) {
        super(r, c, p, a, b, d, v);
        radius = rad;
    }
}
