import javax.swing.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import java.awt.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

// import org.jcp.xml.dsig.internal.dom.Utils;
import org.jogamp.java3d.*;
import org.jogamp.vecmath.*;

import org.jogamp.java3d.utils.geometry.*;
import org.jogamp.java3d.utils.universe.*;
import org.jogamp.java3d.utils.applet.MainFrame;
import org.jogamp.java3d.utils.behaviors.mouse.*;

public class Filled extends JPanel{
    Canvas3D canvas;
	BranchGroup scene = new BranchGroup();
	BranchGroup objRoot = new BranchGroup();

	private Point3d picked = new Point3d();

    PointArray points;
    Point3f[] pointArray;
    LineArray lines;

    SimpleUniverse u = null;

    Mesh mesh = new Mesh();

    Filled(String filepath){
        mesh.fromObj(filepath);

        //tmesh = new TriangleMesh();

        setLayout(new BorderLayout());

        points = new PointArray(mesh.coords.length, PointArray.COORDINATES);

        pointArray = new Point3f[mesh.coords.length];
        for (int i = 0; i < mesh.coords.length; i++) {
            pointArray[i] = new Point3f(mesh.coords[i].x, mesh.coords[i].y, mesh.coords[i].z);
        }
        points.setCoordinates(0, pointArray);


        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		canvas = new Canvas3D(config);

        setPreferredSize(new Dimension(1000,500));
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

		u = new SimpleUniverse(canvas);

        View view = u.getViewer().getView();
        double frontClipDistance = 0.001;
        double backClipDistance = 1000.0;
        view.setFrontClipDistance(frontClipDistance);
        view.setBackClipDistance(backClipDistance);

        // view.setDepthBufferFreezeTransparent(false);
        // view.setTransparencySortingPolicy(View.TRANSPARENCY_SORT_GEOMETRY);

        u.getViewingPlatform().setNominalViewingTransform();


        u.getViewingPlatform().setNominalViewingTransform();
        // u.addBranchGraph(objRoot);

        TransformGroup tg = createBehavior();

        tg.addChild(createSceneGraph());

        objRoot.addChild(tg);

        addLights( objRoot );

        // Set up the background
        Background bgNode = new Background(1.0f,1.0f,1.0f);
        bgNode.setApplicationBounds(getBoundingSphere( ));
        objRoot.addChild(bgNode);
        
        u.addBranchGraph( objRoot );
    }

    public BranchGroup createSceneGraph(){
        BranchGroup bg = new BranchGroup();
        Shape3D s;

        int numTriangles = mesh.faces.length;

        TriangleArray triangles = new TriangleArray(numTriangles * 3, GeometryArray.COORDINATES);

        for(int i = 0; i < numTriangles; i++){
            Point3f p1 = pointArray[mesh.faces[i][0]];
            Point3f p2 = pointArray[mesh.faces[i][1]];
            Point3f p3 = pointArray[mesh.faces[i][2]];

            triangles.setCoordinate(3 * i, p1);
            triangles.setCoordinate(3 * i + 1, p2);
            triangles.setCoordinate(3 * i + 2, p3);
        }

        s = new Shape3D(triangles);
        s.setAppearance(createAppearanceTriangle());
        bg.addChild(s);

        lines = new LineArray(numTriangles * 6, GeometryArray.COORDINATES);

        for(int i = 0; i < numTriangles; i++) {
            for(int j = 0; j < 3; j++){
                lines.setCoordinate(6 * i + j * 2, pointArray[mesh.faces[i][j]]);
                lines.setCoordinate(6 * i + j * 2 + 1, pointArray[mesh.faces[i][(j + 1) % 3]]);
            }
        }

        s = new Shape3D(lines);
        s.setAppearance(createAppearance());
        bg.addChild(s);


        return bg;
    }

    Appearance createAppearance(){
        Appearance app = new Appearance( );

        // assign a Material to the Appearance.
        Color3f color = new Color3f(0.0f, 0.0f, 0.0f);

        ColoringAttributes ca = new ColoringAttributes(color, ColoringAttributes.NICEST);

        app.setColoringAttributes(ca);

        return app;
    }

    Appearance createAppearanceTriangle(){
        Appearance app = new Appearance( );


        TransparencyAttributes ta = new TransparencyAttributes();

        // 透過モードと透過率の設定
        ta.setTransparencyMode(TransparencyAttributes.BLENDED);
        ta.setTransparency(0.0f); 

        // Appearance に TransparencyAttributes を設定
        app.setTransparencyAttributes(ta);


        // assign a Material to the Appearance.
        Color3f color = new Color3f(0.4f, 0.4f, 0.4f);

        ColoringAttributes ca = new ColoringAttributes(color, ColoringAttributes.NICEST);

        app.setColoringAttributes(ca);

        return app;
    }

    TransformGroup createBehavior( ) {

        TransformGroup tg = new TransformGroup();

        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        // Create the rotate behavior node
        MouseRotate behavior1 = new MouseRotate();
        behavior1.setTransformGroup(tg);
        tg.addChild(behavior1);
        behavior1.setSchedulingBounds(getBoundingSphere( ));

        // Create the zoom behavior node
        MouseZoom behavior2 = new MouseZoom();
        behavior2.setTransformGroup(tg);
        tg.addChild(behavior2);
        behavior2.setSchedulingBounds(getBoundingSphere( ));

        // Create the translate behavior node
        MouseTranslate behavior3 = new MouseTranslate();
        behavior3.setTransformGroup(tg);
        tg.addChild(behavior3);
        behavior3.setSchedulingBounds(getBoundingSphere( ));

        return tg;
    }

    public void addLights( BranchGroup bg ) {
    
        // create the color for the light
        Color3f color = new Color3f( 0.5f,0.5f,0.5f );

        // create a vector that describes the direction that
        // the light is shining.
        Vector3f direction  = new Vector3f( 0.0f, 0.0f, -1.0f );

        // create the directional light with the color and direction
        DirectionalLight light = new DirectionalLight( color, direction );

        // set the volume of influence of the light.
        // Only objects within the Influencing Bounds
        // will be illuminated.
        light.setInfluencingBounds( getBoundingSphere( ) );

        // BranchGroup lightGroup = new BranchGroup();
        // lightGroup.addChild(light);

        // add the light to the BranchGroup
        bg.addChild( light );
    }

    BoundingSphere getBoundingSphere( ){
        return new BoundingSphere( new Point3d( 0.0,0.0,0.0 ), 200.0 );
    }

    public static void usage(){
        System.out.println("Please specify one file.");
    }


    // public static void main(String[] args){
    //     if (args.length != 1) {
	// 		usage();
	// 		System.exit(1);
	// 	}

	// 	JFrame f = new Filled(args[0]);
	// 	f.setSize(800, 800);
	// 	f.setVisible(true);
	// 	f.setDefaultCloseOperation(EXIT_ON_CLOSE);

    //     // f.getContentPane().setBackground(Color.WHITE);
    // }
}
