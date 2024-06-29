package ISU;

// helper class for storing stats about a tower
public class TowerData {
    private int cost;
    private String icon;
    private Class towerClass;
    private int range;
    public int getCost() {
        return cost;
    }
    public String getIcon() {
        return icon;
    }
    public int getRange() {
        return range;
    }
    public Class getTowerClass() {
        return towerClass;
    }
    public TowerData(int i, String s, Class c, int r) {
        cost = i;
        icon = s;
        towerClass = c;
        range = r;
    }
}
