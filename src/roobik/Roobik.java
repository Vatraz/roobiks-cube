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

/**
 * @author PotezneSzwagry
 */
public final class Roobik extends MouseAdapter implements KeyListener{
    
    /** Gdy true, obroty odbywaja sie bez animacji */
    private boolean noAnimation;
    /** Przyjmuje wartosc true, jeżeli trwa układanie. 
     *  Zmiana wartosci na false rozpoczyna oddtwarzanie zapisanych ruchów */
    private boolean saveActions;
    /** Jezeli true trwa animowane odtwarzanie zapamietanych ruchow */
    private boolean playReplayOn;
    /** Wybrana sciana przy ktorej rozpoczelo sie odtwarzanie trasy. 
     *  Po jej odtworzeniu jest znowu wybierana */
    /** Lista kolejno wybranych przez uzytkownika ruchow */ 
    private List<String> savedMovesList = new ArrayList<>();
    /** Lista kolejno wybranych przez uzytkownika scian */ 
    private List<Integer> savedFacesList = new ArrayList<>();
    /** Lista kolejno wybranych w procesie losowania ruchow */ 
    private List<String> randomMovesList = new ArrayList<>();
    /** Lista kolejno wybranych w procesie losowania scian */ 
    private List<Integer> randomFacesList = new ArrayList<>(); 
    /** Wybrana sciana, dla ktorej interpretowane sa polecenia */
    private int activeFace;
        
    /** Licznik klatek animacji. Jezeli przyjmuje wartosc 0, timerAnimacja
     *  rozpoczyna swoje dzilanie, az do zwiekszenia wartosci parametru do 20. */
    private int animationCounter;

    /** Blokada przyjmowania polecen podczas animacji */
    private boolean blockInput;            //BLOKADA PODCZAS ANIMACJI
    /** Tablica elementow ktore podlegaja obrotowi */
    private int[] elementsToRotateList = new int[8];
    /** Srodkowy element sciany kostki */
    private int middleElement;
    /** Kat o jaki maja obracac sie elementy */
    private final Transform3D rotation;
    
    /** Tablica poszczegolnych warstw kostki - kolejno 0-8, 9-16, 17-25 */ 
    private int[] currentCubeLayout = new int[26];
    
    private final Cube cube3D;
    Map<String, Layer> ratatableLayers;
    
    
    //CANVAS
    private final PickCanvas pickCanvas;  
    
    private class Layer{
        public int middleElement;
        public int[] elementsToRotate;
        
        public Layer(int middleElement, int[] elementsToRotate){
            this.middleElement = middleElement;
            this.elementsToRotate = elementsToRotate;
        }
    }
    
    /** 
     * Odtwarzanie zapisanych w listach ruchow, symulacja wcisniecia przycisku
     * @param index numer elementu listy
     * @param facesList lista zawierajaca kolejne wybrane sciany
     * @param movesList lista zawierajaca kolejne wybrane ruchy
    */
    private void followActionsList(int index, List<Integer> facesList, List<String> movesList){      
        if(null != facesList.get(index))
            activeFace = facesList.get(index); 
            
        if(null != movesList.get(index)) 
            executeCommand(movesList.get(index));
    }
    
    /** 
     * Ulozenie wylosowanego ukladu, poporzez symulowanie wciskania przyciskow,
     * przy wylaczonej animacji.
     */
    private void followRandomActions(int activeFaceBuffor){
        noAnimation = true;
        saveActions = false;
        for(int i =0; i<randomMovesList.size(); i++)
            followActionsList(i, randomFacesList, randomMovesList);
        saveActions = true;
        noAnimation = false;    
        activeFace = activeFaceBuffor;
    }
    
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
    
    
    private void addMoveToSavedList(String move){
        savedMovesList.add(move);
        savedFacesList.add(activeFace);
    }
    
    /** 
     * Realizacja odtwarzania zapamietanych ruchow, poprzez symulowanie wciskania przyciskow 
     * w stałych odstępach czasu. Dziala pod warunkiem pozytywnej wartosci {@link klikaczAnimowanyOn#playReplay}
     */
    TimerTask timerReplay = new TimerTask() {
        int replayCounter = 0;
        int activeFaceBuffor;
        @Override
        public void run() {
            if(playReplayOn){
                if(replayCounter==0){
                    saveActions = false;
                    activeFaceBuffor = activeFace;
                }
                if(replayCounter < savedMovesList.size()){
                    followActionsList(replayCounter, savedFacesList, savedMovesList);
                    replayCounter++;
                } else
                if(replayCounter == savedMovesList.size()){  
                    blockInput = false;
                    saveActions = true;
                    playReplayOn = false;
                    replayCounter = 0;
                    activeFace = activeFaceBuffor;
                }
            }
        }
    };
        
