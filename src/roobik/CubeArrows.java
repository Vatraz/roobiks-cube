/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roobik;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import static javax.media.j3d.ColoringAttributes.NICEST;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import static javax.media.j3d.TransparencyAttributes.BLENDED;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

/**
 *
 * @author Gruby
 */
public class CubeArrows {
    public TransformGroup tg;
    
    private Appearance getAppearance(){
        Appearance przezroczystoscS = new Appearance();
        ColoringAttributes ca = new ColoringAttributes(new Color3f(0.15f, 0.1f, 0.3f), NICEST);
        przezroczystoscS.setColoringAttributes(ca);
        TransparencyAttributes taS = new TransparencyAttributes();
        taS.setTransparencyMode(BLENDED);
        taS.setTransparency(0.45f);
        przezroczystoscS.setTransparencyAttributes(taS);
        return przezroczystoscS;   
    }
    
    public CubeArrows(){
        tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        //USTAWIENIE APPEARANCE DLA STRZALEK
        Appearance przezroczystoscS = getAppearance();
        
        TriangleArray wsp;
        Shape3D[] strzalka = new Shape3D[12];

        for(int i = 0; i < 3; i++){
        wsp = new TriangleArray(3, TriangleArray.COORDINATES);
        wsp.setCoordinate(2, new Point3f(-0.71f + i*0.51f, 0.78f, +0.77f)); 
        wsp.setCoordinate(1, new Point3f(-0.51f + i*0.51f, 0.98f, +0.77f));
        wsp.setCoordinate(0, new Point3f(-0.31f + i*0.51f, 0.78f, +0.77f));
        strzalka[i] = new Shape3D(wsp, przezroczystoscS);
        }
        strzalka[0].setUserData("s0");
        strzalka[1].setUserData("s1");
        strzalka[2].setUserData("s2");
        
        for(int i = 3; i < 6; i++){
        wsp = new TriangleArray(3, TriangleArray.COORDINATES);
        wsp.setCoordinate(2, new Point3f(0.78f, +0.71f - (i-3)*0.51f, +0.77f)); 
        wsp.setCoordinate(1, new Point3f(0.98f, +0.51f - (i-3)*0.51f, +0.77f));
        wsp.setCoordinate(0, new Point3f(0.78f, +0.31f - (i-3)*0.51f, +0.77f)); 
        strzalka[i] = new Shape3D(wsp, przezroczystoscS);
        }
        strzalka[3].setUserData("s3");
        strzalka[4].setUserData("s4");
        strzalka[5].setUserData("s5");
        
        for(int i = 6; i < 9; i++){
            wsp = new TriangleArray(3, TriangleArray.COORDINATES);
            wsp.setCoordinate(2, new Point3f(+0.71f - (i-6)*0.51f, -0.78f, +0.77f)); 
            wsp.setCoordinate(1, new Point3f(+0.51f - (i-6)*0.51f, -0.98f, +0.77f));
            wsp.setCoordinate(0, new Point3f(+0.31f - (i-6)*0.51f, -0.78f, +0.77f)); 
            strzalka[i] = new Shape3D(wsp, przezroczystoscS);
            }
        strzalka[6].setUserData("s6");
        strzalka[7].setUserData("s7");
        strzalka[8].setUserData("s8");
        
        for(int i = 9; i < 12; i++){
            wsp = new TriangleArray(3, TriangleArray.COORDINATES);
            wsp.setCoordinate(2, new Point3f(-0.78f,-0.71f + (i-9)*0.51f, +0.77f)); 
            wsp.setCoordinate(1, new Point3f(-0.98f,-0.51f + (i-9)*0.51f, +0.77f));
            wsp.setCoordinate(0, new Point3f(-0.78f,-0.31f + (i-9)*0.51f, +0.77f)); 
            strzalka[i] = new Shape3D(wsp, przezroczystoscS);
            }
        strzalka[9].setUserData("s9");
        strzalka[10].setUserData("s10");
        strzalka[11].setUserData("s11");
        
        for(int i = 0; i < 12; i++)
            tg.addChild(strzalka[i]);
    
    
    }
}
