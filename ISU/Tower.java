package ISU;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;

public class Tower extends ButtonComponent {
    protected int leftPath;
    protected int rightPath;
    protected ArrayList<Triple<String, String, Integer>> leftUpgrades;
    protected ArrayList<Triple<String, String, Integer>> rightUpgrades;
    protected static String[] targetingModes = {"First", "Last", "Close", "Strong"};
    protected double evolution;
    protected int moneySpent; // how much money has been spent on this tower
    protected TileMap parent;
    protected Moose target; // current moose that is being targeted
    protected Rectangle range; // the range hitbox
    private long towerID;
    private long totalTowers = 0;
    private int currentMode = 0;
    private int x;
    private int y;
    protected double rotation = 0;
    protected boolean trigger; // allows the tower to fire immediately at the start of the round

    public int leftCost() { // returns the cost of the left path upgrade
        if (leftPath == 4 || leftPath + rightPath == 6) {
            return 0;
        }
        if (leftPath == 2 && rightPath == 3) {
            return leftUpgrades.get(2).getRight() * 2 + rightUpgrades.get(2).getRight();
        }
        return leftUpgrades.get(leftPath).getRight();
    }

    public int rightCost() { // returns the cost of the right path upgrade
        if (rightPath == 4 || leftPath + rightPath == 6) {
            return 0;
        }
        if (leftPath == 3 && rightPath == 2) {
            return leftUpgrades.get(2).getRight() + rightUpgrades.get(2).getRight() * 2;
        }
        return rightUpgrades.get(rightPath).getRight();
    }

    public String upgradeNameLeft() {
        if (leftPath == 4) {
            return "";
        }
        return leftUpgrades.get(leftPath).getLeft();
    }

    public String upgradeNameRight() {
        if (rightPath == 4) {
            return "";
        }
        return rightUpgrades.get(rightPath).getLeft();
    }

    public String upgradeDescriptionLeft() {
        if (leftPath == 4) {
            return "";
        }
        return leftUpgrades.get(leftPath).getMiddle();
    }

    public String upgradeDescriptionRight() {
        if (rightPath == 4) {
            return "";
        }
        return rightUpgrades.get(rightPath).getMiddle();
    }

    public int getSellCost() {
        return (int)(moneySpent * 0.75);
    }

    public int currentModeID() {
        return currentMode;
    }

    public String currentMode() {
        return targetingModes[currentMode];
    }

    public String getName() {
        return "Tower";
    }

    public Rectangle getRange() {
        return range;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLeftPath() {
        return leftPath;
    }

    public int getRightPath() {
        return rightPath;
    }

    public int getMoneySpent() {
        return moneySpent;
    }

    public void sell() {
        parent.addMoneyFull(getSellCost());
        parent.updateMap(x, y, 0);
        destroy();
    }

    public void toggleMode() {
        currentMode++;
        currentMode %= targetingModes.length;
    }

    public void setMode(int mode) {
        currentMode = mode;
    }

    public boolean upgradeLeft() {
        if (leftPath == 4 || leftPath + rightPath == 6) {
            return false;
        }
        int cost = leftCost();
        if (parent.getMoney() >= cost) {
            leftPath++;
            parent.removeMoney(cost);
            moneySpent += cost;
            return true;
        }
        return false;
    }
    public boolean upgradeRight() {
        if (rightPath == 4 || leftPath + rightPath == 6) {
            return false;
        }
        int cost = rightCost();
        if (parent.getMoney() >= cost) {
            rightPath++;
            parent.removeMoney(cost);
            moneySpent += cost;
            return true;
        }
        return false;
    }

    public void setLeftPath(int i) {
        leftPath = i;
    }

    public void setRightPath(int i) {
        rightPath = i;
    }

    public void setMoneySpent(int i) {
        moneySpent = i;
    }

    public void setTarget(Moose m) {
        if (m == null) {
            target = null;
            return;
        }
        
        if (target == null) {
            target = m;
            return;
        }

        switch (currentMode) {
            case 0: // first
                if (m.getEvolution() > target.getEvolution()) {
                    target = m;
                }
                break;
            case 1: // last
                if (m.getEvolution() < target.getEvolution()) {
                    target = m;
                }
                break;
            case 2: // close
                Rectangle hb = getHitbox();
                double oldDistance = new Vector2(hb.getCenterX(), hb.getCenterY()).subtract(target.getPosition()).magnitude();
                double newDistance = new Vector2(hb.getCenterX(), hb.getCenterY()).subtract(m.getPosition()).magnitude();
                if (newDistance < oldDistance) {
                    target = m;
                }
                break;
            case 3: // strong
                if (target.compareTo(m) > 0) {
                    target = m;
                }
                break;
        }
    }

    public void replenishTrigger() {
        trigger = true;
        target = null;
    }
    public void evolve(double delta) {
    }

    public void destroy() {
        super.destroy();
        parent.removeTower(this);
    }

    public void render(Graphics2D g) {
        Rectangle hitbox = getHitbox();
        AffineTransform original = g.getTransform();
        g.translate(hitbox.getCenterX(), hitbox.getCenterY());
        g.rotate(rotation);
        g.drawImage(getImage(), -(int)(hitbox.getWidth() / 2), -(int)(hitbox.getHeight() / 2), 75, 75, null);
        g.setTransform(original);
    }

    public boolean inRange(Moose m) { // returns whether a moose is in the range of a tower
        return m.intersects(range);
    }

    public void onPressed() {
        parent.setUpgradingTower(this);
    }

    public Tower(Vector2 v, TileMap t) {
        super(new Rectangle((int)v.getX() * 75, (int)v.getY() * 75, 75, 75), new Color(0));
        x = (int)v.getX();
        y = (int)v.getY();
        evolution = 0;
        leftPath = 0;
        rightPath = 0;
        moneySpent = 0;
        parent = t;
        towerID = totalTowers++;
        parent.updateMap(x, y, 2);
    }

    public int hashCode() {
        return Long.hashCode(towerID);
    }
}
