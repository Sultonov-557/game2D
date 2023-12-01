package game;

public class GameObject {
    public Vector2D position;
    public Vector2D velocity;
    public Vector2D size;

    public GameObject(Vector2D pos, Vector2D velocity, Vector2D size) {
        this.position = pos;
        this.velocity = velocity;
        this.size = size;
    }

    public static boolean checkCollision(GameObject objectA, GameObject objectB) {
        if (objectA.position.x < objectB.position.x + objectB.size.x &&
                objectA.position.x + objectA.size.x > objectB.position.x &&
                objectA.position.y < objectB.position.y + objectB.size.y &&
                objectA.position.y + objectA.size.y > objectB.size.y &&
                objectB.position.x < objectA.position.x + objectA.size.x &&
                objectB.position.x + objectB.size.x > objectA.position.x &&
                objectB.position.y < objectA.position.y + objectA.size.y &&
                objectB.position.y + objectB.size.y > objectA.size.y) {

            return true;
        }
        return false;
    }
}
