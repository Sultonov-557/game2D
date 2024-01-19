package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, MouseListener, MouseMotionListener {

    int FPS = 90;

    int HEIGHT = 1000;
    int WIDTH = 1000;
    int SIZE = 10;
    int COUNT = 500;

    float BOUNCE = 0.7f;

    int SPEED_LIMIT = 10;

    float GRAVITY = 0.2f;
    float ANTI_GRAVITY = 0.2f;
    float MOUSE_POWER = 0.2f;

    int mouseX = 0;
    int mouseY = 0;
    boolean mouseDown = false;

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
        for (int i = 0; i < COUNT; i++) {
            objects.add(new GameObject(new Vector2D(getRandomNumber(0, WIDTH), getRandomNumber(0, HEIGHT)),
                    new Vector2D(getRandomNumber(-SPEED_LIMIT, SPEED_LIMIT),
                            getRandomNumber(-SPEED_LIMIT, SPEED_LIMIT)),
                    new Vector2D(SIZE, SIZE)));
        }
    }

    public void update() {
        System.out.println(mouseDown + " " + mouseX + " " + mouseY);
        for (GameObject gameObject : objects) {
            if (mouseDown) {

                Vector2D vel = Vector2D
                        .normalize(Vector2D.direction(gameObject.position, new Vector2D(mouseX, mouseY)));

                vel.x = vel.x * MOUSE_POWER;
                vel.y = vel.y * MOUSE_POWER;

                gameObject.velocity = Vector2D.add(vel, gameObject.velocity);
            }

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

                if (true) {
                    Vector2D vel = Vector2D
                            .normalize(Vector2D.direction(otherGameObject.position, gameObject.position));

                    vel.x = vel.x * GRAVITY;
                    vel.y = vel.y * GRAVITY;

                    otherGameObject.velocity = Vector2D.add(vel, otherGameObject.velocity);
                }

                if (true) {
                    Vector2D vel = Vector2D
                            .normalize(Vector2D.direction(gameObject.position, otherGameObject.position));

                    vel.x = vel.x * GRAVITY;
                    vel.y = vel.y * GRAVITY;

                    otherGameObject.velocity = Vector2D.add(vel, otherGameObject.velocity);
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

            gameObject.velocity.x = Math.clamp(gameObject.velocity.x, -SPEED_LIMIT, SPEED_LIMIT);
            gameObject.velocity.y = Math.clamp(gameObject.velocity.y, -SPEED_LIMIT, SPEED_LIMIT);

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

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getPoint().x;
        mouseY = e.getPoint().y;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getPoint().x;
        mouseY = e.getPoint().y;
    }
}
