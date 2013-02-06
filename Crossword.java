import java.util.ArrayList;
import java.io.Serializable;

/**
 * Represents the crossoword puzzle that the user is currently playing
 */
public class Crossword implements Serializable {

    private final ArrayList<Clue> acrossClues, downClues;
    private final String title;
    private final int size;
    private String username;

    /**
     * Deafult constructor for Crossword
     * @param title The title of the crossword
     * @param size The size of the crossword grid (square)
     * @param acrossClues The across Clues
     * @param downClues The down Clues
     */
    Crossword(String title, int size, ArrayList<Clue> acrossClues, ArrayList<Clue> downClues){ 
        this.title = title;
        this.size = size;
        this.acrossClues = acrossClues;
        this.downClues = downClues;
    
        //Sets the correct isDown flag within the clues, and checks that the clues fit on the grid
        for(Clue c: acrossClues){
            c.setIsDown(false);
            if(c.getSize() + c.getX() > size){
                System.out.println("The word size was too large (across)");
                System.out.println("Attempting to resolve the problem by resizing");
                size = c.getSize() + c.getX();
            }
        }
        for(Clue c: downClues){
            c.setIsDown(true);
            if(c.getSize() + c.getY()  > size){
                System.out.println("The word size was too large (down)"); 
                System.out.println("Attempting to resolve the problem by resizing");
                size = c.getSize() + c.getY();
            }
        }
    }
    
    /**
     * @return The size of the crossword square grid
     */
    public int getSize(){
        return size;
    }

    /** 
     * @return The title of the crossword
     */
    public String getTitle(){
        return title;
    }

    /**
     * @return A list of all the across clues
     */
    public ArrayList<Clue> getAcrossClues(){
        return acrossClues;
    }

    /**
     * @return A list of all the down clues
     */
    public ArrayList<Clue> getDownClues(){
        return downClues;
    }

    /** 
     * @return A list of all the clues combined into one list
     */
    public ArrayList<Clue>  getClues(){
        ArrayList<Clue> result = new ArrayList<Clue>();
        for(Clue clue: acrossClues){
            result.add(clue);
        }
        for(Clue clue: downClues){
            result.add(clue);
        }
        return result;
    }

    /**
     * Sets the username of the user currently solving the crossword
     * @param username The users name
     */
    public void setUsername(String username){
        this.username = username;
        Clue.setCurrentSolverName(username);
    }

    /**
     * @return The user name of the person currently solving the crossword
     */
    public String getUsername(){
        return this.username;
    }
}