    /** 
     * Wykonywanie pelnego obrotu elementow kostki znajdujacych sie w 
     * tablicy {@link #elementsToRotateList}, bez animacji 
     */
    private void rotateWithoutAnimation(){
        for ( int i = 0; i < 8; i++){                                                           
            int elementToRotate = currentCubeLayout[elementsToRotateList[i]];
            cube3D.rotateElement(elementToRotate, rotation);
        }
        if(middleElement != -1){
            int elementToRotate = currentCubeLayout[middleElement];
            cube3D.rotateElement(elementToRotate, rotation);
        } 
        blockInput = false;
    }
    
    private void rotateWithAnimation(){
        animationCounter = 0;
    }
    
    /** 
     * Animowane obracanie wybranych elementow kostki znajdujacych sie w tablicy,
     * {@link #elementsToRotateList}. Dziala gdy {@link #animationCounter} = 0, 
     * nastepnie, po wykonaniu animacji zmienia jego wartosc na 20
     */
    TimerTask timerAnimation = new TimerTask() {
        @Override
        public void run() {            
            if(animationCounter < 20){
                for ( int i = 0; i < 8; i++){                                                           
                    int numerElem = currentCubeLayout[elementsToRotateList[i]];
                    cube3D.rotateElement(numerElem, rotation);
                }
                if(middleElement != -1){
                    int numerElem = currentCubeLayout[middleElement];
                    cube3D.rotateElement(numerElem, rotation);
                }
            animationCounter++;
            if(animationCounter == 20) blockInput = false;
            }                
        } 
    };
           
    /** 
     * Aktualizacja ukladu kostki na ten po wykonaniu zadanego ruchu, i nastepnie wykonanie obrotu.
     * W zaleznosci od wartosci {@link #noAnimation} realizuje go z animacja lub bez.
     */
    private void executeElementsRotation(){
        //ZMIANA ULOZENIA KOSTKI
        blockInput = true;
        int elementNumber;
        int elementNumberPlus;
        int prevElementNumber = -1;
        int prevElementNumberPlus = -1;
        
        int firstElementNumber = elementsToRotateList[0];
        int firstElementNumberPlus = elementsToRotateList[1];
        
        //ZMIANA UKLADU KOSTKI W TABLICY
        for ( int i = 0; i < 7; i=i+2){                            
                elementNumber = currentCubeLayout[elementsToRotateList[i]];
                elementNumberPlus = currentCubeLayout[elementsToRotateList[i+1]];      
                currentCubeLayout[elementsToRotateList[i]] = prevElementNumber;
                prevElementNumber = elementNumber;
                currentCubeLayout[elementsToRotateList[i+1]] = prevElementNumberPlus;
                prevElementNumberPlus = elementNumberPlus;
        }
        currentCubeLayout[firstElementNumber] = prevElementNumber;
        currentCubeLayout[firstElementNumberPlus] = prevElementNumberPlus;
        
        //ANIMACJA W PRZYPADKU NORMALNEGO UKLADANIA/ODTWARZANIA W PRZECIWNYM RAZIE OPCJA SZYBKA
        if(!noAnimation)
            rotateWithAnimation();
        else
            rotateWithoutAnimation();
    }
    
    /** 
     * Wybor kierunku obrotu - poziomo - oraz ustawienie skoku animacji, {@link #rotation},
     * w zaleznosci od wartosci {@link #noAnimation}.
     */
    private void rotationPitch(boolean positive){
        if(positive == true){
            if(!noAnimation)rotation.rotY(Math.PI/40);
            else rotation.rotY(Math.PI/2);
        }else{
            if(!noAnimation)rotation.rotY(-Math.PI/40); 
            else rotation.rotY(-Math.PI/2);
        }        
        executeElementsRotation();
    }
    
    /** 
     * Wybor kierunku obrotu - pionowo - oraz ustawienie skoku animacji, {@link #rotation},
     * w zaleznosci od wartosci {@link #noAnimation}.
     */
    private void rotationRoll (boolean positive){
        if(positive == true){
            if(!noAnimation)rotation.rotX(Math.PI/40);
            else rotation.rotX(Math.PI/2);
        }else{
            if(!noAnimation)rotation.rotX(-Math.PI/40); 
            else rotation.rotX(-Math.PI/2);
        }               
        executeElementsRotation();
    }
    
