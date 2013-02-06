import java.util.Date;
import java.io.Serializable;

/**
 * Contains information about the solving of a clue
 */
public class ClueSolver implements Serializable{

    private String name;
    private Date timeCompleted;
    
    /**
     * Default constructor for ClueSolver
     * @param name The name of the user who completed the clue
     * @param timeCompleted The date/time the clue was completed
     */
    public ClueSolver(String name, Date timeCompleted){
        this.name = name;
        this.timeCompleted = timeCompleted; 
    }
    
    /**
     * @return The name of the user who solved the clue
     */
    public String getName(){
        return name;
    }

    /**
     * @return The date/time the clue was completed
     */
    public Date getTimeCompleted(){
        return timeCompleted;
    }
    
    /**
     * @return A string representation of the sting completion
     */
    public String toString(){
        return " by " + name + " at " + timeCompleted;
    }
}
