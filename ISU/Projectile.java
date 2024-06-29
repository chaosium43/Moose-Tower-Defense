package ISU;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

public class Projectile extends RenderComponent {
    protected int pierce;
    protected int damage;
    protected double decay;
    protected Vector2 velocity;
    protected Vector2 position;
    protected double evolution;
    protected ConcurrentHashMap<Moose, Boolean> touchedMooses;
    protected TileMap parent;

    public void destroy() {
        super.destroy();
        parent.removeProjectile(this);
    }

    public void evolve(double d) {
        evolution += d;
        if (evolution > decay) {
            destroy();
        }
        position = position.add(velocity.multiply(d)); // projectile motion wha?????
        setHitbox(new Rectangle(new Point((int)position.getX(), (int)position.getY()), getHitbox().getSize()));
    }

    protected boolean immune(Moose m) {
        for (String s: m.getImmunities()) {
            if (s.equals("sharp")) {
                return true;
            }
        }
        return false;
    }

    public void addTouchedMoose(Moose m) {
        touchedMooses.put(m, true);
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

    public Projectile(Rectangle r, Color c, TileMap p, int a, int b, double d, Vector2 v) {
        super(r, c);
        parent = p;
        pierce = a;
        damage = b;
        decay = d;
        velocity = v;
        position = new Vector2(r.getMinX(), r.getMinY());
        touchedMooses = new ConcurrentHashMap<Moose, Boolean>();
        parent.addProjectile(this);
    }
}
