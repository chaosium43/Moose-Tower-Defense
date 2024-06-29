package ISU;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.*;
import java.io.*;
import java.awt.image.*;

// handles everything to do with the map once the main game starts
public class TileMap extends ButtonComponent implements KeyListener, MouseMotionListener {
    private static double moneyDecay = 1.03;
    private double fillBuffer = 0;
    private Queue<RenderComponent> components;
    private int money = 500;
    private int lives;
    private DifficultyData difficulty;
    private String[] towerChoices;
    private boolean paused;
    private int gameSpeed = 1;
    private int round = 1;
    private int[][] map; // stores data about the map
    private Vector2[] moosePath; // path the mooses need to take from the start to the finish
    private RenderComponent moneyText;
    private RenderComponent livesText;
    private ButtonComponent startButton;
    private boolean roundPlaying = false;
    private ConcurrentHashMap<Long, Moose> mooses;
    private ConcurrentHashMap<Projectile, Boolean> projectiles;
    private HashSet<Integer> pressedKeys;
    private Round roundHandler;
    private TowerData selectedTower;
    private ConcurrentHashMap<Tower, Boolean> towers; // why can java NOT GIVE ME CONCURRENT HASH SETS >:(
    private Vector2 mousePos;
    private HashMap<String, TowerData> towerStats;
    private LinkedList<RenderComponent> bottomGui;
    private LinkedList<RenderComponent> upgradeGui;
    private RenderComponent roundsText;
    private RenderComponent commentaryText;
    private Tower upgradingTower;
    private ButtonComponent leftUpgradeButton;
    private ButtonComponent rightUpgradeButton;
    private ButtonComponent sellButton;
    private ButtonComponent modeButton;
    private RenderComponent towerDescription;
    private RenderComponent towerIcon;
    private RenderComponent upgradeDescription;
    private File saveFile;
    private boolean loaded = false;

    public Vector2[] getPath() {
        return moosePath;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int i) {
        fillBuffer += i;
        double bufferRequirement = Math.pow(moneyDecay, Math.max(0, round - 20));
        int overflow = (int)(fillBuffer / bufferRequirement);
        money += overflow;
        fillBuffer -=  overflow * bufferRequirement;
    }

    public void addMoneyFull(int i) {
        money += i;
    }

    public void removeMoney(int i) {
        money -= i;
    }

    public void addMoose(long i, Moose m) {
        mooses.put(i, m);
    }

    public void removeMoose(long i) {
        mooses.remove(i);
    }

    public void addProjectile(Projectile p) {
        projectiles.put(p, true);
    }

    public void removeProjectile(Projectile p) {
        projectiles.remove(p);
    }

    public void removeLives(int i) {
        lives -= i;
        if (lives <= 0) { // send player to the game over screen once lives drop to 0
            Game.gameOverScreen(difficulty, round);
        }
    }

    public void removeTower(Tower t) {
        towers.remove(t);
    }

    public void updateMap(int x, int y, int v) {
        map[x][y] = v;
    }

    public void updateUpgradeGui() {
        towerIcon.setImage(upgradingTower.getImage());
        towerDescription.setText(upgradingTower.getName());
        modeButton.setText("Targeting: " + upgradingTower.currentMode());
        sellButton.setText("Sell: $" + upgradingTower.getSellCost());
        int leftCost = upgradingTower.leftCost();
        int rightCost = upgradingTower.rightCost();
        if (leftCost == 0) {
            leftUpgradeButton.setColour(new Color(0x888888));
            leftUpgradeButton.setText("Maximum\nUpgrades");
        } else {
            leftUpgradeButton.setText(upgradingTower.upgradeNameLeft() + "\n$" + leftCost);
            if (leftCost > money) {
                leftUpgradeButton.setColour(new Color(0xff0000));
            } else {
                leftUpgradeButton.setColour(new Color(0x008800));
            }
        }

        if (rightCost == 0) {
            rightUpgradeButton.setColour(new Color(0x888888));
            rightUpgradeButton.setText("Maximum\nUpgrades");
        } else {
            rightUpgradeButton.setText(upgradingTower.upgradeNameRight() + "\n$" + rightCost);
            if (rightCost > money) {
                rightUpgradeButton.setColour(new Color(0xff0000));
            } else {
                rightUpgradeButton.setColour(new Color(0x008800));
            }
        }

        Rectangle mouseBox = new Rectangle((int)mousePos.getX(), (int)mousePos.getY(), 1, 1);
        if (leftUpgradeButton.intersects(mouseBox)) {
            upgradeDescription.setText(upgradingTower.upgradeDescriptionLeft());
        } else if (rightUpgradeButton.intersects(mouseBox)) {
            upgradeDescription.setText(upgradingTower.upgradeDescriptionRight());
        } else {
            upgradeDescription.setText("");
        }
    }