    /** 
     * Wybor kierunku obrotu - w poprzek - oraz ustawienie skoku animacji, {@link #rotation},
     * w zaleznosci od wartosci {@link #noAnimation}.
     */
    private void rotationYaw( boolean positive){
        if(positive == true){
            if(!noAnimation)rotation.rotZ(-Math.PI/40);
            else rotation.rotZ(-Math.PI/2);
        }else{
            if(!noAnimation)rotation.rotZ(+Math.PI/40);
            else rotation.rotZ(Math.PI/2);
        }     
        executeElementsRotation();
    }
    
    void cubeReset(){
        cube3D.elementsReset();
        for (int i=0; i<26; i++)
            currentCubeLayout[i]=i;
    }
    
    /** 
     * Konstruktor klasy Roobik 
     */
    public Roobik(){
        System.setProperty("sun.awt.noerasebackground", "true");
        activeFace = 1;
        blockInput = false;
        animationCounter = 20;
        rotation = new Transform3D();
        noAnimation = false;
        saveActions = true;
        playReplayOn = false;
        //CANVAS
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        SimpleUniverse universe = new SimpleUniverse(canvas3D);
        Frame frame = new Frame("Kostka ogurat tego typu bec");       
        frame.setResizable(false);
        canvas3D.setSize(800, 800);
        canvas3D.addKeyListener(this);
        canvas3D.addMouseListener(this);
        
        cube3D = new Cube();

        //SCENE GRAPH
        BranchGroup scene = cube3D.sceneGraph;
        universe.addBranchGraph(scene);
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
        
        ratatableLayers = createRotatablelayers();
        
        for(int i=0; i<26; i++)
            currentCubeLayout[i] = i;
    }
    
    public static void main(String[] args) {
        Roobik roobik = new Roobik();
    }
    
    /** 
     * Creates arrays of randomly chosen faces and moves.
     * Updates {@link Roobik#randomMovesList } and {@link Roobik#randomFacesList } 
     */
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
    
    
    private HashMap createRotatablelayers(){
        HashMap map = new HashMap<String, Layer>();
        map.put("ZXtop", new Layer(10, new int[] {0, 1, 2, 11, 19, 18, 17, 9}));
        map.put("ZXmid", new Layer(-1, new int[] {3, 4, 5, 13, 22, 21, 20, 12}));
        map.put("ZXbot", new Layer(15, new int[] {6, 7, 8, 16, 25, 24, 23, 14}));
        map.put("ZYtop", new Layer(12, new int[] {0, 3, 6, 14, 23, 20, 17, 9}));
        map.put("ZYmid", new Layer(-1, new int[] {1, 4, 7, 15, 24, 21, 18, 10}));
        map.put("ZYbot", new Layer(13, new int[] {2, 5, 8, 16, 25, 22, 19, 11}));
        map.put("XYtop", new Layer(4, new int[] {0, 3, 6, 7, 8, 5, 2, 1}));
        map.put("XYmid", new Layer(-1, new int[] {9, 12, 14, 15, 16, 13, 11, 10}));
        map.put("XYbot", new Layer(21, new int[] {17, 20, 23, 24, 25, 22, 19, 18}));        
        return map;
    }
    
