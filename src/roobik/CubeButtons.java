/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author Gruby
 */
public class CubeButtons {
    public TransformGroup tg;
    
    private static void dodajGuzioryID(TransformGroup rodzic, float x, float y){
        TransparencyAttributes ta = new TransparencyAttributes ();
        ta.setTransparencyMode (TransparencyAttributes.BLENDED);
        ta.setTransparency (1.0f);
       
        Appearance przezr = new Appearance();
        przezr.setTransparencyAttributes(ta);
        Shape3D[] przycisk = new Shape3D[3];
        QuadArray plane = new QuadArray (4, QuadArray.COORDINATES);       
        plane.setCoordinate(0, new Point3f(x+0.13f, y+0.12f, 0.1f));  
        plane.setCoordinate(1, new Point3f(x-0.09f,       y+0.12f, 0.1f));
        plane.setCoordinate(2, new Point3f(x-0.09f,       y-0.12f, 0.1f));
        plane.setCoordinate(3, new Point3f(x+0.13f, y-0.12f, 0.1f));
        przycisk[0] = new Shape3D(plane, przezr);
        przycisk[0].setUserData("r");
        rodzic.addChild(przycisk[0]);
        
        plane = new QuadArray (4, QuadArray.COORDINATES);       
        plane.setCoordinate(0, new Point3f(x+0.25f+0.13f, y+0.12f, 0.1f));  
        plane.setCoordinate(1, new Point3f(x+0.25f-0.09f,       y+0.12f, 0.1f));
        plane.setCoordinate(2, new Point3f(x+0.25f-0.09f,       y-0.12f, 0.1f));
        plane.setCoordinate(3, new Point3f(x+0.25f+0.13f, y-0.12f, 0.1f));
        przycisk[1] = new Shape3D(plane, przezr);
        przycisk[1].setUserData("t");
        rodzic.addChild(przycisk[1]);
        
        plane = new QuadArray (4, QuadArray.COORDINATES);       
        plane.setCoordinate(0, new Point3f(x+0.5f+0.13f, y+0.12f, 0.1f));  
        plane.setCoordinate(1, new Point3f(x+0.5f-0.09f,       y+0.12f, 0.1f));
        plane.setCoordinate(2, new Point3f(x+0.5f-0.09f,       y-0.12f, 0.1f));
        plane.setCoordinate(3, new Point3f(x+0.5f+0.13f, y-0.12f, 0.1f));
        przycisk[2] = new Shape3D(plane, przezr);
        przycisk[2].setUserData("p");
        rodzic.addChild(przycisk[2]);
    }
    
    
    public CubeButtons(){
        tg = new TransformGroup();
        
        TransformGroup[] przyciskKostkaTG = new TransformGroup[3];
        Box[] miniKostka = new Box[3];

        //APPEARANCE
        TransparencyAttributes ta = new TransparencyAttributes ();
        ta.setTransparencyMode (TransparencyAttributes.BLENDED);
        ta.setTransparency (0.5f);
       
        Appearance miniKostkaApp = new Appearance();
        Appearance miniKostkaApp2 = new Appearance();
        Appearance miniKostkaApp3 = new Appearance();   
        
        //PRZESUNIECIE KONTROLEK
        float x = -1.8f;
        float y = -1.75f;
        Transform3D przesuniecie = new Transform3D();
        przesuniecie.set(new Vector3f(x,y,0.0f));
        
        //TEXTURA
        miniKostkaApp.setTransparencyAttributes(ta);
        miniKostkaApp2.setTransparencyAttributes(ta);
        miniKostkaApp3.setTransparencyAttributes(ta);

        //KOSTKA1
        TextureLoader loader = new TextureLoader("obraski/reset.png",null);
        ImageComponent2D image = loader.getImage();
        Texture2D text = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                    image.getWidth(), image.getHeight());
        text.setImage(0, image);
        text.setBoundaryModeS(Texture.WRAP);
        text.setBoundaryModeT(Texture.WRAP);
        miniKostkaApp.setTexture(text);
        
        miniKostka[0] = new Box(0.15f, 0.15f, .0f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, miniKostkaApp);
        przyciskKostkaTG[0] = new TransformGroup();
        przyciskKostkaTG[0].addChild(miniKostka[0]);
        przyciskKostkaTG[0].setTransform(przesuniecie);
        tg.addChild(przyciskKostkaTG[0]);
        
        //KOSTKA 2
        TextureLoader loader2 = new TextureLoader("obraski/losuj.png",null);
        ImageComponent2D image2 = loader2.getImage();
        Texture2D text2 = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                    image2.getWidth(), image2.getHeight());
        text2.setImage(0, image2);
        text2.setBoundaryModeS(Texture.WRAP);
        text2.setBoundaryModeT(Texture.WRAP);
        miniKostkaApp2.setTexture(text2);
        
        przesuniecie.set(new Vector3f(x + 0.25f,y,0.0f));
        miniKostka[1] = new Box(0.15f, 0.15f, .0f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, miniKostkaApp2);
        przyciskKostkaTG[1] = new TransformGroup();
        przyciskKostkaTG[1].addChild(miniKostka[1]);
        przyciskKostkaTG[1].setTransform(przesuniecie);
        tg.addChild(przyciskKostkaTG[1]);
        
        //KOSTKA 3
        TextureLoader loader3 = new TextureLoader("obraski/odtwarzaj.png",null);
        ImageComponent2D image3 = loader3.getImage();
        Texture2D text3 = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                                    image3.getWidth(), image3.getHeight());
        text3.setImage(0, image3);
        text3.setBoundaryModeS(Texture.WRAP);
        text3.setBoundaryModeT(Texture.WRAP);               
        miniKostkaApp3.setTexture(text3);
        
        przesuniecie.set(new Vector3f(x + 0.50f,y,0.0f));
        miniKostka[2] = new Box(0.15f, 0.15f, .0f, Box.GENERATE_NORMALS | Box.GENERATE_TEXTURE_COORDS, miniKostkaApp3);
        przyciskKostkaTG[2] = new TransformGroup();
        przyciskKostkaTG[2].addChild(miniKostka[2]);
        przyciskKostkaTG[2].setTransform(przesuniecie);
        tg.addChild(przyciskKostkaTG[2]);
        
        //DODANIE KLIKALNYCH ELEMENTOW
        
        dodajGuzioryID(tg, x, y);
        
    
    }
}
