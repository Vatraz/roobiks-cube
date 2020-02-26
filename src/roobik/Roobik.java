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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.j3d.*;
import javax.vecmath.Vector3f;



/**
 * @author PotezneSzwagry
 */
public final class Roobik extends MouseAdapter implements KeyListener{
    
    /** Gdy true, obroty odbywaja sie bez animacji */
    static boolean noAnimation;
    /** Przyjmuje wartosc true, jeżeli trwa układanie. 
     *  Zmiana wartosci na false rozpoczyna oddtwarzanie zapisanych ruchów */
    static boolean rememberMoves;
    /** Jezeli true trwa animowane odtwarzanie zapamietanych ruchow */
    static boolean playReplayOn;
    /** Wybrana sciana przy ktorej rozpoczelo sie odtwarzanie trasy. 
     *  Po jej odtworzeniu jest znowu wybierana */
    static int activeFaceBuff; 
    /** Lista kolejno wybranych przez uzytkownika ruchow */ 
    static List<String> pressedKeysList = new ArrayList<>();
    /** Lista kolejno wybranych przez uzytkownika scian */ 
    static List<String> facesList = new ArrayList<>();
    /** Lista kolejno wybranych w procesie losowania ruchow */ 
    static List<String> randomMovesList = new ArrayList<>();
    /** Lista kolejno wybranych w procesie losowania scian */ 
    static List<String> randomFacesList = new ArrayList<>();
    /** Dlugosc listy ruchow */
    static int dlugoscListy;   
    /** Wybrana sciana, dla ktorej interpretowane sa polecenia */
    public static int activeFace;
    
    /** Licznik klatek animacji. Jezeli przyjmuje wartosc 0, timerAnimacja
     *  rozpoczyna swoje dzilanie, az do zwiekszenia wartosci parametru do 20. */
    static int animationCounter;
    /** Licznik wykonanych krokow podczas odtwarzania sekwencji ruchow */
    static int replayCounter;
    /** Blokada przyjmowania polecen podczas animacji */
    static boolean blockInput;            //BLOKADA PODCZAS ANIMACJI
    /** Tablica elementow ktore podlegaja obrotowi */
    static int[] elementsToRotateList = new int[8];
    /** Srodkowy element sciany kostki */
    static int middleElement;
    /** Kat o jaki maja obracac sie elementy */
    static Transform3D rotation;
    
    /** Tablica poszczegolnych warstw kostki - kolejno 0-8, 9-16, 17-25 */ 
    static int[] currentLayout = new int[26];
    
    private Kostka  rubiksCube;
    
    //CANVAS
    static GraphicsConfiguration config;
    static Canvas3D canvas3D;
    private final PickCanvas pickCanvas;
    
    static SimpleUniverse universe;
    private final Timer timer;

    
    //TRANSFORMGROUP POSZCZEGOLNYCH ELEMENTOW
    static TransformGroup observer;  

    

    
    
    /** 
     * Dopisanie do listy numeru aktualnie aktywnej sciany 
     */
    private void dopiszSciane(){
        switch (activeFace) {
            case 0: facesList.add("0"); break;
            case 1: facesList.add("1"); break;
            case 2: facesList.add("2"); break;
            case 3: facesList.add("3"); break;
            case 4: facesList.add("4"); break;
            case 5: facesList.add("5"); break;
            default: break;
        }
    }
    
    /** 
     * Odtwarzanie zapisanych w listach ruchow, symulacja wcisniecia przycisku
     * @param i numer elementu listy
     * @param facesList lista zawierajaca kolejne wybrane sciany
     * @param movesList lista zawierajaca kolejne wybrane ruchy
    */
    private void playMovesList(int i, List<String> facesList, List<String> movesList){      
        if(null != facesList.get(i)) switch (facesList.get(i)) {
                case "0": activeFace = 0; break;
                case "1": activeFace = 1; break;
                case "2": activeFace = 2; break;
                case "3": activeFace = 3; break;
                case "4": activeFace = 4; break;
                case "5": activeFace = 5; break;
                default: break;
            }
            if(null != movesList.get(i)) switch (movesList.get(i)) {
                case "q": command("q");  break;
                case "a": command("a");  break;
                case "z": command("z");  break;
                case "j": command("j");  break;
                case "k": command("k");  break;
                case "l": command("l");  break;
                case "x": command("x");  break;
                case "s": command("s");  break;
                case "w": command("w");  break;
                case "o": command("o");  break;
                case "i": command("i");  break;
                case "u": command("u");  break;
                default:break;
            }
    }
    
    
    /** 
     * Ulozenie wylosowanego ukladu, poporzez symulowanie wciskania przyciskow,
     * przy wylaczonej animacji.
     */
    private void klikaczWylosowany(){
        noAnimation = true;
        rememberMoves = false;
        for(int i =0; i<dlugoscListy; i++)
            playMovesList(i, randomFacesList, randomMovesList);
            
        rememberMoves = true;
        noAnimation = false;    
        activeFace = activeFaceBuff;
    }
    
    
    /** 
     * Realizacja odtwarzania zapamietanych ruchow, poprzez symulowanie wciskania przyciskow 
     * w stałych odstępach czasu. Dziala pod warunkiem pozytywnej wartosci {@link klikaczAnimowanyOn#playReplay}
     */
    TimerTask timerReplay = new TimerTask() {
        int replayCounter = 0;
        @Override
        public void run() {
            if(playReplayOn)
                if(replayCounter < dlugoscListy){
                    System.out.println("Krok "+ replayCounter);
                    playMovesList(replayCounter, facesList, pressedKeysList);
                    replayCounter++;
                } else
                if(replayCounter == dlugoscListy){  
                    blockInput = false;
                    rememberMoves = true;
                    playReplayOn = false;
                    replayCounter = 0;
                    activeFace = activeFaceBuff;
                }
        }
    };
        
