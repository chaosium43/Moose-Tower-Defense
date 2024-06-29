package ISU;

import java.awt.Color;
// helper class for storing data related to difficulties
public class DifficultyData {
    private String name;
    private int lives;
    private int rounds;
    private int minPathLength;
    private double evolveSpeed;
    private Color colour;
    public String getName() {
        return name;
    }

    public int getLives() {
        return lives;
    }

    public int getRounds() {
        return rounds;
    }
    
    public int getMinPathLength() {
        return minPathLength;
    }

    public double getEvolveSpeed() {
        return evolveSpeed;
    }

    public Color getColour() {
        return colour;
    }

    public DifficultyData(String s, int i1, int i2, int i3, double d, Color c) {
        name = s;
        lives = i1;
        evolveSpeed = d;
        colour = c;
        rounds = i2;
        minPathLength = i3;
    }
}
