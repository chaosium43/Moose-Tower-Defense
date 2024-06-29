package ISU;

import java.util.*;
import java.awt.*;

public class Cannon extends Tower {
    private static double[] delays = {1, 1, 0.8, 0.8, 0.8};
    private static int[] pierces = {14, 20, 30, 60, 100};
    private static int[] damages = {1, 1, 2, 3, 10};
    private static double[] speeds = {400, 750, 1000, 1000, 1000};
    private static Rectangle[] eRadiuses = {new Rectangle(75, 75), new Rectangle(125, 125), new Rectangle(125, 125), new Rectangle(150, 150), new Rectangle(225, 225)};
    private static Color[] colours = {new Color(0), new Color(0), new Color(0), new Color(0xff0000), new Color(0x00ff00)};

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
            switch (rightPath) {
                case 3:
                    new ExplosiveProjectile(new Rectangle((int)hb.getCenterX() - 5, (int)hb.getCenterY() - 5, 10, 10), colours[leftPath], parent, pierces[leftPath], damages[leftPath], 1.0, direction.multiply(speeds[rightPath]), eRadiuses[leftPath]) {
                        public void createExplosion(Rectangle explosion) {
                            new Explosion(explosion, new Color(0xffff00), parent, pierce, damage, 0.2, velocity) {
                                public void addTouchedMoose(Moose m) {
                                    super.addTouchedMoose(m);
                                    m.addStatusEffect("stun", new Triple<Integer, Double, Double>(1, 2.0, 2.0));
                                }
                            };
                        }
                    };
                    break;
                case 4:
                    new ExplosiveProjectile(new Rectangle((int)hb.getCenterX() - 5, (int)hb.getCenterY() - 5, 10, 10), colours[leftPath], parent, pierces[leftPath], damages[leftPath], 1.0, direction.multiply(speeds[rightPath]), eRadiuses[leftPath]) {
                        public void createExplosion(Rectangle explosion) {
                            new Explosion(explosion, new Color(0xffff00), parent, pierce, damage, 0.2, velocity) {
                                public void addTouchedMoose(Moose m) {
                                    super.addTouchedMoose(m);
                                    m.blowback(Math.max(Math.min(1, Math.sqrt(50 / m.getDamage())), 0.25));
                                    m.addStatusEffect("stun", new Triple<Integer, Double, Double>(1, 2.0, 2.0));
                                }
                            };
                        }
                    };
                    break;
                default:
                    new ExplosiveProjectile(new Rectangle((int)hb.getCenterX() - 5, (int)hb.getCenterY() - 5, 10, 10), colours[leftPath], parent, pierces[leftPath], damages[leftPath], 1.0, direction.multiply(speeds[rightPath]), eRadiuses[leftPath]);
                    break;
            }
            evolution = 0;
            trigger = false;
        }
        target = null;
    }

    public String getName() {
        return "Cannon";
    }

    public Cannon(Vector2 v, TileMap t) {
        super(v, t);
        parent.removeMoney(650);
        setImage(Game.getImages().get("cannon.png"));
        moneySpent += 650;
        range = new Rectangle((int)(v.getX() - 2) * 75, (int)(v.getY() - 2) * 75, 375, 375);
        leftPath = 0;
        rightPath = 0;
        leftUpgrades = new ArrayList<Triple<String, String, Integer>>();
        rightUpgrades = new ArrayList<Triple<String, String, Integer>>();

        leftUpgrades.add(new Triple<String, String, Integer>("Larger Explosion", "Explosion radius is increased.", 350));
        leftUpgrades.add(new Triple<String, String, Integer>("Unstoppable Shockwave", "Pierce and damage of explosions increase.", 650));
        leftUpgrades.add(new Triple<String, String, Integer>("TNT", "Explosions destroy three layers of mooses.", 1100));
        leftUpgrades.add(new Triple<String, String, Integer>("Mini-Nuke", "Explosions destroy ten layers of mooses.", 3600));
        rightUpgrades.add(new Triple<String, String, Integer>("Faster Bombs", "Bombs travel far faster than before.", 200));
        rightUpgrades.add(new Triple<String, String, Integer>("Rocket Array", "Rockets fire faster than normal bombs", 400));
        rightUpgrades.add(new Triple<String, String, Integer>("Shell Shock", "Mooses are momentarily stunned by explosions.", 1400));
        rightUpgrades.add(new Triple<String, String, Integer>("Blowback Bombs", "Explosions cause mooses to be pushed back along the track", 3200));
    }
}
