package ISU;

import java.awt.*;
import java.util.*;

public class GunnerTower extends Tower {
    private static double[] delays = {1, 0.5, 0.5, 0.2, 0.05};
    private static int[] pierces = {1, 2, 2, 2, 10};
    private static int[] damages = {1, 1, 2, 2, 10};
    private static double[] speeds = {500, 500, 1000, 1000, 1000};

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
                    for (int i = -2; i < 3; i++) {
                        new Projectile(new Rectangle((int)hb.getCenterX() - 5, (int)hb.getCenterY() - 5, 10, 10), new Color(0xffff00), parent, pierces[rightPath], damages[rightPath], 1.0, new Vector2(Math.cos(rotation + i * 0.2), Math.sin(rotation + i * 0.2)).multiply(speeds[leftPath]));
                    }
                    break;
                case 4:
                    new Projectile(new Rectangle((int)hb.getCenterX() - 20, (int)hb.getCenterY() - 20, 40, 40), new Color(0xffff00), parent, pierces[rightPath], damages[rightPath], 1.0, direction.multiply(speeds[leftPath]));
                    break;
                default:
                    new Projectile(new Rectangle((int)hb.getCenterX() - 5, (int)hb.getCenterY() - 5, 10, 10), new Color(0xffff00), parent, pierces[rightPath], damages[rightPath], 1.0, direction.multiply(speeds[leftPath]));
                    break;
            }
            evolution = 0;
            trigger = false;
        }
        target = null;
    }

    public String getName() {
        return "Gunner";
    }
    
    public GunnerTower(Vector2 v, TileMap t) {
        super(v, t);
        parent.removeMoney(200);
        setImage(Game.getImages().get("gunner.png"));
        moneySpent += 200;
        range = new Rectangle((int)(v.getX() - 2) * 75, (int)(v.getY() - 2) * 75, 375, 375);
        leftPath = 0;
        rightPath = 0;
        leftUpgrades = new ArrayList<Triple<String, String, Integer>>();
        rightUpgrades = new ArrayList<Triple<String, String, Integer>>();

        leftUpgrades.add(new Triple<String, String, Integer>("Dualies", "Two pistols for twice the fire power.", 300));
        leftUpgrades.add(new Triple<String, String, Integer>("Rifling", "Makes bullet travel faster.", 100));
        leftUpgrades.add(new Triple<String, String, Integer>("AK47", "Fully automatic killing machine.", 800));
        leftUpgrades.add(new Triple<String, String, Integer>("Gatling Gun", "\"This is my weapon.\" -Heavy", 3000));
        rightUpgrades.add(new Triple<String, String, Integer>("Collateral Damage", "Doubles the pierce of the tower.", 140));
        rightUpgrades.add(new Triple<String, String, Integer>("Heavy Rounds", "Can destroy two moose instead of one.", 500));
        rightUpgrades.add(new Triple<String, String, Integer>("Shotgun", "Shoots five bullets in one volley.", 600));
        rightUpgrades.add(new Triple<String, String, Integer>("Slug", "Shoots a highly potent slug round instead of a shotgun volley.", 2700));
    }
}
