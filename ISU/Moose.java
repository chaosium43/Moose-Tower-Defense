package ISU;

import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
// primary enemy class
public class Moose implements Comparable<Moose> {
    protected int hp = 1;
    protected int maxHp = 1;
    protected double evolution = 0;
    protected Vector2[] path;
    protected TileMap parent;
    protected int mooseType;
    protected MooseData mooseData;
    protected static MooseData[] mooseDatas;
    protected long mooseID;
    protected static final Image image = Game.getImages().get("moose.png");
    protected static long totalMooses = 0;
    protected Rectangle hitbox;
    protected ConcurrentHashMap<String, Triple<Integer, Double, Double>> statusEffects;
    protected static final Image[] crackImages = {
        Game.getImages().get("cracks4.png"),
        Game.getImages().get("cracks3.png"),
        Game.getImages().get("cracks2.png"),
        Game.getImages().get("cracks1.png"),
        Game.getImages().get("cracks0.png"),
        Game.getImages().get("cracks0.png")
    };

    public boolean intersects(Rectangle r) {
        return hitbox.intersects(r);
    }

    public String[] getImmunities() {
        return mooseData.getImmunities();
    }

    public void render(Graphics2D g) {
        if (hitbox == null) {
            return;
        }
        int x = (int)hitbox.getMinX();
        int y = (int)hitbox.getMinY();
        switch (mooseType) {
            case 9: // zebra
                for (int i = 0; i < 5; i++) {
                    if (i % 2 == 0) {
                        g.setColor(new Color(0xffffff));
                    } else {
                        g.setColor(new Color(0));
                    }
                    g.fillRect(x, y + i * 15, 75, 15);
                }
                break;

            case 10: // rainbow
                g.setColor(new Color(0xff0000));
                g.fillRect(x, y, 75, 11);
                g.setColor(new Color(0xff8800));
                g.fillRect(x, y + 11, 75, 10);
                g.setColor(new Color(0xffff00));
                g.fillRect(x, y + 21, 75, 11);
                g.setColor(new Color(0x00ff00));
                g.fillRect(x, y + 32, 75, 11);
                g.setColor(new Color(0x00ffff));
                g.fillRect(x, y + 43, 75, 11);
                g.setColor(new Color(0x2222ff));
                g.fillRect(x, y + 54, 75, 10);
                g.setColor(new Color(0xff00ff));
                g.fillRect(x, y + 64, 75, 11);
                break;
            default:
                g.setColor(mooseData.getColour());
                g.fillRect(x, y, 75, 75);
                break;
        }
        g.drawImage(image, (int)hitbox.getMinX(), (int)hitbox.getMinY(), (int)hitbox.getWidth(), (int)hitbox.getHeight(), null);
        g.drawImage(crackImages[(int)(5.0 * hp / maxHp)], (int)hitbox.getMinX(), (int)hitbox.getMinY(), (int)hitbox.getWidth(), (int)hitbox.getHeight(), null);
        if (statusEffects.keySet().contains("freeze")) {
            g.drawImage(Game.getImages().get("snowflake.png"), (int)hitbox.getMinX(), (int)hitbox.getMinY(), (int)hitbox.getWidth(), (int)hitbox.getHeight(), null);
        } else if (statusEffects.keySet().contains("fire")) {
            g.drawImage(Game.getImages().get("fire.png"), (int)hitbox.getMinX(), (int)hitbox.getMinY(), (int)hitbox.getWidth(), (int)hitbox.getHeight(), null);
        }
    }

    // getters
    public long getMooseID() {
        return mooseID;
    }

    public double getEvolution() {
        return evolution;
    }