    public void writeMap(int[][] m, Vector2[] path, Vector2 head, Vector2 tail) { // writes data to the map
        map = m;
        moosePath = path;
    }

    public void setUpgradingTower(Tower t) { // sets what tower is currently being upgraded self explanatory
        upgradingTower = t;
        if (upgradingTower == null) {
            for (RenderComponent r: bottomGui) {
                r.setVisible(true);
            }
            for (RenderComponent r: upgradeGui) {
                r.setVisible(false);
            }
        } else {
            for (RenderComponent r: bottomGui) {
                r.setVisible(false);
            }
            for (RenderComponent r: upgradeGui) {
                r.setVisible(true);
            }
            updateUpgradeGui();
        }
    }

    public void render(Graphics2D g) {
        if (!loaded) {
            return;
        }
        if (upgradingTower != null) {
            updateUpgradeGui();
        }
        for (int x = 0; x < 14; x++) { // drawing out all the tiles of the map
            for (int y = 0; y < 8; y++) {
                switch (map[x][y]) {
                    case 0: // free tile
                        g.setColor(new Color(0xffffff));
                        g.fill(new Rectangle(x * 75, y * 75, 75, 75));
                        g.setColor(new Color(0x888888));
                        g.fill(new Rectangle(x * 75 + 10, y * 75 + 10, 55, 55));
                        break;
                    case 1: // path tile
                        g.setColor(new Color(0x0000ff));
                        g.fill(new Rectangle(x * 75, y * 75, 75, 75));
                        break;
                    case 2: // tower tile
                        g.setColor(new Color(0xffffff));
                        g.fill(new Rectangle(x * 75, y * 75, 75, 75));
                        break;
                    case 3: // head tile
                        g.setColor(new Color(0x00ff00));
                        g.fill(new Rectangle(x * 75, y * 75, 75, 75));
                        break;
                    case 4: // tail tile
                        g.setColor(new Color(0xff0000));
                        g.fill(new Rectangle(x * 75, y * 75, 75, 75));
                        break;
                }
            }
        }

        // updating stats counters
        moneyText.setText("$" + money);
        livesText.setText(lives + "L");

        // drawing all mooses
        for (Moose m: mooses.values()) {
            m.render(g);
        }

        // drawing selected tower if it exists
        if (selectedTower != null) {
            Vector2 pos = mousePos.divide(75);
            int x = (int)pos.getX();
            int y = (int)pos.getY();

            if (x >= 0 && x < 14 && y >= 0 && y < 8) {
                Image img = (Image)Game.getImages().get(selectedTower.getIcon());
                g.drawImage(img, x * 75, y * 75, 75, 75, null);
                g.setStroke(new BasicStroke(10));

                if (map[x][y] == 0 && money >= selectedTower.getCost()) {
                    g.setColor(new Color(0x00ff00));
                } else {
                    g.setColor(new Color(0xff0000));
                }

                int rangeBoxDim = 150 * selectedTower.getRange() + 65;
                int boxX = -75 * selectedTower.getRange() + x * 75 + 5;
                int boxY = -75 * selectedTower.getRange() + y * 75 + 5;
                g.draw(new Rectangle(boxX, boxY, rangeBoxDim, rangeBoxDim));
            }
        }
    }

    public void destroy() {
        Game.getWindow().removeKeyListener(this);
        Game.currentGame().removeMouseMotionListener(this);
        while (!components.isEmpty()) {
            components.poll().destroy();
        }
        Game.destroyCurrentMap();
    }