    /** 
     * Tworzenie tablicy obracanych elementow 
     */
    private void createElementsToRotateList(
                int zero, int uno, int due, int tre,
                int quattro, int cinque, int sei, int sette){
            elementsToRotateList[0]=zero;          elementsToRotateList[1]=uno;
            elementsToRotateList[2]=due;           elementsToRotateList[3]=tre; 
            elementsToRotateList[4]=quattro;       elementsToRotateList[5]=cinque;
            elementsToRotateList[6]=sei;           elementsToRotateList[7]=sette;
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
    
    /** 
     * Wykonanie obslugiwanej przez program komendy
     * @param command id komendy 
     */
    void executeCommand(String command){
        switch (command) {
            case "undo":  //resetowanie ukladu              
                undoAcition();
                break;  
            case "reset":  //resetowanie ukladu              
                savedMovesList.clear();
                savedFacesList.clear();
                randomMovesList.clear();
                randomFacesList.clear();
                cubeReset(); 
                break;   
            case "random":  //tasowanie kostki                          
                savedMovesList.clear();
                savedFacesList.clear();
                randomMovesList.clear();
                randomFacesList.clear();
                cubeReset();
                createRandomActionsList();
                followRandomActions(activeFace);
                break;   
            case "play":  //odtwarzanie              
                cubeReset();
                followRandomActions(activeFace);
                playReplayOn = true;
                break; 
            case "right0":
                if (saveActions) addMoveToSavedList("right0");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = 10;
                    createElementsToRotateList(0, 1, 2, 11, 19, 18, 17, 9);
                    rotationPitch(true);
                }else
                    if(activeFace == 4){
                        middleElement = 21;
                        createElementsToRotateList(17, 18, 19, 22, 25, 24, 23, 20);
                        rotationYaw(true);
                    }else
                        if(activeFace == 5){
                            middleElement = 4;
                            createElementsToRotateList(0, 3, 6, 7, 8, 5, 2, 1);
                            rotationYaw(false);
                        }   break;
            case "right1":
                if (saveActions) addMoveToSavedList("right1");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = -1;
                    createElementsToRotateList(3, 4, 5, 13, 22, 21, 20, 12);
                    rotationPitch(true);
                }else
                    if(activeFace == 4){
                        middleElement = -1;
                        createElementsToRotateList(9, 10, 11, 13, 16, 15, 14, 12);
                        rotationYaw(true);
                    }else
                        if(activeFace == 5){
                            middleElement = -1;
                            createElementsToRotateList(9, 12, 14, 15, 16, 13, 11, 10);
                            rotationYaw(false);
                        }   break;
            case "right2":
                if (saveActions) addMoveToSavedList("right2");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = 15;
                    createElementsToRotateList(6, 7, 8, 16, 25, 24, 23, 14);
                    rotationPitch(true);
                }else
                    if(activeFace == 4){
                        middleElement = 4;
                        createElementsToRotateList(0, 1, 2, 5, 8, 7, 6, 3);
                        rotationYaw(true);
                    }else
                        if(activeFace == 5){
                            middleElement = 21;
                            createElementsToRotateList(17, 20, 23, 24, 25, 22, 19, 18);
                            rotationYaw(false);
                        }   break;
            case "left0":
                if (saveActions) addMoveToSavedList("left0");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = 10;
                    createElementsToRotateList(9, 17, 18, 19, 11, 2, 1, 0);
                    rotationPitch(false);
                }else
                    if(activeFace == 4){
                        middleElement = 21;
                        createElementsToRotateList(17, 20, 23, 24, 25, 22, 19, 18);
                        rotationYaw(false);
                    }else
                        if(activeFace == 5){
                            middleElement = 4;
                            createElementsToRotateList(0, 1, 2, 5, 8, 7, 6, 3);
                            rotationYaw(true);
                        }   break;
            case "left1":
                if (saveActions) addMoveToSavedList("left1");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = -1;
                    createElementsToRotateList(12, 20, 21, 22, 13, 5, 4, 3);
                    rotationPitch(false);
                }else
                    if(activeFace == 4){
                        middleElement = -1;
                        createElementsToRotateList(9, 12, 14, 15, 16, 13, 11, 10);
                        rotationYaw(false);
                    }else
                        if(activeFace == 5){
                            middleElement = -1;
                            createElementsToRotateList(9, 10, 11, 13, 16, 15, 14, 12);
                            rotationYaw(true);
                        }   break;
            case "left2":                
                if (saveActions) addMoveToSavedList("left2");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = 15;
                    createElementsToRotateList(14, 23, 24, 25, 16, 8, 7, 6);
                    rotationPitch(false);
                }else
                    if(activeFace == 4){
                        middleElement = 4;
                        createElementsToRotateList(0, 3, 6, 7, 8, 5, 2, 1);
                        rotationYaw(false);
                    }else
                        if(activeFace == 5){
                            middleElement = 21;
                            createElementsToRotateList(17, 18, 19, 22, 25, 24, 23, 20);
                            rotationYaw(true);
                        }   break;
            case "down0":
                if (saveActions) addMoveToSavedList("down0");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = 12;
                    createElementsToRotateList(0, 3, 6, 14, 23, 20, 17, 9);
                    rotationRoll(true);
                }else
                    if(activeFace == 3){
                        middleElement = 13;
                        createElementsToRotateList(2, 11, 19, 22, 25, 16, 8 , 5);
                        rotationRoll(false);
                    }else
                        if(activeFace == 2){
                            middleElement = 4;
                            createElementsToRotateList(0, 1, 2, 5, 8, 7, 6, 3);
                            rotationYaw(true);
                        }else
                            if(activeFace == 0){
                                middleElement = 21;
                                createElementsToRotateList(17, 20, 23, 24, 25, 22, 19, 18);
                                rotationYaw(false);
                            }   break;
            case "down1":
                if (saveActions) addMoveToSavedList("down1");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = -1;
                    createElementsToRotateList(1, 4, 7, 15, 24, 21, 18, 10);
                    rotationRoll(true);
                }else
                    if(activeFace == 3){
                        middleElement = -1;
                        createElementsToRotateList(1, 10, 18, 21, 24, 15, 7, 4);
                        rotationRoll(false);
                    }else
                        if(activeFace == 2){
                            middleElement = -1;
                            createElementsToRotateList(9, 10, 11, 13, 16, 15, 14, 12);
                            rotationYaw(true);
                        }else
                            if(activeFace == 0){
                                middleElement = -1;
                                createElementsToRotateList(9, 12, 14, 15, 16, 13, 11, 10);
                                rotationYaw(false);
                            }   break;
            case "down2":
                if (saveActions) addMoveToSavedList("down2");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = 13;
                    createElementsToRotateList(2, 5, 8, 16, 25, 22, 19, 11);
                    rotationRoll(true);
                }else
                    if(activeFace == 3){
                        middleElement = 12;
                        createElementsToRotateList(0, 9, 17, 20, 23, 14, 6, 3);
                        rotationRoll(false);
                    }else
                        if(activeFace == 2){
                            middleElement = 21;
                            createElementsToRotateList(17, 18, 19, 22, 25, 24, 23, 20);
                            rotationYaw(true);
                        }else
                            if(activeFace == 0){
                                middleElement = 4;
                                createElementsToRotateList(0, 3, 6, 7, 8, 5, 2, 1);
                                rotationYaw(false);
                            }   break;
            case "up0":
                if (saveActions) addMoveToSavedList("up0");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = 12;
                    createElementsToRotateList(9, 17, 20, 23, 14, 6, 3, 0);
                    rotationRoll(false);                }else
                    if(activeFace == 3){
                        middleElement = 13;
                        createElementsToRotateList(2, 5, 8, 16, 25, 22, 19, 11);
                        rotationRoll(true);
                   }else
                        if(activeFace == 2){
                            middleElement = 4;
                            createElementsToRotateList(0, 3, 6, 7, 8, 5, 2, 1);
                            rotationYaw(false);
                        }else
                            if(activeFace == 0){
                                middleElement = 21;
                                createElementsToRotateList(17, 18, 19, 22, 25, 24, 23, 20);
                                rotationYaw(true);
                            }   break;
            case "up1":
                if (saveActions) addMoveToSavedList("up1");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = -1;
                    createElementsToRotateList(1, 10, 18, 21, 24, 15, 7, 4);
                    rotationRoll(false);
               }else
                    if(activeFace == 3){
                        middleElement = -1;
                        createElementsToRotateList(1, 4, 7, 15, 24 ,21, 18, 10);
                        rotationRoll(true);
                   }else
                        if(activeFace == 2){
                            middleElement = -1;
                            createElementsToRotateList(9, 12, 14, 15, 16, 13, 11, 10);
                            rotationYaw(false);
                        }else
                            if(activeFace == 0){
                                middleElement = -1;
                                createElementsToRotateList(9, 10, 11, 13, 16, 15, 14, 12);
                                rotationYaw(true);
                            }   break;
            case "up2":
                if (saveActions) addMoveToSavedList("up2");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = 13;
                    createElementsToRotateList(2, 11, 19, 22, 25, 16, 8, 5);
                    rotationRoll(false);
               }else
                    if(activeFace == 3){
                        middleElement = 12;
                        createElementsToRotateList(0, 3, 6, 14, 23, 20, 17, 9 );
                        rotationRoll(true);
                   }else
                        if(activeFace == 2){
                            middleElement = 21;
                            createElementsToRotateList(17, 20, 23, 24, 25, 22, 19, 18);
                            rotationYaw(false);
                        }else
                            if(activeFace == 0){
                                middleElement = 4;
                                createElementsToRotateList(0, 1, 2, 5, 8, 7, 6, 3);
                                rotationYaw(true);
                            }   break;
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
