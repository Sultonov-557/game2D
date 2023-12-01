package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    int FPS = 90;

    int HEIGHT = 1000;
    int WIDTH = 1000;

    float BOUNCE = 0.1f;

    int SPEED_LIMIT = 1;

    Vector2D gravity = new Vector2D(0, 0);

    Thread gameThread;

    ArrayList<GameObject> objects = new ArrayList<>();

    public GamePanel() {
        this.setPreferredSize(new Dimension(HEIGHT, WIDTH));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        init();

        double drawInterval = 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);

                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void init() {
        for (int i = 0; i < 1000; i++) {
            objects.add(new GameObject(new Vector2D(getRandomNumber(0, WIDTH), getRandomNumber(0, HEIGHT)),
                    new Vector2D(getRandomNumber(-SPEED_LIMIT, SPEED_LIMIT),
                            getRandomNumber(-SPEED_LIMIT, SPEED_LIMIT)),
                    new Vector2D(30, 30)));
        }
    }

    public void update() {
        for (GameObject gameObject : objects) {

            gameObject.velocity.x = Math.min(Math.max(-SPEED_LIMIT, gameObject.velocity.x), SPEED_LIMIT);
            gameObject.velocity.y = Math.min(Math.max(-SPEED_LIMIT, gameObject.velocity.y), SPEED_LIMIT);

            gameObject.velocity = Vector2D.add(gameObject.velocity, gravity);

            for (GameObject otherGameObject : objects) {
                if (otherGameObject != gameObject) {
                    if (GameObject.checkCollision(otherGameObject, gameObject)) {
                        Vector2D velA = Vector2D
                                .normalize(Vector2D.direction(otherGameObject.position, gameObject.position));
                        Vector2D velB = Vector2D
                                .normalize(Vector2D.direction(gameObject.position, otherGameObject.position));

                        velA.x = velA.x * BOUNCE;
                        velA.y = velA.y * BOUNCE;
                        velB.x = velB.x * BOUNCE;
                        velB.y = velB.y * BOUNCE;

                        gameObject.velocity = Vector2D.add(velA, gameObject.velocity);
                        otherGameObject.velocity = Vector2D.add(velB, otherGameObject.velocity);
                    }
                }
            }

            if (gameObject.position.x < 0f) {
                gameObject.position.x = 1;
                gameObject.velocity.x = -gameObject.velocity.x * BOUNCE;
            }
            if (gameObject.position.y < 0f) {
                gameObject.position.y = 1;
                gameObject.velocity.y = -gameObject.velocity.y * BOUNCE;
            }
            if (gameObject.position.x + gameObject.size.x > WIDTH) {
                gameObject.position.x = WIDTH - gameObject.size.y;
                gameObject.velocity.x = -gameObject.velocity.x * BOUNCE;
            }
            if (gameObject.position.y + gameObject.size.y > HEIGHT) {
                gameObject.position.y = HEIGHT - gameObject.size.y;
                gameObject.velocity.y = -gameObject.velocity.y * BOUNCE;
            }

            gameObject.position = Vector2D.add(gameObject.position, gameObject.velocity);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (GameObject gameObject : objects) {
            int speed = (int) ((gameObject.velocity.x + gameObject.velocity.y) / 2) * 10;
            if (speed < 0) {
                speed = -speed;
            }

            int minSpeed = 0;
            int maxSpeed = SPEED_LIMIT;
            float minHue = 240f / 360f; // Blue hue (in HSB color space)
            float maxHue = 0f / 360f; // Red hue (in HSB color space)

            // Calculate the percentage of speed within the range
            double percentage = (double) (speed - minSpeed) / (maxSpeed - minSpeed);

            // Calculate the hue value based on the percentage
            float hue = (float) ((1 - percentage) * minHue + percentage * maxHue);

            if (hue > minHue) {
                hue = minHue;
            }
            if (hue < maxHue) {
                hue = maxHue;
            }
            // Create a color with the calculated hue value (saturation and brightness
            // remain unchanged)
            Color newColor = Color.getHSBColor(hue, 1f, 1f);

            g2.setColor(newColor);
            g2.fillRect((int) gameObject.position.x, (int) gameObject.position.y, (int) gameObject.size.x,
                    (int) gameObject.size.y);
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
