package roobik;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

/** 3D model of the cube */
public class Cube { 
    public BranchGroup sceneGraph;
    private CubeArrows cubeArrows;
    private CubeCube cube;

    void rotateElement(int id, Transform3D rotation){
       cube.elemList[id].mulTransform(rotation);
   }
       
    void elementsReset(){
        cube.transformsReset();
    }
      
    void rotateArrows(Transform3D rotation){
        cubeArrows.setTransform(rotation);
    }     
    
    private TransformGroup cubeTransformGroup() {
        TransformGroup tgCube = new TransformGroup();
        tgCube.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        //ELEMENTS OF THE CUBE
        cube = new CubeCube();
        tgCube.addChild(cube.tg);
        
        //ARROWS
        cubeArrows = new CubeArrows();
        tgCube.addChild(cubeArrows.tg);
               
        //PLANES
        CubePlanes cubePlanes = new CubePlanes();
        tgCube.addChild(cubePlanes.tg);

        return tgCube;
    }
    
    private TransformGroup guiTransformGroup() {
        //GUI
        TransformGroup tgGUI = new TransformGroup();
        tgGUI.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        //BUTTONS       
        CubeButtons cubeButtons = new CubeButtons();
        tgGUI.addChild(cubeButtons.tg);
        
        
        
        return tgGUI;
    }
    
    final BranchGroup createSceneGraph(BoundingSphere bound){
        BranchGroup graph = new BranchGroup();

        //TEXTURE
        TextureLoader bgTexture = new TextureLoader("img/background.jpg", null);
	Background bg = new Background(bgTexture.getImage());
	bg.setApplicationBounds(bound);
	graph.addChild(bg);
        
        //LIGHTS
        Vector3f vectorDir  = new Vector3f(-8.0f, -5.0f, -9.0f);
	Color3f kolorDir = new Color3f(0.45f, .4f, 0.45f);
        DirectionalLight swiatloDir = new DirectionalLight(kolorDir, vectorDir);
	swiatloDir.setInfluencingBounds(bound);
        graph.addChild(swiatloDir);
        Color3f kolorAmbient = new Color3f(0.5f, 0.4f, 0.3f);
        AmbientLight swiatloAmbient = new AmbientLight(kolorAmbient);
        swiatloAmbient.setInfluencingBounds(bound);
        graph.addChild(swiatloAmbient);
        
        return graph;
    }
    

    public Cube(){
        BoundingSphere bound = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        sceneGraph = createSceneGraph(bound);
               
        //CUBE
        TransformGroup cubeTG = cubeTransformGroup();
        
        //GUI
        TransformGroup guiTG = guiTransformGroup();
        guiTG.addChild(cubeTG);
        sceneGraph.addChild(guiTG); 
        
        //MOUSE
        MouseRotate mouseRotate = new MouseRotate();
        mouseRotate.setTransformGroup(cubeTG);
        mouseRotate.setSchedulingBounds(bound);
        sceneGraph.addChild(mouseRotate);
        
        //GUI
    }
}
