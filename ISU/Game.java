// Carlton Qian
// ICS4U1-01
// Ms. Wong
// ISU Assignment

package ISU;

import java.util.*;
import java.util.Queue;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.awt.image.*;
import javax.imageio.*;

// handles graphics and game logic
public class Game extends JPanel implements Runnable, MouseListener {
    private static Game currentPanel; // stores the location of the driver jpanel currently in use
    private static HashMap<String, Clip> audios;
    private static HashMap<String, BufferedImage> images;
    private static HashMap<String, ParallelAudio> parallelAudios;
    private static Queue<RenderComponent> components;
    private static HashMap<String, TowerData> towerStats;
    private static TileMap currentMap;
    private static long timestamp = System.nanoTime();
    private static JFrame window;
    private static Clip playingMusic;
    private static int tutorialPage = 0;
    private static DifficultyData[] difficulties = {
        new DifficultyData("Easy", 200, 40, 30, 0.8, new Color(0x00ff00)),
        new DifficultyData("Medium", 150, 60, 25, 1.0, new Color(0xffff00)),
        new DifficultyData("Hard", 100, 80, 25, 1.3, new Color(0xff0000))
    };

    public static HashMap<String, TowerData> getTowerStats() {
        return towerStats;
    }

    public static HashMap<String, BufferedImage> getImages() {
        return images;
    }

    public static HashMap<String, Clip> getAudios() {
        return audios;
    }

    public static DifficultyData[] getDifficulties() {
        return difficulties;
    }

    public static HashMap<String, ParallelAudio> getParallelAudios() {
        return parallelAudios;
    }
    public static void destroyCurrentMap() {
        currentMap = null;
    }

    // returns the current instance of the game that is running
    public static Game currentGame() {
        return currentPanel;
    }

