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
        //USTAWIENIE APPEARANCE DLA PLASZCZYZN
        Appearance przezroczystosc = new Appearance();
        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setTransparencyMode(BLENDED);
        ta.setTransparency(1.0f);
        przezroczystosc.setTransparencyAttributes(ta);
        return przezroczystosc;
    }
    
    public CubePlanes(){
        tg = new TransformGroup();
        Shape3D[] plaszczyzna = new Shape3D[6];
        
        Appearance przezroczystosc = getAppearance();
        QuadArray plane = new QuadArray (4, QuadArray.COORDINATES);       
        plane.setCoordinate(0, new Point3f(-0.77f, -0.77f, -0.77f));  
        plane.setCoordinate(1, new Point3f(-0.77f, -0.77f, +0.77f));
        plane.setCoordinate(2, new Point3f(-0.77f, 0.77f, +0.77f));
        plane.setCoordinate(3, new Point3f(-0.77f, 0.77f, -0.77f));
        plaszczyzna[0] = new Shape3D(plane, przezroczystosc);
        plaszczyzna[0].setUserData("0");
        //1
        plane = new QuadArray (4, QuadArray.COORDINATES);
        plane.setCoordinate(0, new Point3f(-0.77f, -0.77f, +0.77f));  
        plane.setCoordinate(1, new Point3f(0.77f, -0.77f, +0.77f));
        plane.setCoordinate(2, new Point3f(0.77f, 0.77f, +0.77f));
        plane.setCoordinate(3, new Point3f(-0.77f, 0.77f, +0.77f));
        plaszczyzna[1] = new Shape3D(plane, przezroczystosc);
        plaszczyzna[1].setUserData("1");
        //2
        plane = new QuadArray (4, QuadArray.COORDINATES);
        plane.setCoordinate(0, new Point3f(0.77f, +0.77f, -0.77f));  
        plane.setCoordinate(1, new Point3f(0.77f, +0.77f, 0.77f));
        plane.setCoordinate(2, new Point3f(0.77f, -0.77f, 0.77f));
        plane.setCoordinate(3, new Point3f(0.77f, -0.77f, -0.77f));
        plaszczyzna[2] = new Shape3D(plane, przezroczystosc);
        plaszczyzna[2].setUserData("2");
        //3
        plane = new QuadArray (4, QuadArray.COORDINATES);
        plane.setCoordinate(0, new Point3f(+0.77f, -0.77f, -0.77f)); 
        plane.setCoordinate(1, new Point3f(-0.77f, -0.77f, -0.77f));
        plane.setCoordinate(2, new Point3f(-0.77f, 0.77f, -0.77f));
        plane.setCoordinate(3, new Point3f(+0.77f, 0.77f, -0.77f));
        plaszczyzna[3] = new Shape3D(plane, przezroczystosc);
        plaszczyzna[3].setUserData("3");
        //4
        plane = new QuadArray (4, QuadArray.COORDINATES);
        plane.setCoordinate(0, new Point3f(-0.77f, 0.77f, +0.77f)); 
        plane.setCoordinate(1, new Point3f(0.77f, 0.77f, +0.77f));
        plane.setCoordinate(2, new Point3f(0.77f, 0.77f, -0.77f));
        plane.setCoordinate(3, new Point3f(-0.77f, 0.77f, -0.77f));
        plaszczyzna[4] = new Shape3D(plane, przezroczystosc);
        plaszczyzna[4].setUserData("4");
        //5
        plane = new QuadArray (4, QuadArray.COORDINATES);
        plane.setCoordinate(0, new Point3f(-0.77f, -0.77f, -0.77f));
        plane.setCoordinate(1, new Point3f(0.77f, -0.77f, -0.77f));
        plane.setCoordinate(2, new Point3f(0.77f, -0.77f, +0.77f));
        plane.setCoordinate(3, new Point3f(-0.77f, -0.77f, +0.77f));
        plaszczyzna[5] = new Shape3D(plane, przezroczystosc);
        plaszczyzna[5].setUserData("5"); 
        
        for(int i = 0; i < 6; i++)
            tg.addChild(plaszczyzna[i]);
    }
}
