package roobik;

import com.sun.j3d.utils.geometry.Box;
import javax.media.j3d.Appearance;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/** Single element of the cube*/
public class CubeElement{
        public TransformGroup tg = new TransformGroup();
        public Transform3D position = new Transform3D();
        
        public void reset(){
            Transform3D rotation = new Transform3D();
            rotation.rotY(0);
            position.rotY(0);
            tg.setTransform(position);  
        }
        
        public void mulTransform(Transform3D rotation){
            position.mul(rotation, position);
            tg.setTransform(position);   
        }
                     
        
        public CubeElement(int i, Vector3f shiftVector, Appearance front, 
                           Appearance top, Appearance bottom, Appearance right,
                           Appearance left, Appearance back){
            Box box = new Box(.25f, .25f, .25f, front);
            box.getShape(Box.TOP).setAppearance(top);
            box.getShape(Box.BOTTOM).setAppearance(bottom);
            box.getShape(Box.RIGHT).setAppearance(right);
            box.getShape(Box.LEFT).setAppearance(left); 
            box.getShape(Box.BACK).setAppearance(back);

            TransformGroup transformElem = new TransformGroup();
            transformElem.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            transformElem.addChild(box);
            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            tg.addChild(transformElem);

            Transform3D polozenieElem = new Transform3D();
            polozenieElem.set(shiftVector); 
            transformElem.setTransform(polozenieElem);
        }  
    }