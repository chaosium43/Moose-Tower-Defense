package ISU;

import java.awt.*;
import java.util.*;

public class FireTower extends Tower {
    private Moose flameTarget;
    private double flameStack = 1;
    private static double[] delays = {1, 1, 1, 0.1, 0.1};
    private static int[] pierces = {2, 5, 10, 20, 300};
    private static int[] damages = {1, 1, 2, 2, 2};
    private static int[] flameDamage = {0, 3, 3, 3, 3};

    public void evolve(double delta) {
        evolution += delta;
        if ((evolution >= delays[leftPath] || trigger) && target != null) {
            Rectangle hb = getHitbox();
            Rectangle thb = target.getHitbox();
            if (leftPath == 4) { // inferno tower code
                if (target != flameTarget) {
                    flameStack = 1;
                    flameTarget = target;
                }
                flameTarget.damage((int)flameStack, null);
                flameStack = Math.min(flameStack * 2, 50);

                new Explosion(new Rectangle((int)thb.getCenterX() - 25, (int)thb.getCenterY() - 25, 50, 50), new Color(0xff8800), parent, pierces[rightPath], damages[leftPath], 0.25, new Vector2(0, 0)) {
                    public boolean immune(Moose m) {
                        for (String s: m.getImmunities()) {
                            if (s.equals("nonsharp")) {
                                return true;
                            }
                        }
                        return false;
                    }

                    public void addTouchedMoose(Moose m) {
                        super.addTouchedMoose(m);
                        m.addStatusEffect("fire", new Triple<Integer, Double, Double>(flameDamage[leftPath], 1.0, 1.0));
                    }

                    public void render(Graphics2D g) {
                        setBackgroundVisible(false);
                        setImage(Game.getImages().get("fire.png"));
                        super.render(g);
                    }
                };
            } else {
                Vector2 direction = new Vector2(thb.getCenterX() - hb.getCenterX(), thb.getCenterY() - hb.getCenterY()).unit();
                switch (rightPath) {
                    case 2: // fireballs
                        new ExplosiveProjectile(new Rectangle((int)hb.getCenterX() - 5, (int)hb.getCenterY() - 5, 10, 10), new Color(0xff8800), parent, pierces[rightPath], damages[leftPath], 1.0, direction.multiply(500), new Rectangle(75, 75)) {
                            public void addTouchedMoose(Moose m) {
                                super.addTouchedMoose(m);
                                m.addStatusEffect("fire", new Triple<Integer, Double, Double>(flameDamage[leftPath], 1.0, 1.0));
                            }
                        };
                        break;
                    case 3: // fire walls
                        new Explosion(new Rectangle((int)hitbox.getMinX() - 75, (int)hitbox.getMinY() - 75, 225, 225), new Color(0), parent, pierces[rightPath], damages[leftPath], 0.25, new Vector2(0, 0)) {
                            public boolean immune(Moose m) {
                                for (String s: m.getImmunities()) {
                                    if (s.equals("nonsharp")) {
                                        return true;
                                    }
                                }
                                return false;
                            }

                            public void addTouchedMoose(Moose m) {
                                super.addTouchedMoose(m);
                                m.addStatusEffect("fire", new Triple<Integer, Double, Double>(flameDamage[leftPath], 1.0, 1.0));
                            }

                            public void render(Graphics2D g) {
                                Image img = Game.getImages().get("fire.png");
                                for (int i = 0; i <= 2; i++) {
                                    for (int j = 0; j <= 2; j++) {
                                        if (i == 1 && j == 1) {
                                            continue;
                                        }
                                        g.drawImage(img, (int)this.hitbox.getMinX() + i * 75, (int)this.hitbox.getMinY() + j * 75, 75, 75, null);
                                    }
                                }
                            }
                        };
                        break;
                    case 4: // crushing fire walls
                        new CrushingProjectile(new Rectangle((int)hitbox.getMinX() - 75, (int)hitbox.getMinY() - 75, 225, 225), new Color(0), parent, pierces[rightPath], damages[leftPath], 0.25, new Vector2(0, 0)) {
                            public boolean immune(Moose m) {
                                for (String s: m.getImmunities()) {
                                    if (s.equals("nonsharp")) {
                                        return true;
                                    }
                                }
                                return false;
                            }

                            public void damage(Moose m) {
                                if (pierce <= 0) {
                                    return;
                                }
                                if (immune(m)) {
                                    destroy();
                                    return;
                                } else {
                                    m.damage(damage, this);
                                }
                                pierce -= 1;
                            }

                            public void addTouchedMoose(Moose m) {
                                super.addTouchedMoose(m);
                                m.addStatusEffect("fire", new Triple<Integer, Double, Double>(flameDamage[leftPath], 1.0, 1.0));
                            }

                            public void render(Graphics2D g) {
                                Image img = Game.getImages().get("fire.png");
                                for (int i = 0; i <= 2; i++) {
                                    for (int j = 0; j <= 2; j++) {
                                        if (i == 1 && j == 1) {
                                            continue;
                                        }
                                        g.drawImage(img, (int)this.hitbox.getMinX() + i * 75, (int)this.hitbox.getMinY() + j * 75, 75, 75, null);
                                    }
                                }
                            }
                        };
                        break;
                    default: // default flame
                        new Projectile(new Rectangle((int)hb.getCenterX() - 5, (int)hb.getCenterY() - 5, 10, 10), new Color(0xff8800), parent, pierces[rightPath], damages[leftPath], 1.0, direction.multiply(500)) {
                            public boolean immune(Moose m) {
                                for (String s: m.getImmunities()) {
                                    if (s.equals("nonsharp")) {
                                        return true;
                                    }
                                }
                                return false;
                            }

                            public void addTouchedMoose(Moose m) {
                                super.addTouchedMoose(m);
                                m.addStatusEffect("fire", new Triple<Integer, Double, Double>(flameDamage[leftPath], 1.0, 1.0));
                            }
                        };
                        break;
                }
            }
            evolution = 0;
            trigger = false;
        } else if (target == null) {
            flameTarget = null;
            flameStack = 1;
        }
        target = null;
    }