    public void saveGame() throws IOException { // saves the game to current save file
        if (!saveFile.exists()) {
            saveFile.createNewFile();
        }
        PrintWriter p = new PrintWriter(saveFile);
        for (String s: towerChoices) { // prints tower choices
            p.println(s);
        }
        switch (difficulty.getName()) { // print difficulty index
            case "Easy":
                p.println(0);
                break;
            case "Medium":
                p.println(1);
                break;
            case "Hard":
                p.println(2);
                break;
                
        }

        // print game stats
        p.println(money);
        p.println(lives);
        p.println(round);

        // print map
        for (int x = 0; x < 14; x++) {
            for (int y = 0; y < 8; y++) {
                p.println(map[x][y]);
            }
        }

        // print moose path
        p.println(moosePath.length);
        for (Vector2 v: moosePath) {
            p.println(v.getX());
            p.println(v.getY());
        }

        p.println(towers.size());

        // print towers
        for (Tower t: towers.keySet()) {
            p.println(t.getName());
            p.println(t.getX());
            p.println(t.getY());
            p.println(t.getLeftPath());
            p.println(t.getRightPath());
            p.println(t.getMoneySpent());
            p.println(t.currentModeID());
        }

        p.close();
    }

    private void buildGame() { // creates the bonus components needed for the game to work properly
        components = new LinkedList<RenderComponent>();
        mooses = new ConcurrentHashMap<Long, Moose>();
        projectiles = new ConcurrentHashMap<Projectile, Boolean>();
        pressedKeys = new HashSet<Integer>();
        bottomGui = new LinkedList<RenderComponent>();
        upgradeGui = new LinkedList<RenderComponent>();
        roundHandler = new Round(this);
        
        ButtonComponent towersBg = new ButtonComponent(new Rectangle(1050, 0, 230, 720), new Color(0x884400)) {
            public void onPressed() {
                selectedTower = null;
            }
        };
        components.offer(towersBg);
        RenderComponent roundsBg = new RenderComponent(new Rectangle(0, 600, 1050, 120), new Color(0x444444));
        components.offer(roundsBg);

        moneyText = new RenderComponent(new Rectangle(1060, 0, 220, 50), new Color(0xffff00));
        moneyText.setBackgroundVisible(false);
        moneyText.setFont(new Font("Courier New", Font.PLAIN, 40));
        moneyText.setTextColour(new Color(0xffff00));
        moneyText.setTextAnchor(new Vector2(0, 0.5));
        components.offer(moneyText);

        livesText = new RenderComponent(new Rectangle(1060, 50, 220, 50), new Color(0xffaaaa));
        livesText.setBackgroundVisible(false);
        livesText.setFont(new Font("Courier New", Font.PLAIN, 40));
        livesText.setTextColour(new Color(0xff8888));
        livesText.setTextAnchor(new Vector2(0, 0.5));
        components.offer(livesText);

        towerStats = Game.getTowerStats();
        HashMap<String, BufferedImage> images = Game.getImages();

        for (int i = 1; i <= 4; i++) {
            TowerData data = towerStats.get(towerChoices[i]);

            RenderComponent label = new RenderComponent(new Rectangle(1135, 15 + i * 85, 140, 75), new Color(0xffffff));
            label.setFont(new Font("Courier New", Font.BOLD, 50));
            label.setText("$" + data.getCost());
            label.setTextColour(new Color(0));
            components.offer(label);

            ButtonComponent button = new ButtonComponent(new Rectangle(1060, 15 + i * 85, 75, 75), new Color(0xffffff)) {
                public void onPressed() {
                    selectedTower = data;
                }
            };
            button.setImage(images.get(data.getIcon()));
            components.offer(button);
        }

        startButton = new ButtonComponent(new Rectangle(1060, 465, 210, 75), new Color(0xffffff)) {
            public void onPressed() {
                if (roundPlaying) {
                    if (gameSpeed == 1) {
                        gameSpeed = 2;
                    } else {
                        gameSpeed = 1;
                    }
                    setText("" + gameSpeed + "x");
                } else {
                    roundHandler.startRound(round);
                    setText("" + gameSpeed + "x");
                    roundPlaying = true;
                    roundsText.setText("Round " + round + " of " + difficulty.getRounds());
                    for (Tower t: towers.keySet()) { // make sure all towers can fire as soon as round starts
                        t.replenishTrigger();
                    }
                }
            }
        };
        startButton.setText("Start");
        startButton.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        components.offer(startButton);

        ButtonComponent pauseButton = new ButtonComponent(new Rectangle(1060, 550, 210, 75), new Color(0xffffff)) {
            public void onPressed() {
                if (roundPlaying) {
                    paused = !paused;
                    if (paused) {
                        setText("Unpause");
                    } else {
                        setText("Pause");
                    }
                }
            }
        };
        pauseButton.setText("Pause");
        pauseButton.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        components.offer(pauseButton);

        ButtonComponent exitButton = new ButtonComponent(new Rectangle(1060, 635, 210, 75), new Color(0xffffff)) {
            public void onPressed() {
                Game.loadMainMenu();
            }
        };
        exitButton.setText("Exit");
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 50));
        components.offer(exitButton);

        roundsText = new RenderComponent(new Rectangle(0, 600, 1040, 110), new Color(0x444444));
        roundsText.setBackgroundVisible(false);
        roundsText.setTextAnchor(new Vector2(1, 1));
        roundsText.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        roundsText.setTextColour(new Color(0xffffff));
        roundsText.setText("Round 0 of " + difficulty.getRounds());
        components.offer(roundsText);
        bottomGui.offer(roundsText);

        RenderComponent difficultyText = new RenderComponent(new Rectangle(10, 600, 1030, 110), new Color(0x444444));
        difficultyText.setBackgroundVisible(false);
        difficultyText.setTextAnchor(new Vector2(0, 1));
        difficultyText.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        difficultyText.setTextColour(new Color(0xffffff));
        difficultyText.setText("Difficulty: " + difficulty.getName());
        components.offer(difficultyText);
        bottomGui.offer(difficultyText);

        commentaryText = new RenderComponent(new Rectangle(10, 610, 1030, 100), new Color(0x444444));
        commentaryText.setBackgroundVisible(false);
        commentaryText.setTextAnchor(new Vector2(0, 0));
        commentaryText.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        commentaryText.setTextColour(new Color(0xffffff));
        commentaryText.setText("Welcome to Moose Tower Defense!\nIf you forgot how to play, feel free to reference the tutorial in the main menu!");
        components.offer(commentaryText);
        bottomGui.offer(commentaryText);

        towerIcon = new RenderComponent(new Rectangle(10, 610, 100, 100), new Color(0));
        towerIcon.setBackgroundVisible(false);
        towerIcon.setVisible(false);
        components.offer(towerIcon);
        upgradeGui.offer(towerIcon);

        towerDescription = new RenderComponent(new Rectangle(110, 610, 200, 30), new Color(0));
        towerDescription.setBackgroundVisible(false);
        towerDescription.setTextColour(new Color(0xffffff));
        towerDescription.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        towerDescription.setTextAnchor(new Vector2(0, 0.5));
        towerDescription.setVisible(false);
        components.offer(towerDescription);
        upgradeGui.offer(towerDescription);

        modeButton = new ButtonComponent(new Rectangle(110, 645, 200, 30), new Color(0)) {
            public void onPressed() {
                upgradingTower.toggleMode();
            }
        };
        modeButton.setBackgroundVisible(false);
        modeButton.setTextColour(new Color(0xffffff));
        modeButton.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        modeButton.setTextAnchor(new Vector2(0, 0.5));
        modeButton.setVisible(false);
        components.offer(modeButton);
        upgradeGui.offer(modeButton);

        sellButton = new ButtonComponent(new Rectangle(110, 680, 200, 30), new Color(0)) {
            public void onPressed() {
                if (upgradingTower != null) {
                    upgradingTower.sell();
                    setUpgradingTower(null);
                }
            }
        };
        sellButton.setBackgroundVisible(false);
        sellButton.setTextColour(new Color(0xffffff));
        sellButton.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        sellButton.setTextAnchor(new Vector2(0, 0.5));
        sellButton.setVisible(false);
        components.offer(sellButton);
        upgradeGui.offer(sellButton);

        leftUpgradeButton = new ButtonComponent(new Rectangle(350, 610, 340, 80), new Color(0)) {
            public void onPressed() {
                if (upgradingTower != null) {
                    if (upgradingTower.upgradeLeft()) {
                        updateUpgradeGui();
                    }
                }
            }
        };
        leftUpgradeButton.setTextColour(new Color(0xffffff));
        leftUpgradeButton.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        leftUpgradeButton.setTextAnchor(new Vector2(0.1, 0.1));
        leftUpgradeButton.setVisible(false);
        components.offer(leftUpgradeButton);
        upgradeGui.offer(leftUpgradeButton);

        rightUpgradeButton = new ButtonComponent(new Rectangle(700, 610, 340, 80), new Color(0)) {
            public void onPressed() {
                if (upgradingTower != null) {
                    if (upgradingTower.upgradeRight()) {
                        updateUpgradeGui();
                    }
                }
            }
        };
        rightUpgradeButton.setTextColour(new Color(0xffffff));
        rightUpgradeButton.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        rightUpgradeButton.setTextAnchor(new Vector2(0.1, 0.1));
        rightUpgradeButton.setVisible(false);
        components.offer(rightUpgradeButton);
        upgradeGui.offer(rightUpgradeButton);

        upgradeDescription = new RenderComponent(new Rectangle(350, 690, 700, 30), new Color(0));
        upgradeDescription.setTextColour(new Color(0xffffff));
        upgradeDescription.setFont(new Font("Segoe UI", Font.PLAIN, 25));
        upgradeDescription.setTextAnchor(new Vector2(0, 0.5));
        upgradeDescription.setVisible(false);
        upgradeDescription.setBackgroundVisible(false);
        upgradeDescription.setRenderPriority(3);
        components.offer(upgradeDescription);
        upgradeGui.offer(upgradeDescription);

        Game.getWindow().addKeyListener(this);
        Game.currentGame().addMouseMotionListener(this);
        loaded = true;
    }

    public TileMap(DifficultyData d, String[] choices) { // creating a fresh new game
        super(new Rectangle(0, 0, 1050, 600), new Color(0));
        towers = new ConcurrentHashMap<Tower, Boolean>();
        difficulty = d;
        towerChoices = choices;
        lives = difficulty.getLives();
        int minPathLength = d.getMinPathLength();
        saveFile = new File("saves\\" + new Date().getTime() + ".txt");
        do {
            MapGenerator.generate(this);
        } while (moosePath.length < minPathLength); // this is very unoptimized and will need to be fixed later but will do for the time being
        buildGame();
    }

    public TileMap(File save) throws IOException { // loading a game from a save file
        super(new Rectangle(0, 0, 1050, 600), new Color(0));
        saveFile = save;
        Scanner s = new Scanner(save);
        towerChoices = new String[5];
        for (int i = 0; i <= 4; i++) { // read in tower choices
            towerChoices[i] = s.nextLine();
        }

        difficulty = Game.getDifficulties()[Integer.parseInt(s.nextLine())]; // read difficulty
        int tempMoneyStorage = Integer.parseInt(s.nextLine()); // reading in money to be set later
        lives = Integer.parseInt(s.nextLine());
        round = Integer.parseInt(s.nextLine());

        map = new int[14][8];

        for (int x = 0; x < 14; x++) { // reading in the map
            for (int y = 0; y < 8; y++) {
                map[x][y] = Integer.parseInt(s.nextLine());
            }
        }

        int pathLength = Integer.parseInt(s.nextLine());  // reading in the moose path
        moosePath = new Vector2[pathLength];
        for (int i = 0; i < pathLength; i++) {
            moosePath[i] = new Vector2(Double.parseDouble(s.nextLine()), Double.parseDouble(s.nextLine()));
        }

        towerStats = Game.getTowerStats();
        int nTowers =  Integer.parseInt(s.nextLine());
        towers = new ConcurrentHashMap<Tower, Boolean>();

        for (int i = 0; i < nTowers; i++) {
            String towerName = s.nextLine();
            Vector2 gridPos = new Vector2(Double.parseDouble(s.nextLine()), Double.parseDouble(s.nextLine()));

            try { // java stop complaining wawawawawa
                Tower twr = (Tower)(towerStats.get(towerName).getTowerClass().getConstructor(Vector2.class, TileMap.class).newInstance(gridPos, this));
                twr.setLeftPath(Integer.parseInt(s.nextLine()));
                twr.setRightPath(Integer.parseInt(s.nextLine()));
                twr.setMoneySpent(Integer.parseInt(s.nextLine()));
                twr.setMode(Integer.parseInt(s.nextLine()));
                towers.put(twr, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        money = tempMoneyStorage;
        s.close();
        buildGame();
        roundsText.setText("Round " + round + " of " + difficulty.getRounds());
    }

    // moves the game forward by delta seconds
    public void evolve(double delta) {
        if (paused || !loaded) {
            return;
        }
        if (roundPlaying) {
            delta *= gameSpeed;
            for (Moose m: mooses.values()) {
                m.evolve(delta * difficulty.getEvolveSpeed());
            }
            for (Tower t: towers.keySet()) {
                t.evolve(delta);

                // get towers to lock onto new targets
                for (Moose m: mooses.values()) {
                    if (t.inRange(m)) {
                        t.setTarget(m);
                    }
                }
            }
            for (Projectile p: projectiles.keySet()) { // moving projectiles forwards
                p.evolve(delta);
            }
            for (Moose m: mooses.values()) { // see what projectiles are hitting the mooses
                for (Projectile p: projectiles.keySet()) {
                    if (p.intersects(m.getHitbox())) {
                        p.damage(m);
                    }
                }
            }
            if (roundHandler.evolve(delta) && mooses.size() == 0) { // round is completed
                System.gc();
                round++;
                commentaryText.setText(roundHandler.getCommentary(round));
                if (round > difficulty.getRounds()) {
                    Game.gameWinScreen(difficulty, lives);
                    return;
                }
                roundsText.setText("Round " + round + " of " + difficulty.getRounds());
                roundPlaying = false;
                startButton.setText("Start");
                money += 100;
                for (Projectile p: projectiles.keySet()) {
                    p.destroy();
                }
                for (Tower t: towers.keySet()) {
                    t.setTarget(null);
                }

                try {
                    saveGame();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        System.out.println(keyCode);
        if (!pressedKeys.contains(keyCode)) {
            pressedKeys.add(keyCode);
            switch (keyCode) {
                case 8: // backspace
                    if (upgradingTower != null) {
                        upgradingTower.sell();
                        setUpgradingTower(null);
                    }
                    break;
                case 27:
                    selectedTower = null;
                    setUpgradingTower(null);
                    break;
                case 44: // left upgrade
                    if (upgradingTower != null) {
                        if (upgradingTower.upgradeLeft()) {
                            updateUpgradeGui();
                        }
                    }
                    break;
                case 46: // right upgrade
                    if (upgradingTower != null) {
                        if (upgradingTower.upgradeRight()) {
                            updateUpgradeGui();
                        }
                    }
                    break;
                case 49:
                    selectedTower = towerStats.get(towerChoices[1]);
                    break;
                case 50:
                    selectedTower = towerStats.get(towerChoices[2]);
                    break;
                case 51:
                    selectedTower = towerStats.get(towerChoices[3]);
                    break;
                case 52:
                    selectedTower = towerStats.get(towerChoices[4]);
                    break;
            }
        }
    }

    // places down tower once clicked
    public void onPressed() {
        if (selectedTower != null) {
            Vector2 gridPos = mousePos.divide(75);
            int x = (int)gridPos.getX();
            int y = (int)gridPos.getY();
            if (money >= selectedTower.getCost() && map[x][y] == 0) {
                try {
                    towers.put((Tower)(selectedTower.getTowerClass().getConstructor(Vector2.class, TileMap.class).newInstance(gridPos, this)), true);
                    selectedTower = null;
                } catch (Exception e) {
                }
            }
        }

        setUpgradingTower(null);
    }

    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    public void mouseDragged(MouseEvent e) {
    }
    public void mouseMoved(MouseEvent e) {
        Dimension size = Game.currentGame().getSize();
        double windowX = size.getWidth();
        double windowY = size.getHeight();
        mousePos = new Vector2(e.getX() * 1280 / windowX, e.getY() * 720 / windowY);
    }
}
