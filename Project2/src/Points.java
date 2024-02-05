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

public class Points extends JPanel{
    Canvas3D canvas;
	BranchGroup scene = new BranchGroup();
	BranchGroup objRoot = new BranchGroup();

	private Point3d picked = new Point3d();

    PointArray points;

    SimpleUniverse u = null;

    Mesh mesh = new Mesh();

    Points(String filepath){
        mesh.fromObj(filepath);

        points = new PointArray(mesh.coords.length, PointArray.COORDINATES);

        Point3f[] pointArray = new Point3f[mesh.coords.length];
        for (int i = 0; i < mesh.coords.length; i++) {
            pointArray[i] = new Point3f(mesh.coords[i].x, mesh.coords[i].y, mesh.coords[i].z);
        }
        points.setCoordinates(0, pointArray);

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

		canvas = new Canvas3D(config);

        setPreferredSize(new Dimension(1000,500));
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        // setBackground(Color.BLUE);

		u = new SimpleUniverse(canvas);

        View view = u.getViewer().getView();
        double frontClipDistance = 0.001;
        double backClipDistance = 1000.0;
        view.setFrontClipDistance(frontClipDistance);
        view.setBackClipDistance(backClipDistance);
        u.getViewingPlatform().setNominalViewingTransform();


        u.getViewingPlatform().setNominalViewingTransform();
       

        TransformGroup tg = createBehavior();

        tg.addChild(createSceneGraph());

        objRoot.addChild(tg);

        addLights( objRoot );

        // Set up the background
        Background bgNode = new Background(1.0f,1.0f,1.0f);
        bgNode.setApplicationBounds(getBoundingSphere( ));
        objRoot.addChild(bgNode);

        objRoot.compile();
        
        u.addBranchGraph( objRoot );

        System.out.println("AAAA");
    }

    public BranchGroup createSceneGraph(){
        Shape3D s = new Shape3D(points);
        s.setAppearance(createAppearance());
        BranchGroup bg = new BranchGroup();
        bg.addChild(s);

        return bg;
    }


    Appearance createAppearance(){
        Appearance app = new Appearance( );

        // assign a Material to the Appearance.
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(0.0f, 0.0f, 0.0f);

        app.setColoringAttributes(ca);

        PointAttributes pointAttributes = new PointAttributes();
        pointAttributes.setPointSize(2.0f);

        app.setPointAttributes(pointAttributes);

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
        Color3f color = new Color3f( 0.0f,0.0f,0.0f );

        // create a vector that describes the direction that
        // the light is shining.
        Vector3f direction  = new Vector3f( -1.0f,-1.0f,-1.0f );

        // create the directional light with the color and direction
        DirectionalLight light = new DirectionalLight( color, direction );

        // set the volume of influence of the light.
        // Only objects within the Influencing Bounds
        // will be illuminated.
        light.setInfluencingBounds( getBoundingSphere( ) );

        BranchGroup lightGroup = new BranchGroup();
        lightGroup.addChild(light);

        // add the light to the BranchGroup
        bg.addChild( lightGroup );
    }

    BoundingSphere getBoundingSphere( ){
        return new BoundingSphere( new Point3d( 0.0,0.0,0.0 ), 200.0 );
    }

    public static void usage(){
        System.out.println("Please specify one file.");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 描画コード

        

    }

    // public static void main(String[] args){
    //     if (args.length != 1) {
	// 		usage();
	// 		System.exit(1);
	// 	}

	// 	JFrame f = new Points(args[0]);
	// 	f.setSize(600, 600);
	// 	f.setVisible(true);
	// 	f.setDefaultCloseOperation(EXIT_ON_CLOSE);

    //     // f.getContentPane().setBackground(Color.WHITE);
    // }
}
