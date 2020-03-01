package roobik;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.media.j3d.*;
import javax.vecmath.Vector3f;


public final class Roobik extends MouseAdapter implements KeyListener{
    
    /** Enables saving subsequent actions - moves and active faces */
    private boolean saveActions;
    /** Enables playback of saved moves */
    private boolean savedActionsReplayOn;
    /** List of saved moves*/ 
    private List<String> savedMovesList = new ArrayList<>();
    /** List of {@link Roobik#activeFace} values 
     * corresponding to values of {@link Roobik#savedMovesList}*/ 
    private List<Integer> savedFacesList = new ArrayList<>();
    /** List of randomly chosen moves*/ 
    private final List<String> randomMovesList = new ArrayList<>();
    /** List of {@link Roobik#activeFace} values 
     * corresponding to values of {@link Roobik#randomMovesList}*/ 
    private final List<Integer> randomFacesList = new ArrayList<>(); 
    
    /** Currently selected face*/
    private int activeFace;
    
    /** Enables animation of rotation */
    private boolean noAnimation;    
    /** Animation counter, turns on animation if its value is 0 and 
     * {@link Roobik.noAnimation} is true */
    private int animationCounter;
    /** Blocks user input */
    private boolean blockInput; 

    /** Transform3D holding information on elements rotation */
    private final Transform3D rotation;
        
    /** Array of all elements in the cube. The indexes 0-8 correspond 
     *  to layer one, 9-16 layer two and 17-25 layer three. The value 
     *  corresponding to a given index tells what element is currently 
     *  in this place. */ 
    private int[] currentCubeLayout = new int[26];
    
    /** 3D model of the cube */
    private final Cube cube3D;
    
    /** Map of the cube layers that can be rotated. */
    Map<String, Layer> rotatableLayers;
    
    /** Refers to currently chosen layer. */
    private Layer activeLayer;
    
    private final PickCanvas pickCanvas;  
    
    /** Contains the middle element of layer (-1 if none) and all 
     *  its elements. */
    private class Layer{
        public int middleElement;
        public int[] elementsToRotate;
        
        public Layer(int middleElement, int[] elementsToRotate){
            this.middleElement = middleElement;
            this.elementsToRotate = elementsToRotate;
        }
    }
    
    /** Follows commands stored in lists. 
     *  @param index index of actions in list.
     *  @param facesList list of faces 
     *  @param movesList list of moves */
    private void commandFromLists(int index, List<Integer> facesList, List<String> movesList){      
        if(null != facesList.get(index))
            activeFace = facesList.get(index); 
        if(null != movesList.get(index)) 
            executeCommand(movesList.get(index));
    }
    
    /** Arrange the cube without animation based on the actions stored in 
     *  {@link Roobik#randomFacesList} and {@link Roobik#randomMovesList} */
    private void followRandomActions(){
        int activeFaceBuffor = activeFace;
        noAnimation = true;
        saveActions = false;
        for(int i =0; i<randomMovesList.size(); i++)
            commandFromLists(i, randomFacesList, randomMovesList);
        saveActions = true;
        noAnimation = false;    
        activeFace = activeFaceBuffor;
    }
    
    /** Undoes last actions stored in {@link Roobik#randomFacesList} and 
     *  {@link Roobik#randomMovesList} and deletes them */
    private void undoAcition(){
        if (savedMovesList.isEmpty()) return;
        String move = savedMovesList.remove(savedMovesList.size() - 1);
        int face = savedFacesList.remove(savedFacesList.size()-1);
        int activeFaceBuffor = activeFace;
        activeFace = face;
        
        Pattern pattern = Pattern.compile("(\\w+)(\\d)");
        Matcher matcher = pattern.matcher(move);
        matcher.matches();
        String invertedMove;
        switch (matcher.group(1)){
            case "left": invertedMove = "right"; break;
            case "right": invertedMove = "left"; break;
            case "up": invertedMove = "down"; break;
            default: invertedMove = "up"; break;
        }
        saveActions = false;
        executeCommand(invertedMove + matcher.group(2));
        saveActions = true;
        activeFace = activeFaceBuffor;
    }
    
