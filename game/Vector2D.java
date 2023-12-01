package game;

public class Vector2D {
    public float x;
    public float y;

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2D add(Vector2D vec1, Vector2D vec2) {
        Vector2D vec3 = new Vector2D(vec1.x + vec2.x, vec1.y + vec2.y);
        return vec3;
    }

    public static float distance(Vector2D vec1, Vector2D vec2) {
        float xDiff = vec1.x - vec2.x;
        float yDiff = vec1.y - vec2.y;
        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public static Vector2D direction(Vector2D from, Vector2D to) {
        Vector2D dir = new Vector2D(to.x - from.x, to.y - from.y);
        return dir;
    }

    public static Vector2D normalize(Vector2D vector) {
        float magnitude = (float) Math.sqrt(vector.x * vector.x + vector.y * vector.y);
        if (magnitude != 0) {
            vector.x /= magnitude;
            vector.y /= magnitude;
        }
        return vector;
    }
}
