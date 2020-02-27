/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roobik;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

/**
 *
 * @author Gruby
 */
public class CubeCube {
    public CubeElement[] elemList;
    public TransformGroup tg;
    
    private void elementyKostki(TransformGroup parent) {       
        //KOLORY
        Color3f bc = new Color3f(.0f, .0f, .0f);
        Color3f gr = new Color3f(0.01f, 0.9f, 0.0f);
        Color3f rd = new Color3f(1.0f, 0.1f, 0.1f);
        Color3f wh = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f yl = new Color3f(1.0f, 1.0f, .0f);
        Color3f bl = new Color3f(0.1f, .1f, 1.0f);
        Color3f or = new Color3f(0.99f, .45f, .0f);
        Appearance green = new Appearance();
        Appearance red = new Appearance();
        Appearance blue = new Appearance();
        Appearance yellow = new Appearance();
        Appearance white = new Appearance();
        Appearance orange = new Appearance();
        Appearance black = new Appearance();
        
        ColoringAttributes cattr = new ColoringAttributes();
        cattr.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
        
        //MATERIALY KOSTEK
        Material redMaterial = new Material(rd, bc, rd, bc, 40.0f);
        red.setColoringAttributes(cattr);
        red.setMaterial(redMaterial);
        
        Material greenMaterial = new Material(gr, bc, gr, bc, 40.0f);
        green.setColoringAttributes(cattr);
        green.setMaterial(greenMaterial);
        
        Material blueMaterriel = new Material(bl, bc, bl, bc, 40.0f);
        blue.setColoringAttributes(cattr);
        blue.setMaterial(blueMaterriel);
        
        Material yellowMaterial = new Material(yl, bc, yl, or, 40.0f);
        yellow.setColoringAttributes(cattr);
        yellow.setMaterial(yellowMaterial);
        
        Material blackMaterial = new Material(bc, bc, bc, bc, 40.0f);
        black.setColoringAttributes(cattr);
        black.setMaterial(blackMaterial);
        
        Material orangeMaterial = new Material(or, bc,or, bc ,40.0f);
        orange.setColoringAttributes(cattr);
        orange.setMaterial(orangeMaterial);
        
        Material whiteMaterial = new Material(wh, bc ,wh,bc, 40.0f);
        white.setColoringAttributes(cattr);
        white.setMaterial(whiteMaterial);     
          
        elemList = new CubeElement[26];
        
        //LAYER 1                                                               FRONT | TOP | BOTTOM | RIGHT | LEFT | BACK
        elemList[0] = new CubeElement(0,  new Vector3f(-0.51f,+0.51f,+0.51f),   red, white, black, black, green, black);      
        elemList[1] = new CubeElement(1,  new Vector3f(0.0f,+0.51f,+0.51f),     red, white, black, black, black, black);        
        elemList[2] = new CubeElement(2,  new Vector3f(+0.51f,+0.51f,+0.51f),   red, white, black, blue, black, black);
        elemList[3] = new CubeElement(3,  new Vector3f(-0.51f,.0f,+0.51f),      red, black, black, black, green, black);
        elemList[4] = new CubeElement(4,  new Vector3f(0.0f,0.0f,+0.51f),       red, black, black, black, black, black);
        elemList[5] = new CubeElement(5,  new Vector3f(+0.51f,.0f,+0.51f),      red, black, black, blue, black, black);
        elemList[6] = new CubeElement(6,  new Vector3f(-0.51f,-0.51f,+0.51f),   red, black, yellow, black, green, black);
        elemList[7] = new CubeElement(7,  new Vector3f(-0.00f,-0.51f,+0.51f),   red, black, yellow, black, black, black);
        elemList[8] = new CubeElement(8,  new Vector3f(+0.51f,-0.51f,+0.51f),   red, black, yellow, blue, black, black);
        //LAYER 2                                                               FRONT | TOP | BOTTOM | RIGHT | LEFT | BACK
        elemList[9] = new CubeElement(9,  new Vector3f(-0.51f,+0.51f,.0f),      black, white, black, black,   green, black);
        elemList[10] = new CubeElement(10, new Vector3f(-0.0f,+0.51f,.0f),      black, white, black, black,   black, black);
        elemList[11] = new CubeElement(11, new Vector3f(+0.51f,+0.51f,.0f),     black, white, black, blue,  black, black);
        elemList[12] = new CubeElement(12, new Vector3f(-0.51f,.0f,.0f),        black, black, black, black,  green, black);
        elemList[13] = new CubeElement(13, new Vector3f(+0.51f,.0f,.0f),        black, black, black, blue, black, black);
        elemList[14] = new CubeElement(14, new Vector3f(-0.51f,-0.51f,.0f),     black, black, yellow, black,   green, black);
        elemList[15] = new CubeElement(15, new Vector3f(-0.00f,-0.51f,.0f),     black, black, yellow, black,   black, black);
        elemList[16] = new CubeElement(16, new Vector3f(+0.51f,-0.51f,.0f),     black, black, yellow, blue,  black, black);
        //LAYER 3                                                               FRONT | TOP | BOTTOM | RIGHT | LEFT | BACK
        elemList[17] = new CubeElement(17, new Vector3f(-0.51f,+0.51f,-0.51f),  black, white, black,  black,  green, orange);        
        elemList[18] = new CubeElement(18, new Vector3f(0.0f,+0.51f,-0.51f),    black, white, black,  black,  black, orange);        
        elemList[19] = new CubeElement(19, new Vector3f(+0.51f,+0.51f,-0.51f),  black, white, black,  blue, black, orange);
        elemList[20] = new CubeElement(20, new Vector3f(-0.51f,0.0f,-0.51f),    black, black, black, black,  green, orange);
        elemList[21] = new CubeElement(21, new Vector3f(0.0f,0.0f,-0.51f),      black, black, black, black,  black, orange);
        elemList[22] = new CubeElement(22, new Vector3f(+0.51f,0.0f,-0.51f),    black, black, black, blue, black, orange);
        elemList[23] = new CubeElement(23, new Vector3f(-0.51f,-0.51f,-0.51f),  black, black, yellow,  black,  green, orange);
        elemList[24] = new CubeElement(24, new Vector3f(0.0f,-0.51f,-0.51f),    black, black, yellow,  black,  black, orange);
        elemList[25] = new CubeElement(25, new Vector3f(+0.51f,-0.51f,-0.51f),  black, black, yellow,  blue, black, orange);
        
        for (int i=0; i < 26; i++)
            parent.addChild(elemList[i].tg);
    }
    
    public void transformsReset(){
        for (int i=0; i<26; i++)
            elemList[i].reset();  
    }
    
    public CubeCube(){
        tg = new TransformGroup();
        elementyKostki(tg);
    }
}
