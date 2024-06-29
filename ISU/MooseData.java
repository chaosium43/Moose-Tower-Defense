package ISU;

import java.awt.*;

public class MooseData { // helper class for 
    private int[] child; // moose type that spawns once moose is defeated
    private int[] nChildren; // # of children that spawn once moose is taken down
    private Color colour; // colour of moose
    private int hp; // how much damage needs to be done to the moose to take it down
    private String[] immunities; // different immunities the moose has
    private double speed; // how fast the moose will move/evolve
    private int damage; // amount of damage the moose does once it reaches the end of the track
    private static String[] blackImmunity = {"explosive"};
    private static String[] whiteImmunity = {"freeze"};
    private static String[] leadImmunity = {"sharp"};
    private static String[] arcticImmunity = {"nonsharp"};
    private static String[] zebraImmunity = {"explosive", "freeze"};
    private static String[] defaultImmunity = {};
    private static MooseData[] datas;

    // getters
    public int[] getChild() {
        return child;
    }

    public int[] getNChildren() {
        return nChildren;
    }

    public int getHp() {
        return hp;
    }

    public Color getColour() {
        return colour;
    }

    public String[] getImmunities() {
        return immunities;
    }

    public double getSpeed() {
        return speed;
    }

    public int getDamage() {
        return damage;
    }

    public void computeDamage() {
        damage = hp;
        for (int i = 0; i < child.length; i++) {
            damage += datas[child[i]].damage * nChildren[i];
        }
    }

    public static MooseData[] getMooseDatas() { // returns all the mooses that exist in this game
        if (datas == null) {
            datas = new MooseData[12];

            // red
            int[] c1 = {1};
            int[] c2 = {0};
            datas[0] = new MooseData(c1, c2, 1, new Color(0xff0000), defaultImmunity, 1.5);
            
            // blue
            int[] c3 = {0};
            int[] c4 = {1};
            datas[1] = new MooseData(c3, c4, 1, new Color(0x4444ff), defaultImmunity, 1.9);

            // green
            int[] c5 = {1};
            int[] c6 = {1};
            datas[2] = new MooseData(c5, c6, 1, new Color(0x00ff00), defaultImmunity, 2.25);

            // yellow
            int[] c7 = {2};
            int[] c8 = {1};
            datas[3] = new MooseData(c7, c8, 1, new Color(0xffff00), defaultImmunity, 2.6);

            // pink
            int[] c9 = {3};
            int[] c10 = {1};
            datas[4] = new MooseData(c9, c10, 1, new Color(0xff8888), defaultImmunity, 3);

            // black and white
            int[] c11 = {4};
            int[] c12 = {2};
            datas[5] = new MooseData(c11, c12, 1, new Color(0), blackImmunity, 2);
            datas[6] = new MooseData(c11, c12, 1, new Color(0xffffff), whiteImmunity, 2);

            // lead
            int[] c13 = {5};
            int[] c14 = {2};
            datas[7] = new MooseData(c13, c14, 1, new Color(0x444444), leadImmunity, 1);

            // arctic
            int[] c15 = {6};
            int[] c16 = {2};
            datas[8] = new MooseData(c15, c16, 1, new Color(0x00ffff), arcticImmunity, 2.25);

            // zebra
            int[] c17 = {5, 6};
            int[] c18 = {1, 1};
            datas[9] = new MooseData(c17, c18, 1, new Color(0x8888ff), zebraImmunity, 2.25);

            // rainbow
            int[] c19 = {9};
            int[] c20 = {2};
            datas[10] = new MooseData(c19, c20, 1, new Color(0x8888ff), defaultImmunity, 2);

            // ceramic
            int[] c21 = {10};
            int[] c22 = {2};
            datas[11] = new MooseData(c21, c22, 10, new Color(0x884400), defaultImmunity, 1.8);
        }

        for (MooseData d: datas) { // precomputation so good
            d.computeDamage();
        }
        return datas;
    }

    public MooseData(int[] i1, int[] i2, int i3, Color c, String[] sa, double d) {
        child = i1;
        nChildren = i2;
        hp = i3;
        colour = c;
        immunities = sa;
        speed = d;
    }
}
