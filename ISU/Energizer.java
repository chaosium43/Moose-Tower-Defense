package ISU;

import java.util.*;
import java.awt.*;

public class Energizer extends Tower {
    private static double[] delays = {1, 1, 1, 0.25, 0.25};
    private static int[] pierces = {3, 10, 25, 25, 100};
    private static int[] damages = {1, 2, 2, 5, 5};
    private static Color[] colours = {new Color(0x0000ff), new Color(0xff00ff), new Color(0xffff00), new Color(0xffff00), new Color(0xff0000)};

    public void evolve(double delta) {
        evolution += delta;
        if ((evolution >= delays[leftPath] || trigger) && target != null) {
            Rectangle hb = getHitbox();
            Rectangle thb = target.getHitbox();
            Vector2 direction = new Vector2(thb.getCenterX() - hb.getCenterX(), thb.getCenterY() - hb.getCenterY()).unit();
            rotation = Math.acos(direction.getX());
            if (direction.getY() < 0) {
                rotation *= -1;
            }
            if (rightPath == 4) { // crushing projectile
                new CrushingProjectile(new Rectangle((int)hb.getCenterX() - 20, (int)hb.getCenterY() - 20, 40, 40), colours[leftPath], parent, pierces[leftPath], damages[rightPath], 1, direction.multiply(500)) {
                    public boolean immune(Moose m) {
                        return false;
                    }
                };
            } else if (leftPath == 4) { // laser
                int x = (int)hb.getCenterX();
                int y = (int)hb.getCenterY();
                Vector2 laserDir = direction.multiply(2000);
                new LaserProjectile(new Rectangle(x, y, (int)laserDir.getX(), (int)laserDir.getY()), colours[leftPath], parent, pierces[leftPath], damages[rightPath], 0.2, direction);
            } else { // normal projectile
                if (rightPath >= 2) {
                    new Projectile(new Rectangle((int)hb.getCenterX() - 10, (int)hb.getCenterY() - 10, 20, 20), colours[leftPath], parent, pierces[leftPath], damages[rightPath], 1, direction.multiply(500)) {
                        public boolean immune(Moose m) {
                            return false;
                        }
                    };
                } else {
                    new Projectile(new Rectangle((int)hb.getCenterX() - 10, (int)hb.getCenterY() - 10, 20, 20), colours[leftPath], parent, pierces[leftPath], damages[rightPath], 1, direction.multiply(500)) {
                        public boolean immune(Moose m) {
                            for (String s: m.getImmunities()) {
                                if (s.equals("sharp") || s.equals("nonsharp")) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    };
                }
            }
            evolution = 0;
            trigger = false;
        }
        target = null;
    }

    public String getName() {
        return "Energizer";
    }

    public Energizer(Vector2 v, TileMap t) {
        super(v, t);
        parent.removeMoney(500);
        setImage(Game.getImages().get("energizer.png"));
        moneySpent += 500;
        range = new Rectangle((int)(v.getX() - 2) * 75, (int)(v.getY() - 2) * 75, 375, 375);
        leftPath = 0;
        rightPath = 0;
        leftUpgrades = new ArrayList<Triple<String, String, Integer>>();
        rightUpgrades = new ArrayList<Triple<String, String, Integer>>();

        leftUpgrades.add(new Triple<String, String, Integer>("Piercing Energy", "Energy bolts now have 10 pierce instead of 3.", 300));
        leftUpgrades.add(new Triple<String, String, Integer>("Solar Bolts", "Energy bolts have pierce upgraded to 25.", 650));
        leftUpgrades.add(new Triple<String, String, Integer>("Plasma Cannon", "Energizer fires four times as fast!", 1600));
        leftUpgrades.add(new Triple<String, String, Integer>("Laser Cannon", "Energy bolts are replaced with a deadly laser.", 4250));
        rightUpgrades.add(new Triple<String, String, Integer>("Concentrated Energy", "Doubles the damage of bolts.", 450));
        rightUpgrades.add(new Triple<String, String, Integer>("Universal Energy", "Energy bolts can destroy any type of moose", 300));
        rightUpgrades.add(new Triple<String, String, Integer>("Devastating Energy", "Energy bolts now do 5 damage.", 1750));
        rightUpgrades.add(new Triple<String, String, Integer>("Crushing Circles", "Bolts now do crushing amounts of damage to single mooses", 3350));
    }
}