    public Vector2 getPosition() {
        return new Vector2(hitbox.getCenterX(), hitbox.getCenterY());
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public int getDamage() {
        return mooseData.getDamage();
    }

    public ConcurrentHashMap<String, Triple<Integer, Double, Double>> getStatusEffects() {
        return statusEffects;
    }

    // helper method that damages mooses by certain amount
    public void damage(int dmg, Projectile p) { 
        if (hp > dmg) { 
            hp -= dmg;
            if (p != null) {
                p.addTouchedMoose(this);
            }
        } else { // spawn children
            double randomOffset = 0;
            Game.getParallelAudios().get("pop.wav").play();
            dmg -= hp;
            int moneyAcc = 0;
            Queue<Triple<Integer, Integer, Integer>> children = new LinkedList<Triple<Integer, Integer, Integer>>();
            children.offer(new Triple<Integer, Integer, Integer>(mooseType, 0, dmg));

            while (!children.isEmpty()) {
                Triple<Integer, Integer, Integer> front = children.poll();
                MooseData md = mooseDatas[front.getLeft()];
                if (front.getRight() >= front.getMiddle()) { // children recursively get destroyed
                    moneyAcc++;
                    int[] childTypes = md.getChild();
                    int[] childCount = md.getNChildren();

                    for (int i = 0; i < childTypes.length; i++) {
                        for (int j = 0; j < childCount[i]; j++) {
                            int childType = childTypes[i];
                            MooseData childData = mooseDatas[childType];
                            children.offer(new Triple<Integer, Integer, Integer>(childType, childData.getHp(), front.getRight() - front.getMiddle()));
                        }
                    }
                } else { // spawn in child
                    Moose child = new Moose(parent, front.getLeft(), front.getRight(), evolution - randomOffset);
                    randomOffset = Math.random();
                    for (String effect: statusEffects.keySet()) { // replicate effects on children
                        if (!effect.equals("freeze")) { // freeze does not apply to children
                            child.addStatusEffect(effect, statusEffects.get(effect));
                        }
                    }
                    if (p != null) {
                        p.addTouchedMoose(child);
                    }
                }
            }

            parent.addMoney(moneyAcc);
            destroy();
        }
    }

    public void destroy() { // moose destruction code
        parent.removeMoose(mooseID);
    }

    public void evolve(double delta) { // moves the moose forward in time by delta seconds
        for (String effect: statusEffects.keySet()) { // handling effects
            Triple<Integer, Double, Double> effectData = statusEffects.get(effect);
            int effectUses = effectData.getLeft();
            double effectEvolution = effectData.getRight();
            double effectDelay = effectData.getMiddle();
            effectEvolution -= delta;
            if (effectEvolution <= 0) {
                effectUses -= 1;
                effectEvolution += effectDelay;
                statusEffects.put(effect, new Triple<Integer, Double, Double>(effectUses, effectDelay, effectEvolution));

                if (effect.equals("fire")) { // doing fire damage
                    damage(1, null);
                }

                if (hp <= 0) { // moose no longer exists
                    break;
                }

                if (effectUses <= 0) { // effect is no longer in use
                    statusEffects.remove(effect);
                    continue;
                }
            } else {
                statusEffects.put(effect, new Triple<Integer, Double, Double>(effectUses, effectDelay, effectEvolution));
            }
        }


        if (statusEffects.keySet().contains("stun") || statusEffects.keySet().contains("freeze")) { // moose should not move while these effects exist
            return;
        }

        evolution += delta * mooseData.getSpeed();
        if (evolution + 1 >= path.length) { // subtract lives
            parent.removeLives(getDamage());
            destroy();
            return;
        }

        int wholePart = (int)evolution;
        if (wholePart < 0) { // sANityCHeCK
            wholePart = 0;
        }
        double decimalPart = evolution - wholePart;
        Vector2 position = path[wholePart].lerp(path[wholePart + 1], decimalPart);

        hitbox = (new Rectangle((int)(position.getX() * 75), (int)(position.getY() * 75), 75, 75));
    }

    public void addStatusEffect(String name, Triple<Integer, Double, Double> effect) {
        if (effect.getLeft() <= 0) {
            return;
        }
        if (name.equals("fire") && statusEffects.keySet().contains("fire")) { // don't want fire damage to be lost when applying new effect
            Triple<Integer, Double, Double> oldEffect = statusEffects.get("fire");
            statusEffects.put("fire", new Triple<Integer, Double, Double>(Math.max(oldEffect.getLeft(), effect.getLeft()), Math.min(oldEffect.getMiddle(), effect.getMiddle()), Math.min(oldEffect.getRight(), effect.getRight())));
            return;
        }
        statusEffects.put(name, effect);
        if (name.equals("freeze") && statusEffects.keySet().contains("fire")) {
            statusEffects.remove("fire");
        } else if (name.equals("fire") && statusEffects.keySet().contains("freeze")) {
            statusEffects.remove("freeze");
        }
    }

    public void blowback(double delta) { // sets the moose's evolution back delta seconds
        evolution = Math.max(0, evolution - delta);
    }

    public int compareTo(Moose m) {
        if (m instanceof MiniBlimp || m instanceof TinyGamerr || m instanceof HugeGamerr || m instanceof DDT) {
            return -1;
        }
        return Integer.compare(mooseType, m.mooseType);
    }

    public void buildMoose(TileMap map, int type, int dmg) {
        statusEffects = new ConcurrentHashMap<String, Triple<Integer, Double, Double>>();
        mooseType = type;
        parent = map;
        mooseData = mooseDatas[type];
        hp = mooseData.getHp();
        maxHp = hp;
        path = parent.getPath();
        damage(dmg, null);
        mooseID = totalMooses++;
        mooseType = type;
        parent.addMoose(mooseID, this);
        evolve(0);
    }

    public Moose(TileMap map, int type, int dmg) {
        if (mooseDatas == null) {
            mooseDatas = MooseData.getMooseDatas();
        }

        buildMoose(map, type, dmg);
    }

    public Moose(TileMap map, int type, int dmg, double e) {
        evolution = e;
        buildMoose(map, type, dmg);
    }

    public int hashCode() {
        return Long.hashCode(mooseID);
    }

    public String toString() {
        return Long.toString(mooseID);
    }
}
