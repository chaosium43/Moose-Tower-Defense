package ISU;

import java.awt.*;

public class DDT extends Moose {
    private static String[] immunities = {"explosive", "sharp"};
    public void buildMoose(TileMap map, int type, int dmg) {
        super.buildMoose(map, type, dmg);
        hp = 400;
        maxHp = 400;
    }


    public int getDamage() {
        return MooseData.getMooseDatas()[11].getDamage() * 4 + 400;
    }

    public void evolve(double delta) {
        evolution += delta * 3;
        if (evolution + 1 >= path.length) { // subtract lives
            parent.removeLives(getDamage());
            destroy();
            return;
        }

        int wholePart = (int)evolution;
        double decimalPart = evolution - wholePart;
        Vector2 position = path[wholePart].lerp(path[wholePart + 1], decimalPart);

        hitbox = (new Rectangle((int)(position.getX() * 75) - 60, (int)(position.getY() * 75) - 20, 195, 115));
    }

    public void damage(int dmg, Projectile p) {
        if (hp > dmg) {
            hp -= dmg;
        } else {
            double randomOffset = 0;
            Game.getParallelAudios().get("pop.wav").play();
            for (int i = 0; i < 4; i++) { // spawn the three children in
                Moose child = new Moose(parent, 11, 0, evolution - randomOffset);
                randomOffset = Math.random();
                if (p != null) {
                    p.addTouchedMoose(child);
                }
            }
            parent.addMoney(1);
            destroy();
        }
    }

    public void render(Graphics2D g) {
        g.drawImage(Game.getImages().get("ddt.png"), (int)hitbox.getMinX(), (int)(hitbox.getMinY()), 195, 115, null);
    }

    public DDT(TileMap map, int type, int dmg) {
        super(map, type, dmg);
    }
    public DDT(TileMap map, int type, int dmg, double e) {
        super(map, type, dmg, e);
    }

    public int compareTo(Moose m) {
        if (m instanceof DDT) {
            return 0;
        }
        return 1;
    }
}