    /** 
     * Wykonywanie pelnego obrotu elementow kostki znajdujacych sie w 
     * tablicy {@link #elementsToRotateList}, bez animacji 
     */
    private void rotateWithoutAnimation(){
        for ( int i = 0; i < 8; i++){                                                           
            int elementToRotate = currentLayout[elementsToRotateList[i]];
            rubiksCube.rotateElement(elementToRotate, rotation);
        }
        if(middleElement != -1){
            int elementToRotate = currentLayout[middleElement];
            rubiksCube.rotateElement(elementToRotate, rotation);
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
                    int numerElem = currentLayout[elementsToRotateList[i]];
                    rubiksCube.rotateElement(numerElem, rotation);
                }
                if(middleElement != -1){
                    int numerElem = currentLayout[middleElement];
                    rubiksCube.rotateElement(numerElem, rotation);
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
        //ZAPISANIE RUCHU
        if(rememberMoves) dopiszSciane();
        
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
                elementNumber = currentLayout[elementsToRotateList[i]];
                elementNumberPlus = currentLayout[elementsToRotateList[i+1]];      
                currentLayout[elementsToRotateList[i]] = prevElementNumber;
                prevElementNumber = elementNumber;
                currentLayout[elementsToRotateList[i+1]] = prevElementNumberPlus;
                prevElementNumberPlus = elementNumberPlus;
        }
        currentLayout[firstElementNumber] = prevElementNumber;
        currentLayout[firstElementNumberPlus] = prevElementNumberPlus;
        
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
    private void rotationRoll(boolean positive){
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
    private void rotationPitch( boolean positive){
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
    
    
    /** 
     * Konstruktor klasy Roobik 
     */
    public Roobik(){
        //ZMIENNE NA START
        activeFaceBuff = 0;
        activeFace = 1;
        blockInput = false;
        animationCounter = 20;
        rotation = new Transform3D();
        noAnimation = false;
        rememberMoves = true;
        playReplayOn = false;
        
        //RAMKA
        config = SimpleUniverse.getPreferredConfiguration();
        canvas3D = new Canvas3D(config);
        universe = new SimpleUniverse(canvas3D);
        Frame frame = new Frame("Kostka ogurat tego typu bec");       
        frame.setResizable(false);
        canvas3D.setSize(800, 800);
        canvas3D.addKeyListener(this);
        canvas3D.addMouseListener(this);
        
        rubiksCube = new Kostka();

        //GRAF SCENY I DOMYSLNY WIDOK
        BranchGroup scena = rubiksCube.sceneGraph;
        universe.addBranchGraph(scena);
        Transform3D transformPrzesObs = new Transform3D();
        transformPrzesObs.set(new Vector3f(0.0f, 0.0f, 4.7f));
        universe.getViewingPlatform().getViewPlatformTransform().setTransform(transformPrzesObs);
        
        //ZEGAR
        timer = new Timer();
        timer.scheduleAtFixedRate(timerAnimation, 0, 15);
        timer.scheduleAtFixedRate(timerReplay, 0, 300);
        
        //DETEKCJA OBIEKTOW NA PODSTAWIE KLIKNIEC MYSZY
        pickCanvas = new PickCanvas(canvas3D, scena);
        pickCanvas.setMode(PickCanvas.GEOMETRY);
        
        frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent winEvent) {System.exit(0);}});
        frame.add(canvas3D);
        frame.pack();
        frame.show();
    }
    
    /**
     * Glowna metoda klasy.
     * @param args 
     */
    public static void main(String[] args) {
        System.setProperty("sun.awt.noerasebackground", "true");
        Roobik rubik = new Roobik();
    }
    
