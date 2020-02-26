/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roobik;

import com.sun.j3d.utils.geometry.Box;
import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 *
 * @author Gruby
 */
public class CubeElement{
        public TransformGroup tg = new TransformGroup();
        public Transform3D position = new Transform3D();
        
        public void reset(){
            Transform3D obrot = new Transform3D();
            obrot.rotY(0);
            position.rotY(0);
            tg.setTransform(position);  
        }
        
        public void mulTransform(Transform3D rotation){
            position.mul(rotation, position);
            tg.setTransform(position);   
        }
                     
        
        public CubeElement(int i, Vector3f wektorPrzesuniecie, Appearance front, Appearance top, Appearance bottom,  Appearance right,  Appearance left, Appearance back){
            Box kloc = new Box(.25f, .25f, .25f, front);
            kloc.getShape(Box.TOP).setAppearance(top);
            kloc.getShape(Box.BOTTOM).setAppearance(bottom);
            kloc.getShape(Box.RIGHT).setAppearance(right);
            kloc.getShape(Box.LEFT).setAppearance(left); 
            kloc.getShape(Box.BACK).setAppearance(back);

            TransformGroup transformElem = new TransformGroup();
            transformElem.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            transformElem.addChild(kloc);
            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            tg.addChild(transformElem);

            Transform3D polozenieElem = new Transform3D();
            polozenieElem.set(wektorPrzesuniecie); 
            transformElem.setTransform(polozenieElem);
        }  
    }