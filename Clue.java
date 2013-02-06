import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

/**
 * Represents a clue, and answer in the crossword 
 */
public class Clue implements Serializable, Comparable<Clue>{
    private final int number, x,y;
    private final String clue, answer;
    private boolean isDown;
    private char[] userAnswer;
    private ClueSolver clueSolver;
    private ArrayList<SolvedLog> solvedLog = new ArrayList<SolvedLog>();
    private static String currentSolverName;

    /**
     * Sets up the clue
     * @param number The number of the clue
     * @param x The x coordinate of the first letter of the clue
     * @param y The y coordinate of the first letter of the clue
     * @param clue The hint that the user is given
     * @param answer The solution to the clue
     */ 
    Clue(int number, int x, int y, String clue, String answer){
        this.number = number;
        this.x = x;
        this.y = y;
        this.clue = clue;
        this.answer = answer;
        currentSolverName = "";
        userAnswer = new char[answer.length()];
    }

    /**
     * @return true if the clue has been solved
     */
    public boolean checkSolved(){
       return ((new String(userAnswer)).equals(answer.toUpperCase()));
    }

    /**
     * Sets the user answer for the given cell cooridnate
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param character the character the user has entered
     */
    public void  setUserAnswer(int x, int y, char character){
        int position;
        if (isDown) position =  y - this.y;
        else position = x - this.x;
        
        userAnswer[position] = character;
        
        if((checkSolved()) && (clueSolver == null)){
            clueSolver = new ClueSolver(currentSolverName, new Date());
            updateSolvedLog();
        }
    }

    /**
     * Adds to any subscribed solved logs the details of the clue solver
     */
    public void updateSolvedLog(){
         if(checkSolved()){
            for(SolvedLog sl: solvedLog){
                sl.addSolvedClue(this);
            }
        }
    }

    /**
     * Gets the character that the user has inputted at the given position
     * @param x The x coordinate of the cell
     * @param y The y coordinate of the cell
     * @return The character
     */
    public char getUserAnswer(int x, int y){
        int position;
        if (isDown) position =  y - this.y;
        else position = x - this.x;
        return userAnswer[position];
    }

    /**
     * @param isDown The value to set isDown to
     */
    public void setIsDown(boolean isDown){
        this.isDown = isDown;
    }

    /**
     * @return The value of isDown
     */
    public boolean getIsDown(){
        return isDown;
    }

    /**
     * @return The number of the clue
     */
    public int getNumber(){
        return number;
    }

    /**
     * @return The x coordinate of the associated cell
     */
    public int getX(){
        return x;
    }

    /**
     * @return The y coordinate of the associated cell
     */
    public int getY(){
        return y;
    }

    /**
     * @return The length of the answer
     */
    public int getSize(){
        return answer.length();
    }

    /**
     * @return The time/date that the clue was solved
     */
    public Date getDateSolved(){
        if(clueSolver != null){
            return clueSolver.getTimeCompleted();
        }
        return null;
    }

    /**
     * Adds a solvedLog to the list of subscribed solvedLogs   
     * SolvedLogs in this list will be provided with information when the clue is solved
     * @param solvedLog The solvedLog to be added
     */
    public void addSolvedLog(SolvedLog solvedLog){
        this.solvedLog.add(solvedLog);
        updateSolvedLog();

    }

    /**
     * Sets the name of the solver of the clue
     * This field is common to all clues, hence it's static nature
     * @param solverName The new name the solver will be given
     */
    public static void setCurrentSolverName(String solverName){
        currentSolverName = solverName;
    }

    /**
     * This function produces a bracketed string of the length of the answer
     * It takes into account answers which include spaces and hyphens
     * @return A string describing the length of the answer
     */
    public String getAnswerLength(){
        //A simple parse based roughly on on a finite state machine
        String result = "(";
        int count = 0;
        for(int i = 0; i < answer.length(); i++){
            if(answer.charAt(i) == ' '){
                result = result + Integer.toString(count) + ",";
                count = 0;
            }
            else if(answer.charAt(i) == '-'){
                result = result + Integer.toString(count) + "-";
                count = 0;
            }
            else count++;
        }
        return result + Integer.toString(count) + ")";
    }

    /**
     * @return A string representation of the clue
     */
    public String toString(){
        return Integer.toString(number) + ". "  + clue + " " + getAnswerLength(); 
    }

    /**
     * @return A string representation of the solved clue
     */
    public String getSolvedString(){
        return Integer.toString(number) + ". "  + clue + " " + getAnswerLength() + clueSolver; 
    }

    /**
     * Compares the clue to annother clue based on when they were solved
     * @param c The clue to compare with
     * @return a numerical representation of the comparison
     */
    public int compareTo(Clue c){
        if((getDateSolved() != null) && (c.getDateSolved() != null)){
            return getDateSolved().compareTo(c.getDateSolved());
        }
        else return 0;
    }
}