    public void render(Graphics2D g) {
        super.render(g);
        if (flameTarget != null) {
            Rectangle targetBox = flameTarget.getHitbox();
            g.setColor(new Color(0xff0000));
            g.setStroke(new BasicStroke((int)flameStack));
            g.drawLine((int)hitbox.getCenterX(), (int)hitbox.getCenterY(), (int)targetBox.getCenterX(), (int)targetBox.getCenterY());
        }
    }

    public String getName() {
        return "Fire Tower";
    }

    public FireTower(Vector2 v, TileMap t) {
        super(v, t);
        parent.removeMoney(400);
        setImage(Game.getImages().get("fire tower.png"));
        moneySpent += 400;
        range = new Rectangle((int)(v.getX() - 2) * 75, (int)(v.getY() - 2) * 75, 375, 375);
        leftPath = 0;
        rightPath = 0;
        leftUpgrades = new ArrayList<Triple<String, String, Integer>>();
        rightUpgrades = new ArrayList<Triple<String, String, Integer>>();

        leftUpgrades.add(new Triple<String, String, Integer>("Hotter Fire", "Attacks now cause mooses to catch on fire.", 300));
        leftUpgrades.add(new Triple<String, String, Integer>("Scorching Heat", "Fires are now hot enough to destory two layers of mooses.", 500));
        leftUpgrades.add(new Triple<String, String, Integer>("Flamethrower", "Spits out fire at ludicrous speeds.", 2600));
        leftUpgrades.add(new Triple<String, String, Integer>("Inferno Tower", "CLASH OF CLANS TH10!!!!!!", 5000));
        rightUpgrades.add(new Triple<String, String, Integer>("Bigger Fire", "Fires can harm up to five mooses at once.", 200));
        rightUpgrades.add(new Triple<String, String, Integer>("Fireballs", "Shoots out explosive fireballs instead of flames.", 450));
        rightUpgrades.add(new Triple<String, String, Integer>("Firewall", "Creates a giant wall of fire around the tower.", 1500));
        rightUpgrades.add(new Triple<String, String, Integer>("Ring of Fire", "Millions of mooses must walk into literal hell...", 3200));
    }
}