    /** Modifies {@link Roobik#randomMovesList} by adding the passed move ID. 
     *  Adds the current value of {@link Roobik#activeFace} to 
     *  {@link Roobik#randomFacesList}. */
    private void addMoveToSavedList(String move){
        savedMovesList.add(move);
        savedFacesList.add(activeFace);
    }
    
    /** Plays saved actions if {@link Roobik#savedActionsReplayOn} is true */
    TimerTask timerReplay = new TimerTask() {
        int replayCounter = 0;
        int activeFaceBuffor;
        @Override
        public void run() {
            if(savedActionsReplayOn){
                if(replayCounter==0){
                    saveActions = false;
                    activeFaceBuffor = activeFace;
                }
                if(replayCounter < savedMovesList.size()){
                    commandFromLists(replayCounter, savedFacesList, savedMovesList);
                    replayCounter++;
                } else
                if(replayCounter == savedMovesList.size()){  
                    blockInput = false;
                    saveActions = true;
                    savedActionsReplayOn = false;
                    replayCounter = 0;
                    activeFace = activeFaceBuffor;
                }
            }
        }
    };
        
    /** Rotates 3D cube elements without animation. */
    private void rotateWithoutAnimation(){
        for ( int i = 0; i < 8; i++){                                                           
            int elementToRotate = currentCubeLayout[activeLayer.elementsToRotate[i]];
            cube3D.rotateElement(elementToRotate, rotation);
        }
        if(activeLayer.middleElement != -1){
            int elementToRotate = currentCubeLayout[activeLayer.middleElement];
            cube3D.rotateElement(elementToRotate, rotation);
        } 
        blockInput = false;
    }
    
    /** Starts the animation handled by {@link Roobik#timerAnimation}. */
    private void rotateWithAnimation(){
        animationCounter = 0;
    }
    
    /** If {@link Roobik#animationCounter} is 0, animate rotation of elements 
     *  stored in {@link Roobik#activeLayer}. */
    TimerTask timerAnimation = new TimerTask() {
        @Override
        public void run() {            
            if(animationCounter < 20){
                for ( int i = 0; i < 8; i++){                                                           
                    int numerElem = currentCubeLayout[activeLayer.elementsToRotate[i]];
                    cube3D.rotateElement(numerElem, rotation);
                }
                if(activeLayer.middleElement != -1){
                    int numerElem = currentCubeLayout[activeLayer.middleElement];
                    cube3D.rotateElement(numerElem, rotation);
                }
            animationCounter++;
            if(animationCounter == 20) blockInput = false;
            }                
        } 
    };
           
    /** Updates the current cube layout by changing values stored in 
     *  {@link Roobik#currentCubeLayout} 
     *  @param inverted if true, the elements will be rotated in opposite 
     *  direction */
    private void executeElementsRotation(boolean inverted){
        blockInput = true;
        int elementNumber;
        int elementNumberPlus;
        int prevElementNumber = -1;
        int prevElementNumberPlus = -1;
        
        int[] elementsToRotateArr = activeLayer.elementsToRotate.clone();
        // If rotation is inverted
        if (inverted){
            int n = elementsToRotateArr.length;
            for (int i = 0; i < n / 2; i++) { 
                int buf = elementsToRotateArr[i]; 
                elementsToRotateArr[i] = elementsToRotateArr[n - i - 1]; 
                elementsToRotateArr[n - i - 1] = buf; 
            }
        }
        
        int firstElementNumber = elementsToRotateArr[0];
        int firstElementNumberPlus = elementsToRotateArr[1];
        
        // Change the layout of elements in the cube 
        for ( int i = 0; i < 7; i=i+2){                            
                elementNumber = currentCubeLayout[elementsToRotateArr[i]];
                elementNumberPlus = currentCubeLayout[elementsToRotateArr[i+1]];      
                currentCubeLayout[elementsToRotateArr[i]] = prevElementNumber;
                prevElementNumber = elementNumber;
                currentCubeLayout[elementsToRotateArr[i+1]] = prevElementNumberPlus;
                prevElementNumberPlus = elementNumberPlus;
        }
        currentCubeLayout[firstElementNumber] = prevElementNumber;
        currentCubeLayout[firstElementNumberPlus] = prevElementNumberPlus;
    }
    
