import javax.swing.JTextArea;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A class that displays a log of solved clues
 */
public class SolvedLog extends JTextArea{
        
    ArrayList<Clue> solvedClue = new ArrayList<Clue>();

    /**
     * Adds a clue to the solved clue list and displays it on screen
     * Will display in chronological order, irrespective of which order 
     * the clues were added in
     * @param clue The clue that is to be added to the list
     */
    public void addSolvedClue(Clue clue){
        if(solvedClue.contains(clue) == false){
            solvedClue.add(clue);
            Collections.sort(solvedClue);
            setText("");
            for(Clue c : solvedClue){
                append(c.getSolvedString() + "\n");
            }
        }
    }
    
    /**
     * Resets the log to empty and clears the visual component
     */
    public void removeAllSolvedClues(){
        solvedClue = new ArrayList<Clue>();
        setText("");
    }

}
