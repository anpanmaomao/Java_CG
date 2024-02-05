import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;

import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.java3d.utils.applet.MainFrame;
import org.jogamp.java3d.utils.behaviors.mouse.*;



public class Mesh {
    int[][] faces;
    Vector3f[] coords;
    Vector2f[] texCoords;
    Vector3f[] normals;
    // Constructors, copy methods, ...
    public Mesh() {}

    public Face getFace(int i) { 
        Vertex vertices = getVertex(i);
        return new Face(vertices);
    }

    // Get i-th vertex
    public Vertex getVertex(int i) {
        return new Vertex(coords[faces[i][0]], texCoords[faces[i][1]], normals[faces[i][2]]);
    }

    public Mesh fromObj(String filepath) {
        try{
            List<String> l = Files.readAllLines(Paths.get(filepath));
            String[] lines = l.toArray(new String[0]);
            String[] tokens;
            String[] facetokens;
            ArrayList < Vector3f > coords = new ArrayList < Vector3f > ();
            ArrayList < Vector2f > texCoords = new ArrayList < Vector2f > ();
            ArrayList < Vector3f > normals = new ArrayList < Vector3f > ();
            ArrayList < int[] > faces = new ArrayList < int[] > ();
            for (int i = 0, sz = lines.length; i < sz; ++i) {
                tokens = lines[i].split("\\s+");
                // Skip empty lines.
                if (tokens.length > 0) {
                    if (tokens[0].equals("o")) { 
                        // Do something with name.
                    } else if (tokens[0].equals("v")) { 
                        // Coordinate.
                        Vector3f read = new Vector3f(Float.parseFloat(tokens[1]), -1.0f * Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                        coords.add(read);
                    } else if (tokens[0].equals("vt")) {
                        // Texture coordinate.
                        Vector2f read = new Vector2f(Float.parseFloat(tokens[1]), -1.0f * Float.parseFloat(tokens[2]));
                        texCoords.add(read);
                    } else if (tokens[0].equals("vn")) {
                        // Normal.
                        Vector3f read = new Vector3f(Float.parseFloat(tokens[1]), -1.0f * Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                        normals.add(read);
                    } else if (tokens[0].equals("f")) {
                        // Face.
                        int count = tokens.length;
                        // tokens length includes "f", and so is 1 longer.
                        int[] indices = new int[count - 1]; 
                        // Simplified version. Assumes that face will be "v/vt/vn".
                        for (int j = 1; j < count; ++j) {
                            // Indices in .obj file start at 1, not 0.
                            indices[j - 1] = Integer.parseInt(tokens[j]) - 1;
                        }
                        faces.add(indices);
                    }
                }
            }
        
            // copy read attributes to mesh attributes
            this.faces = faces.toArray(new int[faces.size()][]);
            this.coords = coords.toArray(new Vector3f[coords.size()]);
            this.texCoords = texCoords.toArray(new Vector2f[texCoords.size()]);
            this.normals = normals.toArray(new Vector3f[normals.size()]);

            return this;

        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
