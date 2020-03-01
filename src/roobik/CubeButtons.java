package roobik;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.Appearance;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/** GUI buttons */
public class CubeButtons {
    public TransformGroup tg;
    
    private static void addButtonIDs(TransformGroup rodzic, float x, float y){
        QuadArray plane;
        TransparencyAttributes ta = new TransparencyAttributes ();
        ta.setTransparencyMode (TransparencyAttributes.BLENDED);
        ta.setTransparency (1.0f);
       
        Appearance transparency = new Appearance();
        transparency.setTransparencyAttributes(ta);
        Shape3D[] button = new Shape3D[4];
        String[] IDs = {"reset", "random", "play", "undo"};
        
        for (int i=0; i<4; i++){
            plane = new QuadArray (4, QuadArray.COORDINATES);       
            plane.setCoordinate(0, new Point3f(x+0.13f+0.25f*i, y+0.12f, 0.1f));  
            plane.setCoordinate(1, new Point3f(x-0.09f+0.25f*i, y+0.12f, 0.1f));
            plane.setCoordinate(2, new Point3f(x-0.09f+0.25f*i, y-0.12f, 0.1f));
            plane.setCoordinate(3, new Point3f(x+0.13f+0.25f*i, y-0.12f, 0.1f));
            button[i] = new Shape3D(plane, transparency);
            button[i].setUserData(IDs[i]);
            rodzic.addChild(button[i]);
        }
    }
    
    
    public CubeButtons(){
        tg = new TransformGroup();
        TextureLoader loader;
        TransformGroup[] buttonTG = new TransformGroup[4];
        Texture2D[] texture = new Texture2D[4];

        //APPEARANCE
        TransparencyAttributes ta = new TransparencyAttributes ();
        ta.setTransparencyMode (TransparencyAttributes.BLENDED);
        ta.setTransparency (0.5f);
       
        //MOVE CONTROLS
        float x = -1.8f;
        float y = -1.75f;
        Transform3D shift = new Transform3D();
        shift.set(new Vector3f(x,y,0.0f));
        
        //TEXTURE
        Appearance[] minicubeAppearance = new Appearance[4];
        for (int i=0; i<4; i+=1){
            minicubeAppearance[i] = new Appearance();
            minicubeAppearance[i].setTransparencyAttributes(ta);
        }
        
        String[] pathes = {"img/reset.png", "img/random.png", 
                           "img/play.png", "img/undo.png"};
        for (int i=0; i<4; i++){
            loader = new TextureLoader(pathes[i], null);
            ImageComponent2D image = loader.getImage();
            texture[i] = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                        image.getWidth(), image.getHeight());
            texture[i].setImage(0, image);
            texture[i].setBoundaryModeS(Texture.WRAP);
            texture[i].setBoundaryModeT(Texture.WRAP);
            minicubeAppearance[i].setTexture(texture[i]);
            
            shift.set(new Vector3f(x + 0.25f*i, y, 0.0f));
            buttonTG[i] = new TransformGroup();
            buttonTG[i].addChild(new Box(
                    0.15f, 0.15f, .0f, Box.GENERATE_NORMALS 
                            | Box.GENERATE_TEXTURE_COORDS, 
                    minicubeAppearance[i]));
            buttonTG[i].setTransform(shift);
            tg.addChild(buttonTG[i]);
        }
        
        //DODANIE KLIKALNYCH ELEMENTOW
        addButtonIDs(tg, x, y);    
    }
}
