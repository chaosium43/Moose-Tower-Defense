package ISU;

import java.awt.Color;
import java.awt.Rectangle;

public class Explosion extends Projectile { // explosion that comes out when an explosive projectile blows up
    public boolean immune(Moose m) {
        for (String s: m.getImmunities()) {
            if (s.equals("explosive") || s.equals("nonsharp")) {
                return true;
            }
        }
        return false;
    }

    public void evolve(double delta) {
        evolution += delta;
        if (evolution > decay) {
            destroy();
        }
    }

    public void damage(Moose m) {
        if (!touchedMooses.keySet().contains(m) && pierce > 0) {
            if (immune(m)) {
                return;
            } else {
                addTouchedMoose(m);
                m.damage(damage, this);
            }
            pierce -= 1;
        }
    }

    public Explosion(Rectangle r, Color c, TileMap p, int a, int b, double d, Vector2 v) {
        super(r, c, p, a, b, d, v);
    }
}