    public static JFrame getWindow() {
        return window;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D)g;
        double scaleFactor = getSize().getWidth() / 1280;
        graphics.scale(scaleFactor, scaleFactor);
        RenderComponent.drawComponents(graphics);
    }

    // deletes all elements in the gui
    public static void clearScreen() {
        while (!components.isEmpty()) { 
            components.poll().destroy();
        }
    }

    public static void playMusic(Clip c) {
        if (playingMusic != null) {
            if (playingMusic.equals(c)) {
                return;
            }
            playingMusic.stop();
        }
        playingMusic = c;
        c.setFramePosition(0);
        c.start();
    }

    public static void loopMusic(Clip c) {
        if (playingMusic != null) {
            if (playingMusic.equals(c)) {
                return;
            }
            playingMusic.stop();
        }
        playingMusic = c;
        c.setFramePosition(0);
        c.loop(1 << 30);
    }

    // loads the gui components for the main menu
    public static void loadMainMenu() {
        clearScreen();
        loopMusic(audios.get("menu theme.wav"));

        RenderComponent bg = new RenderComponent(new Rectangle(0, 0, 1280, 720), new Color(0));
        bg.setImage(images.get("vaporwave background.jpg"));
        bg.setBackgroundVisible(false);

        RenderComponent title1 = new RenderComponent(new Rectangle(160, 60, 960, 100), new Color(0));
        title1.setBackgroundVisible(false);
        title1.setTextColour(new Color(0xDB9EE1));
        title1.setFont(new Font("MS Gothic", Font.BOLD, 100));
        title1.setText("MOOSE TOWER");
        //title1.setTextAnchor(new Vector2(0.6, 0.5));

        RenderComponent title2 = new RenderComponent(new Rectangle(160, 180, 960, 100), new Color(0));
        title2.setBackgroundVisible(false);
        title2.setTextColour(new Color(0xDB9EE1));
        title2.setFont(new Font("MS Gothic", Font.BOLD, 100));
        title2.setText("DEFENSE");
        //title2.setTextAnchor(new Vector2(0.6, 0.5));

        ButtonComponent newGame = new ButtonComponent(new Rectangle(320, 300, 640, 80), new Color(0xDB9EE1)) {
            public void onPressed() {
                loadTowerChooser();
            }
        };
        newGame.setTextColour(new Color(0xffffff));
        newGame.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        newGame.setText("NEW GAME");

        ButtonComponent loadGame = new ButtonComponent(new Rectangle(320, 400, 640, 80), new Color(0xDB9EE1)) {
            public void onPressed() {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("saves"));
                int returnVal = chooser.showOpenDialog(window);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        loadMainGame(chooser.getSelectedFile());
                    } catch (Exception e) {

                    }
                }
            }
        };
        loadGame.setTextColour(new Color(0xffffff));
        loadGame.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        loadGame.setText("LOAD GAME");

        ButtonComponent tutorial = new ButtonComponent(new Rectangle(320, 500, 640, 80), new Color(0xDB9EE1)) {
            public void onPressed() {
                tutorialScreen();
            }
        };
        tutorial.setTextColour(new Color(0xffffff));
        tutorial.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        tutorial.setText("TUTORIAL");

        ButtonComponent about = new ButtonComponent(new Rectangle(320, 600, 640, 80), new Color(0xDB9EE1)) {
            public void onPressed() {
                aboutScreen();
            }
        };
        about.setTextColour(new Color(0xffffff));
        about.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        about.setText("ABOUT");

        components.offer(bg);
        components.offer(title1);
        components.offer(title2);
        components.offer(newGame);
        components.offer(loadGame);
        components.offer(tutorial);
        components.offer(about);
    }

    // loads gui components for the tower selector and the 
    public static void loadTowerChooser() {
        clearScreen();

        RenderComponent bg = new RenderComponent(new Rectangle(0, 0, 1280, 720), new Color(0));
        bg.setImage(images.get("vaporwave background.jpg"));
        bg.setBackgroundVisible(false);

        RenderComponent title = new RenderComponent(new Rectangle(20, 20, 620, 100), new Color(0));
        title.setBackgroundVisible(false);
        title.setFont(new Font("MS Gothic", Font.BOLD, 100));
        title.setTextColour(new Color(0xDB9EE1));
        title.setText("NEW GAME");
        title.setTextAnchor(new Vector2(0, 0.5));

        ButtonComponent back = new ButtonComponent(new Rectangle(1040, 20, 220, 100), new Color(0)) {
            public void onPressed() {
                loadMainMenu();
            }
        };
        back.setBackgroundVisible(false);
        back.setFont(new Font("MS Gothic", Font.BOLD, 100));
        back.setTextColour(new Color(0xDB9EE1));
        back.setText("BACK");
        back.setTextAnchor(new Vector2(1, 0.5));

        // display for towers that have been chosen
        ButtonComponent[] selectView = new ButtonComponent[5];
        RenderComponent[] priceView = new RenderComponent[5];
        String[] towerChoices = new String[5];
        for (int i = 1; i <= 4; i++) {
            int x = i; // why can't java support upvalues like lua >:(

            RenderComponent label = new RenderComponent(new Rectangle(22 + 315 * (i - 1), 120, 295, 50), new Color(0));
            label.setBackgroundVisible(false);
            label.setFont(new Font("Courier New", Font.PLAIN, 50));
            label.setTextColour(new Color(0xffffff));
            label.setTextAnchor(new Vector2(0, 0.5));
            label.setText(Integer.toString(i));
            label.setRenderPriority(4);
            components.offer(label);

            RenderComponent price = new RenderComponent(new Rectangle(22 + 315 * (i - 1), 365, 295, 50), new Color(0));
            price.setBackgroundVisible(false);
            price.setFont(new Font("Courier New", Font.PLAIN, 50));
            price.setTextColour(new Color(0xffff00));
            price.setTextAnchor(new Vector2(0, 0.5));
            price.setText("");
            price.setRenderPriority(4);
            priceView[i] = price;
            components.offer(price);

            ButtonComponent button = new ButtonComponent(new Rectangle(20 + 315 * (i - 1), 120, 295, 295), new Color(0xDB9EE1)) {
                public void onPressed() {
                    towerChoices[x] = null;
                    price.setText("");
                    setImage(null);
                }
            };
            //button.setRenderPriority(1);
            selectView[i] = button;
            components.offer(button);
        }

        // displaying all the possible towers that the player can choose from
        int position = 20;
        for (String s: towerStats.keySet()) {
            TowerData data = towerStats.get(s);
            ButtonComponent button = new ButtonComponent(new Rectangle(position, 435, 150, 150), new Color(0xDB9EE1)) {
                public void onPressed() { // adds tower to the selection
                    for (int i = 1; i <= 4; i++) { // making sure some idiot doesn't select the same tower twice
                        if (towerChoices[i] == null) {
                            continue;
                        }
                        if (towerChoices[i].equals(s)) {
                            return;
                        }
                    }
                    for (int i = 1; i <= 4; i++) {
                        if (towerChoices[i] == null) { // slot is open
                            towerChoices[i] = s;
                            selectView[i].setImage(images.get(data.getIcon()));
                            priceView[i].setText("$" + data.getCost());
                            break;
                        }
                    }
                }
            };
            button.setImage(images.get(data.getIcon()));
            components.offer(button);
            position += 170;
        }

        // display difficulties
        position = 20;
        for (int i = 0; i < 3; i++, position += 420) {
            DifficultyData difficulty = difficulties[i];
            ButtonComponent button = new ButtonComponent(new Rectangle(position, 600, 400, 100), difficulty.getColour()) {
                public void onPressed() {
                    for (int i = 1; i <= 4; i++) { // checking if four towers have been chosen
                        if (towerChoices[i] == null) {
                            return;
                        }
                    }
                    loadMainGame(difficulty, towerChoices);
                }
            };
            button.setFont(new Font("Segoe UI", Font.PLAIN, 80));
            button.setText(difficulty.getName());
            button.setTextColour(new Color(0));
            components.offer(button);
        }


        components.offer(bg);
        components.offer(title);
        components.offer(back);

    }

    public static void loadMainGame(DifficultyData d, String[] towerChoices) { // loads the tower defense part of the game itself (fresh game)
        clearScreen();
        System.gc();
        currentMap = new TileMap(d, towerChoices);
        components.offer(currentMap);
        loopMusic(audios.get("track theme.wav"));
    }

    public static void loadMainGame(File save) {
        try {
            System.gc();
            currentMap = new TileMap(save);
            clearScreen();
            components.offer(currentMap);
            loopMusic(audios.get("track theme.wav"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gameOverScreen(DifficultyData d, int round) { // loads the game over screen when you LOOSE!
        clearScreen();
        playMusic(audios.get("game over theme.wav"));

        RenderComponent bg = new RenderComponent(new Rectangle(0, 0, 1280, 720), new Color(0));
        bg.setImage(images.get("it's joever.jpg"));
        bg.setBackgroundVisible(false);
        components.offer(bg);

        RenderComponent infoDisplay = new RenderComponent(new Rectangle(0, 310, 1280, 100), new Color(0));
        infoDisplay.setBackgroundVisible(false);
        infoDisplay.setTextColour(new Color(0xDB9EE1));
        infoDisplay.setFont(new Font("MS Gothic", Font.BOLD, 80));
        infoDisplay.setText("Rounds completed: " + (round - 1) + " of " + d.getRounds());
        components.offer(infoDisplay);

        ButtonComponent backButton = new ButtonComponent(new Rectangle(480, 500, 320, 80), new Color(0xDB9EE1)) {
            public void onPressed() {
                loadMainMenu();
            }
        };
        backButton.setFont(new Font("MS Gothic", Font.PLAIN, 60));
        backButton.setTextColour(new Color(0xffffff));
        backButton.setText("Back");
        components.offer(backButton);
    }

    public static void gameWinScreen(DifficultyData d, int lives) { // thing that congratulates you when you win whaaa???
        clearScreen();
        playMusic(audios.get("BILLIONS MUST SMILE.wav"));

        RenderComponent bg = new RenderComponent(new Rectangle(0, 0, 1280, 720), new Color(0));
        bg.setImage(images.get("winscreen.jpg"));
        bg.setBackgroundVisible(false);
        components.offer(bg);

        RenderComponent title = new RenderComponent(new Rectangle(0, 50, 1280, 200), new Color(0));
        title.setBackgroundVisible(false);
        title.setTextColour(new Color(0xDB9EE1));
        title.setFont(new Font("MS Gothic", Font.BOLD, 160));
        title.setText("You win!");
        components.offer(title);

        RenderComponent difficultyText = new RenderComponent(new Rectangle(0, 260, 1280, 100), new Color(0));
        difficultyText.setBackgroundVisible(false);
        difficultyText.setTextColour(new Color(0xDB9EE1));
        difficultyText.setFont(new Font("MS Gothic", Font.BOLD, 80));
        difficultyText.setText("Difficulty: " + d.getName());
        components.offer(difficultyText);

        RenderComponent livesText = new RenderComponent(new Rectangle(0, 360, 1280, 100), new Color(0));
        livesText.setBackgroundVisible(false);
        livesText.setTextColour(new Color(0xDB9EE1));
        livesText.setFont(new Font("MS Gothic", Font.BOLD, 80));
        livesText.setText("Lives: " + lives + " of " + d.getLives());
        components.offer(livesText);

        ButtonComponent backButton = new ButtonComponent(new Rectangle(480, 500, 320, 80), new Color(0xDB9EE1)) {
            public void onPressed() {
                loadMainMenu();
            }
        };
        backButton.setFont(new Font("MS Gothic", Font.PLAIN, 60));
        backButton.setTextColour(new Color(0xffffff));
        backButton.setText("Back");
        components.offer(backButton);
    }

    public static void tutorialScreen() {
        tutorialPage = 0;
        
        RenderComponent backBg = new RenderComponent(new Rectangle(0, 0, 1280, 720), new Color(0));
        backBg.setImage(images.get("vaporwave background.jpg"));
        backBg.setBackgroundVisible(false);
        components.offer(backBg);

        RenderComponent bg = new RenderComponent(new Rectangle(160, 50, 960, 540), new Color(0));
        bg.setImage(images.get("tutorial0.png"));
        bg.setBackgroundVisible(false);
        components.offer(bg);

        ButtonComponent scrollLeft = new ButtonComponent(new Rectangle(10, 610, 200, 100), new Color(0xDB9EE1)) {
            public void onPressed() {
                tutorialPage--;
                tutorialPage %= 4;

                bg.setImage(images.get("tutorial" + tutorialPage + ".png"));
            }
        };
        scrollLeft.setTextColour(new Color(0xffffff));
        scrollLeft.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        scrollLeft.setText("<-");
        components.offer(scrollLeft);

        ButtonComponent scrollRight = new ButtonComponent(new Rectangle(1070, 610, 200, 100), new Color(0xDB9EE1)) {
            public void onPressed() {
                tutorialPage++;
                tutorialPage %= 4;

                bg.setImage(images.get("tutorial" + tutorialPage + ".png"));
            }
        };
        scrollRight.setTextColour(new Color(0xffffff));
        scrollRight.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        scrollRight.setText("->");
        components.offer(scrollRight);

        ButtonComponent backButton = new ButtonComponent(new Rectangle(220, 610, 840, 100), new Color(0xDB9EE1)) {
            public void onPressed() {
                loadMainMenu();
            }
        };
        backButton.setTextColour(new Color(0xffffff));
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        backButton.setText("Back");
        components.offer(backButton);
    }

    public static void aboutScreen() {
        RenderComponent bg = new RenderComponent(new Rectangle(0, 0, 1280, 720), new Color(0));
        bg.setImage(images.get("vaporwave background.jpg"));
        bg.setBackgroundVisible(false);
        components.offer(bg);

        RenderComponent title = new RenderComponent(new Rectangle(50, 50, 1230, 670), new Color(0));
        title.setBackgroundVisible(false);
        title.setTextColour(new Color(0xDB9EE1));
        title.setTextAnchor(new Vector2(0, 0));
        title.setFont(new Font("MS Gothic", Font.BOLD, 120));
        title.setText("Carlton Qian\nICS4U1-01\nMrs. Wong\nISU Game Project");
        components.offer(title);

        ButtonComponent back = new ButtonComponent(new Rectangle(1040, 20, 220, 100), new Color(0)) {
            public void onPressed() {
                loadMainMenu();
            }
        };
        back.setBackgroundVisible(false);
        back.setFont(new Font("MS Gothic", Font.BOLD, 100));
        back.setTextColour(new Color(0xDB9EE1));
        back.setText("BACK");
        back.setTextAnchor(new Vector2(1, 0.5));
        components.offer(back);
    }
    // game initializing
    public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        // reading files and initializing variables
        audios = new HashMap<String, Clip>();
        images = new HashMap<String, BufferedImage>();
        towerStats = new HashMap<String, TowerData>();
        components = new LinkedList<RenderComponent>();
        for (File f: new File("assets/images").listFiles()) {
            images.put(f.getName(), ImageIO.read(f));
        }
        for (File f: new File("assets/audio").listFiles()) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(f);
            Clip c = (Clip)AudioSystem.getLine(new DataLine.Info(Clip.class, audioStream.getFormat()));
            c.addLineListener(new LineListener() {
                public void update(LineEvent e) {}
            });
            c.open(audioStream);
            audios.put(f.getName(), c);
        }
        parallelAudios = new HashMap<String, ParallelAudio>();
        parallelAudios.put("pop.wav", new ParallelAudio(new File("assets/audio/pop.wav"), 5));
        parallelAudios.put("collide.wav", new ParallelAudio(new File("assets/audio/collide.wav"), 5));

        towerStats.put("Gunner", new TowerData(200, "gunner.png", GunnerTower.class, 2));
        towerStats.put("Energizer", new TowerData(500, "energizer.png", Energizer.class, 2));
        towerStats.put("Cannon", new TowerData(650, "cannon.png", Cannon.class, 2));
        towerStats.put("Fire Tower", new TowerData(400, "fire tower.png", FireTower.class, 2));
        towerStats.put("Water Tower", new TowerData(350, "water tower.png", WaterTower.class, 1));
        // setting up the window
        window = new JFrame("Moose Tower Defense");
        window.setLayout(null);

        Game d = new Game();
        currentPanel = d;
        window.setPreferredSize(new Dimension(640, 360));
        d.setBackground(new Color(0));
        window.add(d);
        window.pack();
        window.setVisible(true);
        d.addMouseListener(d);

        window.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {}
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
            public void windowClosed(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}  
        });

        loadMainMenu();

        // starting graphics thread
        Thread graphicsThread = new Thread(d);
        graphicsThread.start();
    }

    // render task
    public void run() {
        while (true) {
            try {
                // forcing the window into a 16:9 aspect ratio
                double delta = (System.nanoTime() - timestamp) * 0.000000001; // how much time in seconds has elapsed since the previous frame
                timestamp = System.nanoTime();
                Container window = getParent();
                Dimension size = window.getSize();
                double windowX = size.getWidth();
                double windowY = size.getHeight();

                if (windowY / windowX < 9.0 / 16) { // window is too wide horizontally
                    int y = (int)windowY;
                    int x = (int)(windowY * 16 / 9);
                    currentPanel.setBounds((int)((windowX - x) / 2), 0, x, y);
                } else { // window is too wide vertically
                    int x = (int)windowX;
                    int y = (int)(windowX * 9 / 16);
                    currentPanel.setBounds(0, (int)((windowY - y) / 2), x, y);
                }
                if (currentMap != null) {
                    currentMap.evolve(Math.min(0.1, delta));
                }
                repaint();
                Thread.sleep(0);
            } catch (InterruptedException e) {
            }
        }
    }

    // mouse listener stuff
    public void mouseClicked(MouseEvent e) {
        
    }

    // forwards all mouse presses from the panel to components that need them like buttons
    public void mousePressed(MouseEvent e) {
        Dimension size = getSize();
        double windowX = size.getWidth();
        double windowY = size.getHeight();
        int clickX = (int)(e.getX() * 1280 / windowX);
        int clickY = (int)(e.getY() * 720 / windowY);
        ButtonComponent.processClick(clickX, clickY);
    }
    public void mouseReleased(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}