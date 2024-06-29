package ISU;

import java.awt.*;

public class MiniBlimp extends Moose {
    public int getDamage() {
        return mooseData.getDamage() * 3 + hp;
    }
    
    public void render(Graphics2D g) {
        int x = (int)hitbox.getMinX() + 50;
        int y = (int)hitbox.getMinY();
        int[] xPoints = {x - 50, x, x, x - 50};
        int[] yPoints = {y, y + 25, y + 70, y + 95};
        switch (mooseType) {
            case 9: // zebra
                for (int i = 0; i < 5; i++) {
                    if (i % 2 == 0) {
                        g.setColor(new Color(0xffffff));
                    } else {
                        g.setColor(new Color(0));
                    }
                    g.fillRect(x, y + i * 19, 105, 19);
                }
                g.fillPolygon(xPoints, yPoints, 4);
                break;

            case 10: // rainbow
                g.setColor(new Color(0xff0000));
                g.fillRect(x, y, 105, 14);
                g.setColor(new Color(0xff8800));
                g.fillRect(x, y + 14, 105, 13);
                g.setColor(new Color(0xffff00));
                g.fillRect(x, y + 27, 105, 14);
                g.setColor(new Color(0x00ff00));
                g.fillRect(x, y + 41, 105, 13);
                g.setColor(new Color(0x00ffff));
                g.fillRect(x, y + 54, 105, 14);
                g.setColor(new Color(0x2222ff));
                g.fillRect(x, y + 68, 105, 13);
                g.setColor(new Color(0xff00ff));
                g.fillRect(x, y + 81, 105, 14);
                g.setColor(new Color(0xff8888));
                g.fillPolygon(xPoints, yPoints, 4);
                break;
            default:
                g.setColor(mooseData.getColour());
                g.fillRect(x, y, 105, 95);
                g.fillPolygon(xPoints, yPoints, 4);
                break;
        }
        g.drawImage(crackImages[(int)(5.0 * hp / maxHp)], x, y, 105, 95, null);
    }

    public void damage(int dmg, Projectile p) {
        if (hp > dmg) {
            hp -= dmg;
        } else {
            double randomOffset = 0;
            Game.getParallelAudios().get("pop.wav").play();
            for (int i = 0; i < 3; i++) { // spawn the three children in
                Moose child = new Moose(parent, mooseType, 0, evolution - randomOffset);
                randomOffset = Math.random();
                if (p != null) {
                    p.addTouchedMoose(child);
                }
            }
            parent.addMoney(1);
            destroy();
        }
    }

    public void evolve(double delta) {
        evolution += delta * mooseData.getSpeed() * 0.5;
        if (evolution + 1 >= path.length) { // subtract lives
            parent.removeLives(getDamage());
            destroy();
            return;
        }

        int wholePart = (int)evolution;
        double decimalPart = evolution - wholePart;
        Vector2 position = path[wholePart].lerp(path[wholePart + 1], decimalPart);

        hitbox = (new Rectangle((int)(position.getX() * 75) - 40, (int)(position.getY() * 75) - 10, 155, 95));
    }

    public void buildMoose(TileMap map, int type, int dmg) {
        super.buildMoose(map, type, dmg);
        hp = mooseData.getDamage() * 2;
        maxHp = hp;
    }
    public MiniBlimp(TileMap map, int type, int dmg) {
        super(map, type, dmg);
    }
    
    public MiniBlimp(TileMap map, int type, int dmg, double e) {
        super(map, type, dmg, e);
    }

    public int compareTo(Moose m) {
        if (m instanceof TinyGamerr || m instanceof HugeGamerr || m instanceof DDT) {
            return -1;
        }
        if (m instanceof MiniBlimp) {
            return Integer.compare(mooseType, m.mooseType);
        }
        return 1;
    }
}