        /** Losowanie kolejnych obrotow, i wpisanie ich na liste */
    static void losowyUklad(){
        Random generator = new Random();
        for(int i = 0; i < 123; i++){
            int sciana = generator.nextInt(6);
            int klawisz = generator.nextInt(12);
            List<String> availableFaces = Arrays.asList(
                    "0", "1", "2", "3", "4", "5", "6");
            List<String> availableMoves = Arrays.asList(
                    "u", "i", "o", "w", "s", "x", "j", "k", "l", "q", "a", "z");
            randomMovesList.add(availableMoves.get(klawisz));
            randomFacesList.add(availableFaces.get(sciana));      
        }
    }
    
    /** 
     * Tworzenie tablicy obracanych elementow 
     */
    private void createElementsToRotateList(int zero, int uno, int due, int tre, int quattro, int cinque, int sei, int sette){
            elementsToRotateList[0]=zero;               elementsToRotateList[1]=uno;
            elementsToRotateList[2]=due;                elementsToRotateList[3]=tre; 
            elementsToRotateList[4]=quattro;            elementsToRotateList[5]=cinque;
            elementsToRotateList[6]=sei;                elementsToRotateList[7]=sette;
    }
       
    /** 
     * Obsluga wyboru elementow za pomoca myszy
     * @param e 
     */
    @Override
    public void mouseClicked(MouseEvent e){
        pickCanvas.setShapeLocation(e);
        PickResult trafiony = pickCanvas.pickClosest();
        if (trafiony == null) {
        }else if(!blockInput){
            Object id = trafiony.getNode(PickResult.SHAPE3D).getUserData();
            if(id == "0"){command("0");}       
            else if(id == "1"){command("1");}  
            else if(id == "2"){command("2");}   
            else if(id == "3"){command("3");}   
            else if(id == "4"){command("4");}   
            else if(id == "5"){command("5");}   
            else if(id == "s11"){command("q");} 
            else if(id == "s10"){command("a");}
            else if(id == "s9"){command("z");} 
            else if(id == "s8"){command("j");} 
            else if(id == "s7"){command("k");} 
            else if(id == "s6"){command("l");} 
            else if(id == "s5"){command("x");} 
            else if(id == "s4"){command("s");} 
            else if(id == "s3"){command("w");} 
            else if(id == "s2"){command("o");} 
            else if(id == "s1"){command("i");} 
            else if(id == "s0"){command("u");} 
            else if(id == "r"){command("r");} 
            else if(id == "p"){command("p");} 
            else if(id == "t"){command("t");} 
                                                            
        }
    }
    
