package ISU;

import java.awt.*;

public class CrushingProjectile extends Projectile { // accomplishes the same thing as a regular projectile but can harm the same moose multiple times at once
    public void damage(Moose m) {
        if (immune(m)) {
            destroy();
            return;
        } else {
            m.damage(damage, this);
        }
        pierce -= 1;
        if (pierce == 0) {
            destroy();
        }
    }

    public CrushingProjectile(Rectangle r, Color c, TileMap p, int a, int b, double d, Vector2 v) {
        super(r, c, p, a, b, d, v);
    }
}
