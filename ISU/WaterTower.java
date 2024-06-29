package ISU;

import java.util.*;
import java.awt.*;

public class WaterTower extends Tower {
    private static double[] tankSize = {5, 10, 10, 25, 50};
    private static double[] tankRecharge = {1, 1, 2, 2, 5};
    private static double[] delays = {1, 0.4, 0.4, 0.4, 0.4};
    private static double[] blowback = {0, 0, 0, 0, 0.1};
    private static int[] freeze = {0, 0, 0, 1, 1};
    private static int[] scald = {0, 0, 0, 1, 3};
    private static int[] damages = {1, 1, 2, 4, 8};
    private static int[] pierces = {1, 2, 2, 4, 8};
    private static double[] sprayer = {45, 45, 22.5, 22.5, 22.5};
    private static int attackChain = 0;
    private double charge = 0;

    private static Color getProjectileColour(int left, int right) {
        if (left >= 3) {
            return new Color(0xbbbbbb);
        } else if (right >= 3) {
            return new Color(0x00ffff);
        } else {
            return new Color(0x4444ff);
        }
    }

    private int getAttackChain() { // used for making the water tower not instantly release its charge so it doesn't deplete its tank
        if (attackChain >= 2 / delays[leftPath]) {
            return 4;
        } else {
            return 1;
        }
    }

    public void evolve(double delta) {
        charge = Math.min(tankSize[rightPath], charge + delta * tankRecharge[rightPath]);
        evolution += delta;
        if ((evolution >= delays[leftPath] / getAttackChain() || trigger) && target != null) {
            if (charge >= 1.25) { // send a volley of fire
                trigger = false;
                charge -= 1.25;
                evolution = 0;
                attackChain++;
                for (double theta = 0; theta < 360; theta += sprayer[leftPath]) {
                    double thetaR = theta * Math.PI / 180;
                    Vector2 direction = new Vector2(Math.sin(thetaR), Math.cos(thetaR)).multiply(500);
                    new Projectile(new Rectangle((int)hitbox.getMinX() + 30, (int)hitbox.getMinY() + 30, 15, 15), getProjectileColour(leftPath, rightPath), parent, pierces[leftPath], damages[leftPath], 0.25, direction) {
                        public boolean immune(Moose m) {
                            for (String s: m.getImmunities()) {
                                if (s.equals("nonsharp")) {
                                    return true;
                                }
                                if (leftPath < 3 && s.equals("sharp")) {
                                    return true;
                                }
                                if (rightPath > 2 && leftPath < 3 && s.equals("freeze")) {
                                    return true;
                                }
                            }
                            return false;
                        }

                        public void addTouchedMoose(Moose m) {
                            super.addTouchedMoose(m);
                            m.addStatusEffect("fire", new Triple<Integer, Double, Double>(scald[leftPath], 1.0, 1.0));
                            m.addStatusEffect("freeze", new Triple<Integer, Double, Double>(freeze[rightPath], 3.0, 3.0));
                            m.blowback(blowback[rightPath]);
                        }
                    };
                }
            }
        } else if (target == null) {
            attackChain = 0;
        }
        target = null;
    }

    public void replenishTrigger() {
        charge = tankSize[rightPath];
        trigger = true;
    }

    public String getName() {
        return "Water Tower";
    }
    
    public WaterTower(Vector2 v, TileMap t) {
        super(v, t);
        parent.removeMoney(350);
        setImage(Game.getImages().get("water tower.png"));
        moneySpent += 350;
        range = new Rectangle((int)(v.getX() - 1) * 75, (int)(v.getY() - 1) * 75, 225, 225);
        leftPath = 0;
        rightPath = 0;
        leftUpgrades = new ArrayList<Triple<String, String, Integer>>();
        rightUpgrades = new ArrayList<Triple<String, String, Integer>>();

        leftUpgrades.add(new Triple<String, String, Integer>("Water Hose", "Water is and is released faster and stronger.", 300));
        leftUpgrades.add(new Triple<String, String, Integer>("Water Fountain", "Fires double the amount of water per shot.", 700));
        leftUpgrades.add(new Triple<String, String, Integer>("Steam Shooter", "Steam does additional damage, scalds mooses, and can destroy lead.", 1250));
        leftUpgrades.add(new Triple<String, String, Integer>("Steam Reactor", "Hypercharged steam is far more potent", 3900));
        rightUpgrades.add(new Triple<String, String, Integer>("Larger Tank", "Increases the size of the tower's water tank.", 400));
        rightUpgrades.add(new Triple<String, String, Integer>("Water Pump", "Increases the rate at which water regenerates in the tower's tank.", 850));
        rightUpgrades.add(new Triple<String, String, Integer>("Electric Freezer", "Water tower now shoots ice that can freeze mooses.", 1800));
        rightUpgrades.add(new Triple<String, String, Integer>("Ice Vortex", "Generates additional wind that blows mooses back.", 4000));
    }
}