    /** 
     * Wykonanie obslugiwanej przez program komendy
     * @param klawisz id komendy 
     */
    void command(String klawisz){
        switch (klawisz) {
            case "r":  //resetowanie ukladu              
                pressedKeysList.clear();
                facesList.clear();
                randomMovesList.clear();
                randomFacesList.clear();
                rubiksCube.resetKostki(); 
                break;   
            case "t":  //tasowanie kostki                          
                pressedKeysList.clear();
                facesList.clear();
                randomMovesList.clear();
                randomFacesList.clear();
                activeFaceBuff = activeFace;
                rubiksCube.resetKostki();
                losowyUklad();
                dlugoscListy = randomMovesList.size();
                klikaczWylosowany();
                activeFace = activeFaceBuff;
                break;   
            case "p":  //odtwarzanie
                activeFaceBuff = activeFace;                
                rubiksCube.resetKostki();
                dlugoscListy = randomMovesList.size();
                klikaczWylosowany();
                dlugoscListy = pressedKeysList.size();
                rememberMoves = false;   
                playReplayOn = true;
                break; 
            case "w":
                if (rememberMoves) pressedKeysList.add("w");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = 10;
                    createElementsToRotateList(0, 1, 2, 11, 19, 18, 17, 9);
                    rotationRoll(true);
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
            case "s":
                if (rememberMoves) pressedKeysList.add("s");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = -1;
                    createElementsToRotateList(3, 4, 5, 13, 22, 21, 20, 12);
                    rotationRoll(true);
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
            case "x":
                if (rememberMoves) pressedKeysList.add("x");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = 15;
                    createElementsToRotateList(6, 7, 8, 16, 25, 24, 23, 14);
                    rotationRoll(true);
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
            case "q":
                if (rememberMoves) pressedKeysList.add("q");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = 10;
                    createElementsToRotateList(9, 17, 18, 19, 11, 2, 1, 0);
                    rotationRoll(false);
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
            case "a":
                if (rememberMoves) pressedKeysList.add("a");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = -1;
                    createElementsToRotateList(12, 20, 21, 22, 13, 5, 4, 3);
                    rotationRoll(false);
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
            case "z":                
                if (rememberMoves) pressedKeysList.add("z");
                if(activeFace >=0 && activeFace <=3){
                    middleElement = 15;
                    createElementsToRotateList(14, 23, 24, 25, 16, 8, 7, 6);
                    rotationRoll(false);
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
            case "j":
                if (rememberMoves) pressedKeysList.add("j");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = 12;
                    createElementsToRotateList(0, 3, 6, 14, 23, 20, 17, 9);
                    rotationPitch(true);
                }else
                    if(activeFace == 3){
                        middleElement = 13;
                        createElementsToRotateList(2, 11, 19, 22, 25, 16, 8 , 5);
                        rotationPitch(false);
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
            case "k":
                if (rememberMoves) pressedKeysList.add("k");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = -1;
                    createElementsToRotateList(1, 4, 7, 15, 24, 21, 18, 10);
                    rotationPitch(true);
                }else
                    if(activeFace == 3){
                        middleElement = -1;
                        createElementsToRotateList(1, 10, 18, 21, 24, 15, 7, 4);
                        rotationPitch(false);
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
            case "l":
                if (rememberMoves) pressedKeysList.add("l");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = 13;
                    createElementsToRotateList(2, 5, 8, 16, 25, 22, 19, 11);
                    rotationPitch(true);
                }else
                    if(activeFace == 3){
                        middleElement = 12;
                        createElementsToRotateList(0, 9, 17, 20, 23, 14, 6, 3);
                        rotationPitch(false);
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
            case "u":
                if (rememberMoves) pressedKeysList.add("u");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = 12;
                    createElementsToRotateList(9, 17, 20, 23, 14, 6, 3, 0);
                    rotationPitch(false);
                }else
                    if(activeFace == 3){
                        middleElement = 13;
                        createElementsToRotateList(2, 5, 8, 16, 25, 22, 19, 11);
                        rotationPitch(true);
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
            case "i":
                if (rememberMoves) pressedKeysList.add("i");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = -1;
                    createElementsToRotateList(1, 10, 18, 21, 24, 15, 7, 4);
                    rotationPitch(false);
                }else
                    if(activeFace == 3){
                        middleElement = -1;
                        createElementsToRotateList(1, 4, 7, 15, 24 ,21, 18, 10);
                        rotationPitch(true);
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
            case "o":
                if (rememberMoves) pressedKeysList.add("o");
                if(activeFace == 1 || activeFace == 4 || activeFace == 5){
                    middleElement = 13;
                    createElementsToRotateList(2, 11, 19, 22, 25, 16, 8, 5);
                    rotationPitch(false);
                }else
                    if(activeFace == 3){
                        middleElement = 12;
                        createElementsToRotateList(0, 3, 6, 14, 23, 20, 17, 9 );
                        rotationPitch(true);
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
            case "0":
                activeFace = 0;
                rotation.rotY(-Math.PI/2);
                rubiksCube.rotateArrows(rotation);
                break;                  
            case "1":
                activeFace = 1;
                rotation.rotY(0);
                rubiksCube.rotateArrows(rotation);
                break;  
            case "2":
                activeFace = 2;
                rotation.rotY(Math.PI/2);
                rubiksCube.rotateArrows(rotation);
                break;   
            case "3":
                activeFace = 3;
                rotation.rotY(Math.PI);
                rubiksCube.rotateArrows(rotation);
                break;   
            case "4":
                activeFace = 4;
                rotation.rotX(-Math.PI/2);
                rubiksCube.rotateArrows(rotation);
                break;   
            case "5":                
                activeFace = 5;
                rotation.rotX(Math.PI/2);
                rubiksCube.rotateArrows(rotation);
                break;   
            default:
                break;
        }
    }
    /** 
     * Obsluga klawiatury
     * @param ke 
     */
    @Override
    public void keyTyped(KeyEvent ke) {
        if (!blockInput)
            switch (ke.getKeyChar()){
            case 'w': command("w"); break;
            case 's': command("s"); break;
            case 'x': command("x"); break;  
            case 'q': command("q"); break;
            case 'a': command("a"); break;
            case 'z': command("a"); break;      
            case 'j': command("j");  break;
            case 'k': command("k"); break;
            case 'l': command("l"); break;        
            case 'u': command("u"); break;
            case 'i': command("i"); break;    
            case 'o': command("o");  break;
            case 'r': command("r"); break;
            case 'p': command("p"); break;
            case 't': command("t"); break;
            case '0': command("0"); break;
            case '1': command("1"); break;
            case '2': command("2"); break;
            case '3': command("3"); break;
            case '4': command("4"); break;
            case '5': command("5"); break;
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
