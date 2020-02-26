/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roobik;

import javax.media.j3d.Appearance;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import static javax.media.j3d.TransparencyAttributes.BLENDED;
import javax.vecmath.Point3f;

/**
 *
 * @author Gruby
 */
public class CubePlanes {
    public TransformGroup tg;
    
    private Appearance getAppearance(){
        Appearance transparency = new Appearance();
        TransparencyAttributes transparencyAttr = new TransparencyAttributes();
        transparencyAttr.setTransparencyMode(BLENDED);
        transparencyAttr.setTransparency(1.0f);
        transparency.setTransparencyAttributes(transparencyAttr);
        return transparency;
    }
    
    public CubePlanes(){
        tg = new TransformGroup();
        Shape3D[] plane = new Shape3D[6];
        
        Appearance przezroczystosc = getAppearance();
        QuadArray planeCoords = new QuadArray (4, QuadArray.COORDINATES);       
        planeCoords.setCoordinate(0, new Point3f(-0.77f, -0.77f, -0.77f));  
        planeCoords.setCoordinate(1, new Point3f(-0.77f, -0.77f, +0.77f));
        planeCoords.setCoordinate(2, new Point3f(-0.77f, 0.77f, +0.77f));
        planeCoords.setCoordinate(3, new Point3f(-0.77f, 0.77f, -0.77f));
        plane[0] = new Shape3D(planeCoords, przezroczystosc);
        plane[0].setUserData("face0");
        //1
        planeCoords = new QuadArray (4, QuadArray.COORDINATES);
        planeCoords.setCoordinate(0, new Point3f(-0.77f, -0.77f, +0.77f));  
        planeCoords.setCoordinate(1, new Point3f(0.77f, -0.77f, +0.77f));
        planeCoords.setCoordinate(2, new Point3f(0.77f, 0.77f, +0.77f));
        planeCoords.setCoordinate(3, new Point3f(-0.77f, 0.77f, +0.77f));
        plane[1] = new Shape3D(planeCoords, przezroczystosc);
        plane[1].setUserData("face1");
        //2
        planeCoords = new QuadArray (4, QuadArray.COORDINATES);
        planeCoords.setCoordinate(0, new Point3f(0.77f, +0.77f, -0.77f));  
        planeCoords.setCoordinate(1, new Point3f(0.77f, +0.77f, 0.77f));
        planeCoords.setCoordinate(2, new Point3f(0.77f, -0.77f, 0.77f));
        planeCoords.setCoordinate(3, new Point3f(0.77f, -0.77f, -0.77f));
        plane[2] = new Shape3D(planeCoords, przezroczystosc);
        plane[2].setUserData("face2");
        //3
        planeCoords = new QuadArray (4, QuadArray.COORDINATES);
        planeCoords.setCoordinate(0, new Point3f(+0.77f, -0.77f, -0.77f)); 
        planeCoords.setCoordinate(1, new Point3f(-0.77f, -0.77f, -0.77f));
        planeCoords.setCoordinate(2, new Point3f(-0.77f, 0.77f, -0.77f));
        planeCoords.setCoordinate(3, new Point3f(+0.77f, 0.77f, -0.77f));
        plane[3] = new Shape3D(planeCoords, przezroczystosc);
        plane[3].setUserData("face3");
        //4
        planeCoords = new QuadArray (4, QuadArray.COORDINATES);
        planeCoords.setCoordinate(0, new Point3f(-0.77f, 0.77f, +0.77f)); 
        planeCoords.setCoordinate(1, new Point3f(0.77f, 0.77f, +0.77f));
        planeCoords.setCoordinate(2, new Point3f(0.77f, 0.77f, -0.77f));
        planeCoords.setCoordinate(3, new Point3f(-0.77f, 0.77f, -0.77f));
        plane[4] = new Shape3D(planeCoords, przezroczystosc);
        plane[4].setUserData("face4");
        //5
        planeCoords = new QuadArray (4, QuadArray.COORDINATES);
        planeCoords.setCoordinate(0, new Point3f(-0.77f, -0.77f, -0.77f));
        planeCoords.setCoordinate(1, new Point3f(0.77f, -0.77f, -0.77f));
        planeCoords.setCoordinate(2, new Point3f(0.77f, -0.77f, +0.77f));
        planeCoords.setCoordinate(3, new Point3f(-0.77f, -0.77f, +0.77f));
        plane[5] = new Shape3D(planeCoords, przezroczystosc);
        plane[5].setUserData("face5"); 
        
        for(int i = 0; i < 6; i++)
            tg.addChild(plane[i]);
    }
}
