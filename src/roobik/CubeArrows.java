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
import javax.media.j3d.Transform3D;
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
    
    public void setTransform(Transform3D rotation){
        tg.setTransform(rotation);
    }
    
    private Appearance getAppearance(){
        Appearance appearance = new Appearance();
        ColoringAttributes coloring = new ColoringAttributes(
                new Color3f(0.15f, 0.1f, 0.3f), NICEST);
        appearance.setColoringAttributes(coloring);
        TransparencyAttributes transparency = new TransparencyAttributes();
        transparency.setTransparencyMode(BLENDED);
        transparency.setTransparency(0.45f);
        appearance.setTransparencyAttributes(transparency);
        return appearance;   
    }
    
    public CubeArrows(){
        tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        
        //USTAWIENIE APPEARANCE DLA STRZALEK
        Appearance arrowTransparency = getAppearance();
        
        TriangleArray wsp;
        Shape3D[] arrow = new Shape3D[12];

        for(int i = 0; i < 3; i++){
        wsp = new TriangleArray(3, TriangleArray.COORDINATES);
        wsp.setCoordinate(2, new Point3f(-0.71f + i*0.51f, 0.78f, +0.77f)); 
        wsp.setCoordinate(1, new Point3f(-0.51f + i*0.51f, 0.98f, +0.77f));
        wsp.setCoordinate(0, new Point3f(-0.31f + i*0.51f, 0.78f, +0.77f));
        arrow[i] = new Shape3D(wsp, arrowTransparency);
        }
        arrow[0].setUserData("up0");
        arrow[1].setUserData("up1");
        arrow[2].setUserData("up2");
        
        for(int i = 3; i < 6; i++){
        wsp = new TriangleArray(3, TriangleArray.COORDINATES);
        wsp.setCoordinate(2, new Point3f(0.78f, +0.71f - (i-3)*0.51f, +0.77f)); 
        wsp.setCoordinate(1, new Point3f(0.98f, +0.51f - (i-3)*0.51f, +0.77f));
        wsp.setCoordinate(0, new Point3f(0.78f, +0.31f - (i-3)*0.51f, +0.77f)); 
        arrow[i] = new Shape3D(wsp, arrowTransparency);
        }
        arrow[3].setUserData("right0");
        arrow[4].setUserData("right1");
        arrow[5].setUserData("right2");
        
        for(int i = 6; i < 9; i++){
            wsp = new TriangleArray(3, TriangleArray.COORDINATES);
            wsp.setCoordinate(2, new Point3f(+0.71f - (i-6)*0.51f, -0.78f, +0.77f)); 
            wsp.setCoordinate(1, new Point3f(+0.51f - (i-6)*0.51f, -0.98f, +0.77f));
            wsp.setCoordinate(0, new Point3f(+0.31f - (i-6)*0.51f, -0.78f, +0.77f)); 
            arrow[i] = new Shape3D(wsp, arrowTransparency);
            }
        arrow[6].setUserData("down2");
        arrow[7].setUserData("down1");
        arrow[8].setUserData("down0");
        
        for(int i = 9; i < 12; i++){
            wsp = new TriangleArray(3, TriangleArray.COORDINATES);
            wsp.setCoordinate(2, new Point3f(-0.78f, -0.71f + (i-9)*0.51f, +0.77f)); 
            wsp.setCoordinate(1, new Point3f(-0.98f, -0.51f + (i-9)*0.51f, +0.77f));
            wsp.setCoordinate(0, new Point3f(-0.78f, -0.31f + (i-9)*0.51f, +0.77f)); 
            arrow[i] = new Shape3D(wsp, arrowTransparency);
            }
        arrow[9].setUserData("left2");
        arrow[10].setUserData("left1");
        arrow[11].setUserData("left0");
        
        for(int i = 0; i < 12; i++)
            tg.addChild(arrow[i]);
    }
}
