import org.jogamp.vecmath.*;

public class Vertex {
    Vector3f coord;
    Vector2f texCoord;
    Vector3f normal;
    Vertex(Vector3f v, Vector2f vt, Vector3f vn) {
        coord = v;
        texCoord = vt;
        normal = vn;
    }
}
