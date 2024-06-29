package ISU;

import java.awt.*;

public class TinyGamerr extends Moose {
    public void buildMoose(TileMap map, int type, int dmg) {
        super.buildMoose(map, type, dmg);
        hp = 200;
        maxHp = 200;
    }

    public int getDamage() {
        return MooseData.getMooseDatas()[11].getDamage() * 4 + 200;
    }

    public void evolve(double delta) {
        evolution += delta;
        if (evolution + 1 >= path.length) { // subtract lives
            parent.removeLives(getDamage());
            destroy();
            return;
        }

        int wholePart = (int)evolution;
        double decimalPart = evolution - wholePart;
        Vector2 position = path[wholePart].lerp(path[wholePart + 1], decimalPart);

        hitbox = (new Rectangle((int)(position.getX() * 75) - 80, (int)(position.getY() * 75) - 10, 235, 95));
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
        int x = (int)hitbox.getMinX() + 50;
        int y = (int)hitbox.getMinY();
        g.setColor(new Color(0x008800));
        g.fillRect(x, y + 10, 185, 75);
        g.fillRect(x + 185, y + 35, 50, 25);
        g.setColor(new Color(0));
        g.fillRect(x, y, 40, 20);
        g.fillRect(x + 145, y, 40, 20);
        g.fillRect(x, y + 75, 40, 20);
        g.fillRect(x + 145, y + 75, 40, 20);
        g.drawImage(Game.getImages().get("gamerr.png"), x + 55, y + 10, 75, 75, null);
        g.drawImage(crackImages[(int)(5.0 * hp / maxHp)], x, y + 10, 185, 75, null);
    }

    public TinyGamerr(TileMap map, int type, int dmg) {
        super(map, type, dmg);
    }

    public TinyGamerr(TileMap map, int type, int dmg, double e) {
        super(map, type, dmg, e);
    }

    public int compareTo(Moose m) {
        if (m instanceof HugeGamerr || m instanceof DDT) {
            return -1;
        }
        if (m instanceof TinyGamerr) {
            return 0;
        }
        return 1;
    }
}
