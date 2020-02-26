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
import static roobik.Roobik.currentLayout;

/**
 * Klasa obslugujaca inicjalizacje elementow kostki, a takze losowanie jej ukladu
 * oraz przywrocenie go do stanu poczatkowego
 * @author PotezneSzwagry
 */


public class Kostka { 
   public BranchGroup sceneGraph;
   private CubeArrows cubeArrows;
   public TransformGroup tgArrows;
   private CubeCube cube;

   void rotateElement(int id, Transform3D rotation){
       cube.elemList[id].mulTransform(rotation);
   }
       
    /** 
     * Przywrocenie ukladu kostki do jego stanu poczatkowego 
     */
    void resetKostki(){
        for (int i=0; i<26; i++){
            cube.elemList[i].reset(); 
            currentLayout[i]=i; 
        }
    }
      

    void rotateArrows(Transform3D rotation){
        cubeArrows.tg.setTransform(rotation);
    }     
    
    private void addElemsSceneGraph(TransformGroup tgCube, BoundingSphere bound) {

        //STRZALKI
        cubeArrows = new CubeArrows();
        tgCube.addChild(cubeArrows.tg);
        
        //GUI
        TransformGroup gui = new TransformGroup();
        gui.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        //STWORZENIE KOSTKI
        CubePlanes cubePlanes = new CubePlanes();
        tgCube.addChild(cubePlanes.tg);
        tgCube.addChild(cube.tg);
        
        //DZIEN DOBRY Z TEJ STRONY MYSZ
        MouseRotate myszObr = new MouseRotate();
        myszObr.setTransformGroup(tgCube);
        myszObr.setSchedulingBounds(bound);
        sceneGraph.addChild(myszObr);
 
        //DODANIE ELEMENTOW DO GRAFU        
        CubeButtons cubeButtons = new CubeButtons();
        gui.addChild(cubeButtons.tg);
        
        gui.addChild(tgCube);
        
        sceneGraph.addChild(gui);
        
    }
    
    final BranchGroup createSceneGraph(BoundingSphere bound){
        BranchGroup grafS = new BranchGroup();

        //TEKSTURA
        TextureLoader bgTexture = new TextureLoader("obraski/paweljumper.jpg", null);
	Background bg = new Background(bgTexture.getImage());
	bg.setApplicationBounds(bound);
	grafS.addChild(bg);
        
        //USTAWIENIE SWIATEL
        Vector3f vectorDir  = new Vector3f(-8.0f, -5.0f, -9.0f);
	Color3f kolorDir = new Color3f(0.45f, .4f, 0.45f);
        DirectionalLight swiatloDir = new DirectionalLight(kolorDir, vectorDir);
	swiatloDir.setInfluencingBounds(bound);
        grafS.addChild(swiatloDir);
        Color3f kolorAmbient = new Color3f(0.5f, 0.4f, 0.3f);
        AmbientLight swiatloAmbient = new AmbientLight(kolorAmbient);
        swiatloAmbient.setInfluencingBounds(bound);
        grafS.addChild(swiatloAmbient);
        
        return grafS;
    }
    

    public Kostka(){
        cube = new CubeCube();
        TransformGroup tgCube = new TransformGroup();
        tgCube.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        BoundingSphere bound = new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

        sceneGraph = createSceneGraph(bound);
        addElemsSceneGraph(tgCube, bound);
        
    }
    
    
}
