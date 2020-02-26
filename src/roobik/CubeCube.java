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
import static roobik.Roobik.currentLayout;

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
        Appearance zielen = new Appearance();
        Appearance czerwien = new Appearance();
        Appearance blekit = new Appearance();
        Appearance zolc = new Appearance();
        Appearance biel = new Appearance();
        Appearance pomarancz = new Appearance();
        Appearance czern = new Appearance();
        
        ColoringAttributes cattr = new ColoringAttributes();
        cattr.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
        
        //MATERIALY KOSTEK
        Material czerwienM = new Material(rd, bc, rd, bc, 40.0f);
        czerwien.setColoringAttributes(cattr);
        czerwien.setMaterial(czerwienM);
        
        Material zielenM = new Material(gr, bc, gr, bc, 40.0f);
        zielen.setColoringAttributes(cattr);
        zielen.setMaterial(zielenM);
        
        Material blekitM = new Material(bl, bc, bl, bc, 40.0f);
        blekit.setColoringAttributes(cattr);
        blekit.setMaterial(blekitM);
        
        Material zolcM = new Material(yl, bc, yl, or, 40.0f);
        zolc.setColoringAttributes(cattr);
        zolc.setMaterial(zolcM);
        
        Material czernM = new Material(bc, bc, bc, bc, 40.0f);
        czern.setColoringAttributes(cattr);
        czern.setMaterial(czernM);
        
        Material pomaranczM = new Material(or, bc,or, bc ,40.0f);
        pomarancz.setColoringAttributes(cattr);
        pomarancz.setMaterial(pomaranczM);
        
        Material bielM = new Material(wh, bc ,wh,bc, 40.0f);
        biel.setColoringAttributes(cattr);
        biel.setMaterial(bielM);
     
          
        elemList = new CubeElement[26]; 
        //WARSTWA 1                                               FRONT | TOP | BOTTOM | RIGHT | LEFT | BACK
        elemList[0] = new CubeElement(0,  new Vector3f(-0.51f,+0.51f,+0.51f),   czerwien, biel, czern, czern, zielen, czern);      
        elemList[1] = new CubeElement(1,  new Vector3f(0.0f,+0.51f,+0.51f),     czerwien, biel, czern, czern, czern, czern);        
        elemList[2] = new CubeElement(2,  new Vector3f(+0.51f,+0.51f,+0.51f),   czerwien, biel, czern, blekit, czern, czern);
        elemList[3] = new CubeElement(3,  new Vector3f(-0.51f,.0f,+0.51f),      czerwien, czern, czern, czern, zielen, czern);
        elemList[4] = new CubeElement(4,  new Vector3f(0.0f,0.0f,+0.51f),       czerwien, czern, czern, czern, czern, czern);
        elemList[5] = new CubeElement(5,  new Vector3f(+0.51f,.0f,+0.51f),      czerwien, czern, czern, blekit, czern, czern);
        elemList[6] = new CubeElement(6,  new Vector3f(-0.51f,-0.51f,+0.51f),   czerwien, czern, zolc, czern, zielen, czern);
        elemList[7] = new CubeElement(7,  new Vector3f(-0.00f,-0.51f,+0.51f),   czerwien, czern, zolc, czern, czern, czern);
        elemList[8] = new CubeElement(8,  new Vector3f(+0.51f,-0.51f,+0.51f),   czerwien, czern, zolc, blekit, czern, czern);
        //WARSTWA 2                                             FRONT | TOP | BOTTOM | RIGHT | LEFT | BACK
        elemList[9] = new CubeElement(9,  new Vector3f(-0.51f,+0.51f,.0f),      czern, biel, czern, czern,   zielen, czern);
        elemList[10] = new CubeElement(10, new Vector3f(-0.0f,+0.51f,.0f),       czern, biel, czern, czern,   czern, czern);
        elemList[11] = new CubeElement(11, new Vector3f(+0.51f,+0.51f,.0f),      czern, biel, czern, blekit,  czern, czern);
        elemList[12] = new CubeElement(12, new Vector3f(-0.51f,.0f,.0f),         czern, czern, czern, czern,  zielen, czern);
        elemList[13] = new CubeElement(13, new Vector3f(+0.51f,.0f,.0f),         czern, czern, czern, blekit, czern, czern);
        elemList[14] = new CubeElement(14, new Vector3f(-0.51f,-0.51f,.0f),      czern, czern, zolc, czern,   zielen, czern);
        elemList[15] = new CubeElement(15, new Vector3f(-0.00f,-0.51f,.0f),      czern, czern, zolc, czern,   czern, czern);
        elemList[16] = new CubeElement(16, new Vector3f(+0.51f,-0.51f,.0f),      czern, czern, zolc, blekit,  czern, czern);
        //WARSTWA 3                                             FRONT | TOP | BOTTOM | RIGHT | LEFT | BACK
        elemList[17] = new CubeElement(17, new Vector3f(-0.51f,+0.51f,-0.51f),   czern, biel, czern,  czern,  zielen, pomarancz);        
        elemList[18] = new CubeElement(18, new Vector3f(0.0f,+0.51f,-0.51f),     czern, biel, czern,  czern,  czern, pomarancz);        
        elemList[19] = new CubeElement(19, new Vector3f(+0.51f,+0.51f,-0.51f),   czern, biel, czern,  blekit, czern, pomarancz);
        elemList[20] = new CubeElement(20, new Vector3f(-0.51f,0.0f,-0.51f),     czern, czern, czern, czern,  zielen, pomarancz);
        elemList[21] = new CubeElement(21, new Vector3f(0.0f,0.0f,-0.51f),       czern, czern, czern, czern,  czern, pomarancz);
        elemList[22] = new CubeElement(22, new Vector3f(+0.51f,0.0f,-0.51f),     czern, czern, czern, blekit, czern, pomarancz);
        elemList[23] = new CubeElement(23, new Vector3f(-0.51f,-0.51f,-0.51f),   czern, czern, zolc,  czern,  zielen, pomarancz);
        elemList[24] = new CubeElement(24, new Vector3f(0.0f,-0.51f,-0.51f),     czern, czern, zolc,  czern,  czern, pomarancz);
        elemList[25] = new CubeElement(25, new Vector3f(+0.51f,-0.51f,-0.51f),   czern, czern, zolc,  blekit, czern, pomarancz);
        
        for (int i=0; i < 26; i++){
            parent.addChild(elemList[i].tg);
            currentLayout[i] = i;
        }
    }
    
    
    public CubeCube(){
        tg = new TransformGroup();
        elementyKostki(tg);
    }
}