    /** 
     * Calculate {@link #rotation} along the Y-axis, depending on the  
     * value of {@link noAnimation} and {@link inverted}. 
     * @param inverted inversion of the default rotation direction.
     */
    private void rotationPitch(boolean inverted){
        if(inverted == false){
            if(!noAnimation)rotation.rotY(Math.PI/40);
            else rotation.rotY(Math.PI/2);
        }else{
            if(!noAnimation)rotation.rotY(-Math.PI/40); 
            else rotation.rotY(-Math.PI/2);
        }        
        executeElementsRotation(inverted);
        if(!noAnimation) rotateWithAnimation();
        else rotateWithoutAnimation();
    }
    
    /** 
     * Calculate {@link #rotation} along the X-axis, depending on the  
     * value of {@link noAnimation} and {@link inverted}. 
     * @param inverted inversion of the default rotation direction.
     */
    private void rotationRoll (boolean inverted){
        if(inverted == false){
            if(!noAnimation)rotation.rotX(Math.PI/40);
            else rotation.rotX(Math.PI/2);
        }else{
            if(!noAnimation)rotation.rotX(-Math.PI/40); 
            else rotation.rotX(-Math.PI/2);
        }               
        executeElementsRotation(inverted);
        if(!noAnimation) rotateWithAnimation();
        else rotateWithoutAnimation();
    }
    
    /** 
     * Calculate {@link #rotation} along the Z-axis, depending on the  
     * value of {@link noAnimation} and {@link inverted}. 
     * @param inverted inversion of the default rotation direction.
     */
    private void rotationYaw( boolean inverted){
        if(inverted == false){
            if(!noAnimation)rotation.rotZ(Math.PI/40);
            else rotation.rotZ(Math.PI/2);
        }else{
            if(!noAnimation)rotation.rotZ(-Math.PI/40);
            else rotation.rotZ(-Math.PI/2);
        }     
        executeElementsRotation(inverted);
        if(!noAnimation) rotateWithAnimation();
        else rotateWithoutAnimation();
    }
    
    /** Clears all lists containing saved actions: {@link Roobik#savedMovesList}
     *  {@link Roobik#savedFacesList}, {@link Roobik#randomMovesList} and 
     *  {@link Roobik#randomFacesList} */
    void clearSavedLists(){
        savedMovesList.clear();
        savedFacesList.clear();
        randomMovesList.clear();
        randomFacesList.clear();
    }
    
    /** Resets {@link Roobik#currentCubeLayout} and 3D model of the cube */
    void cubeReset(){
        cube3D.elementsReset();
        for (int i=0; i<26; i++)
            currentCubeLayout[i]=i;
    }

    public Roobik(){
        System.setProperty("sun.awt.noerasebackground", "true");
        activeFace = 1;
        blockInput = false;
        animationCounter = 20;
        rotation = new Transform3D();
        noAnimation = false;
        saveActions = true;
        savedActionsReplayOn = false;
        
        //CANVAS
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        SimpleUniverse universe = new SimpleUniverse(canvas3D);
        Frame frame = new Frame("R00bik");       
        frame.setResizable(false);
        canvas3D.setSize(800, 800);
        canvas3D.addKeyListener(this);
        canvas3D.addMouseListener(this);
        
        cube3D = new Cube();

        //SCENE GRAPH
        BranchGroup scene = cube3D.sceneGraph;
        universe.addBranchGraph(scene);
        
        //OBSERVER
        Transform3D observerTransform = new Transform3D();
        observerTransform.set(new Vector3f(0.0f, 0.0f, 4.7f));
        universe.getViewingPlatform().getViewPlatformTransform().setTransform(observerTransform);
        
        //TIMER
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerAnimation, 0, 15);
        timer.scheduleAtFixedRate(timerReplay, 0, 400);
        
        //MOUSE CLICK DETECTION
        pickCanvas = new PickCanvas(canvas3D, scene);
        pickCanvas.setMode(PickCanvas.GEOMETRY);
        
        frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent winEvent) {System.exit(0);}});
        frame.add(canvas3D);
        frame.pack();
        frame.show();
        
        rotatableLayers = createRotatablelayers();
        
        for(int i=0; i<26; i++)
            currentCubeLayout[i] = i;
    }
    
    public static void main(String[] args) {
        Roobik roobik = new Roobik();
    }
    
    /** Creates arrays of randomly chosen faces and moves.
     *  Updates {@link randomMovesList } and {@link randomFacesList} */
    void createRandomActionsList(){
        Random generator = new Random();
        int[] availableFaces = {0, 1, 2, 3, 4, 5, 6};
        String[] availableMoves = {"up0", "up1", "up2", "right0", "right1", 
            "right2", "down0", "down1", "down2", "left0", "left1", "left2"};
        for(int i = 0; i < 123; i++){
            int face = generator.nextInt(6);
            int move = generator.nextInt(12);
            randomMovesList.add(availableMoves[move]);
            randomFacesList.add(availableFaces[face]);      
        }
    }
    
    /** Returns a map containing all layers that can be rotated */
    private HashMap createRotatablelayers(){
        HashMap map = new HashMap<String, Layer>();
        map.put("ZXtop", new Layer(10, new int[] {0, 1, 2, 11, 19, 18, 17, 9}));
        map.put("ZXmid", new Layer(-1, new int[] {3, 4, 5, 13, 22, 21, 20, 12}));
        map.put("ZXbot", new Layer(15, new int[] {6, 7, 8, 16, 25, 24, 23, 14}));
        
        map.put("YZbot", new Layer(12, new int[] {0, 3, 6, 14, 23, 20, 17, 9}));
        map.put("YZmid", new Layer(-1, new int[] {1, 4, 7, 15, 24, 21, 18, 10}));
        map.put("YZtop", new Layer(13, new int[] {2, 5, 8, 16, 25, 22, 19, 11}));
        
        map.put("XYtop", new Layer(4, new int[] {0, 3, 6, 7, 8, 5, 2, 1}));
        map.put("XYmid", new Layer(-1, new int[] {9, 12, 14, 15, 16, 13, 11, 10}));
        map.put("XYbot", new Layer(21, new int[] {17, 20, 23, 24, 25, 22, 19, 18}));        
        return map;
    }
    
    @Override
    public void mouseClicked(MouseEvent e){
        pickCanvas.setShapeLocation(e);
        PickResult picked = pickCanvas.pickClosest();
        if (picked == null) {
        }else if(!blockInput){
            Object id = picked.getNode(PickResult.SHAPE3D).getUserData();
            executeCommand(id.toString());                                                      
        }
    }
    
    /** Executes the passed command */
    void executeCommand(String command){
        boolean inverseDefault = true;
        switch (command) {
            case "undo":  //resetowanie ukladu              
                undoAcition();
                break;  
            case "reset":  //resetowanie ukladu              
                clearSavedLists();
                cubeReset(); 
                break;   
            case "random":  //tasowanie kostki 
                clearSavedLists();
                cubeReset();
                createRandomActionsList();
                followRandomActions();
                break;   
            case "play":  //odtwarzanie              
                cubeReset();
                followRandomActions();
                savedActionsReplayOn = true;
                break; 
            case "left0":
                inverseDefault = false;
            case "right0":
                if (saveActions) addMoveToSavedList(command);
                if(activeFace >=0 && activeFace <=3){
                    activeLayer = rotatableLayers.get("ZXtop");
                    rotationPitch(!inverseDefault);
                }else if(activeFace == 4){
                    activeLayer = rotatableLayers.get("XYbot");
                    rotationYaw(inverseDefault);
                }else if(activeFace == 5){
                    activeLayer = rotatableLayers.get("XYtop");
                    rotationYaw(!inverseDefault);
                } break;
            case "left1":
                inverseDefault = false;
            case "right1":
                if (saveActions) addMoveToSavedList(command);
                if(activeFace >=0 && activeFace <=3){
                    activeLayer = rotatableLayers.get("ZXmid");
                    rotationPitch(!inverseDefault);
                }else if(activeFace == 4){
                    activeLayer = rotatableLayers.get("XYmid");
                    rotationYaw(inverseDefault); 
                }else if(activeFace == 5){
                    activeLayer = rotatableLayers.get("XYmid");
                    rotationYaw(!inverseDefault);
                } break;
            case "left2":
                inverseDefault = false;
            case "right2":
                if (saveActions) addMoveToSavedList(command);
                if(activeFace >=0 && activeFace <=3){
                    activeLayer = rotatableLayers.get("ZXbot");
                    rotationPitch(!inverseDefault);
                }else if(activeFace == 4){
                    activeLayer = rotatableLayers.get("XYtop");
                    rotationYaw(inverseDefault);
                }else if(activeFace == 5){
                    activeLayer = rotatableLayers.get("XYbot");
                    rotationYaw(!inverseDefault);
                } break;
            case "down0":
                inverseDefault = false;
            case "up0":
                if (saveActions) addMoveToSavedList(command);
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    activeLayer = rotatableLayers.get("YZbot");
                    rotationRoll(inverseDefault);
                }else if(activeFace == 3){
                    activeLayer = rotatableLayers.get("YZtop");
                    rotationRoll(!inverseDefault);
                }else if(activeFace == 2){
                    activeLayer = rotatableLayers.get("XYtop");
                    rotationYaw(!inverseDefault);
                }else if(activeFace == 0){
                    activeLayer = rotatableLayers.get("XYbot");
                    rotationYaw(inverseDefault);
                }break;
            case "down1":
                inverseDefault = false;
            case "up1":
                if (saveActions) addMoveToSavedList(command);
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    activeLayer = rotatableLayers.get("YZmid");
                    rotationRoll(inverseDefault);
                }else if(activeFace == 3){
                    activeLayer = rotatableLayers.get("YZmid");
                    rotationRoll(!inverseDefault);
                }else if(activeFace == 2){
                    activeLayer = rotatableLayers.get("XYmid");
                    rotationYaw(!inverseDefault);
                }else if(activeFace == 0){
                    activeLayer = rotatableLayers.get("XYmid");
                    rotationYaw(inverseDefault);
                }break;
            case "down2":
                inverseDefault = false;
            case "up2":
                if (saveActions) addMoveToSavedList(command);
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    activeLayer = rotatableLayers.get("YZtop");
                    rotationRoll(inverseDefault);
                }else if(activeFace == 3){
                    activeLayer = rotatableLayers.get("YZbot");
                    rotationRoll(!inverseDefault);
                }else if(activeFace == 2){
                    activeLayer = rotatableLayers.get("XYbot");
                    rotationYaw(!inverseDefault);
                }else if(activeFace == 0){
                    activeLayer = rotatableLayers.get("XYtop");
                    rotationYaw(inverseDefault);
                }break;
            case "face0":
                activeFace = 0;
                rotation.rotY(-Math.PI/2);
                cube3D.rotateArrows(rotation);
                break;                  
            case "face1":
                activeFace = 1;
                rotation.rotY(0);
                cube3D.rotateArrows(rotation);
                break;  
            case "face2":
                activeFace = 2;
                rotation.rotY(Math.PI/2);
                cube3D.rotateArrows(rotation);
                break;   
            case "face3":
                activeFace = 3;
                rotation.rotY(Math.PI);
                cube3D.rotateArrows(rotation);
                break;   
            case "face4":
                activeFace = 4;
                rotation.rotX(-Math.PI/2);
                cube3D.rotateArrows(rotation);
                break;   
            case "face5":                
                activeFace = 5;
                rotation.rotX(Math.PI/2);
                cube3D.rotateArrows(rotation);
                break;   
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        if (!blockInput)
            switch (ke.getKeyChar()){
            case 'w': executeCommand("right0"); break;
            case 's': executeCommand("right1"); break;
            case 'x': executeCommand("right2"); break;  
            case 'q': executeCommand("left0"); break;
            case 'a': executeCommand("left1"); break;
            case 'z': executeCommand("left1"); break;      
            case 'j': executeCommand("down0");  break;
            case 'k': executeCommand("down1"); break;
            case 'l': executeCommand("down2"); break;        
            case 'u': executeCommand("up0"); break;
            case 'i': executeCommand("up1"); break;    
            case 'o': executeCommand("up2");  break;
            case 'r': executeCommand("reset"); break;
            case 'p': executeCommand("play"); break;
            case 't': executeCommand("random"); break;
            case '0': executeCommand("face0"); break;
            case '1': executeCommand("face1"); break;
            case '2': executeCommand("face2"); break;
            case '3': executeCommand("face3"); break;
            case '4': executeCommand("face4"); break;
            case '5': executeCommand("face5"); break;
            case 'b': executeCommand("undo"); break;
            default: break;
            } 
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}