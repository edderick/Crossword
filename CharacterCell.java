import java.awt.Graphics;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * A cell that contains a character of a Clue
 */
public class CharacterCell extends Cell{
    
    private Clue acrossClue, downClue;
    private final int x, y;
    private boolean isFirst; //True if is the first letter of a clue
    private boolean isFocus, isHighlighted, isDown, defaultDown;
    private char[] character; //This is an array to allow printing
    private char[] clueNumber;

    /**
     * Constructor for Character cell
     * Sets up cell and listeners
     * @param x The x coordinate of the cell
     * @param y The y coordinate of the cell
     */
    public CharacterCell(int x, int y){
        super();
        this.x = x;
        this.y = y;
        isFirst = false;
        isFocus = false;
        isHighlighted = false;
        isDown = false;

        character = new char[1];
        character[0] = ' ';

        //Allow the cell to obtain focus
        setFocusable(true);
        setRequestFocusEnabled(true);

        addMouseListener(new MouseAdapter(){
            //If it is already focused swap isDown, otherwise set defaultdown
            public void mousePressed(MouseEvent e){ //Mouse pressed is used to prevent dragging issue
                if(isFocus){ 
                    ((GridPanel)getParent()).setFocus(CharacterCell.this.x, CharacterCell.this.y, !isDown);
                }else{
                    ((GridPanel)getParent()).setFocus(CharacterCell.this.x, CharacterCell.this.y, defaultDown);
                }

            }
});
        
        addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                //All letters are made capital to resolve case sensitivity issues
                character[0] = Character.toUpperCase(e.getKeyChar());
                //If the character is not a crazy special character then write it to the cell and move on
                if (((int) character[0] >= 32) && ((int) character[0] <= 126)) {  
                    ((GridPanel)getParent()).focusNextCell();
            
                    if(acrossClue != null) acrossClue.setUserAnswer(CharacterCell.this.x,CharacterCell.this.y,character[0]);
                    if(downClue != null) downClue.setUserAnswer(CharacterCell.this.x,CharacterCell.this.y,character[0]);

                    repaint();
                }
                //If the character is a back space, set this cell empty and move back
                else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    character[0] = ' ';
                    ((GridPanel)getParent()).focusPreviousCell();
            
                    if(acrossClue != null) acrossClue.setUserAnswer(CharacterCell.this.x,CharacterCell.this.y,character[0]);
                    if(downClue != null) downClue.setUserAnswer(CharacterCell.this.x,CharacterCell.this.y,character[0]);

                    repaint();
                }
                //If the character is a delete, set this cell empty and stay put
                else if(e.getKeyCode() == KeyEvent.VK_DELETE){
                    character[0] = ' ';
                    repaint();
                }
            }
        });

        addFocusListener(new FocusAdapter(){
            //Set the isFocus flag and force repaint
            public void focusGained(FocusEvent e){
                isFocus = true;
                repaint();
            }
            public void focusLost(FocusEvent e){
                isFocus = false;
                repaint();
            }
        });
    }

    /**
     * Associates an across clue with the cell
     * @param clue The clue to be associated
     */
    public void addAcrossClue(Clue clue){
        acrossClue = clue;
        //Check to see if the cell is the first of this clue 
        if ((acrossClue.getX() == x) && (acrossClue.getY() == y)){ 
            isFirst = true;
            clueNumber = Integer.toString(clue.getNumber()).toCharArray();
        }
        calculateDefaultDown();
        //Allows support for loading a clue with a characater saved for this cell
        character[0] = clue.getUserAnswer(x,y); 
    }

    /**
     * Associates a down clue with the cell
     * @param clue The clue to be associated
     */
    public void addDownClue(Clue clue){
        downClue = clue;
        //Check to see if the cell is the first of this clue 
        if ((downClue.getX() == x) && (downClue.getY() == y)){ 
            isFirst = true;
            clueNumber = Integer.toString(clue.getNumber()).toCharArray();
        }
        calculateDefaultDown();
        //Allows support for loading a clue with a characater saved for this cell
        character[0] = clue.getUserAnswer(x,y); 
    }

    /**
     * Calculates if the default orientation for the clue should be across or down
     * Based on which clues exist and which clue this is the first letter of 
     */
    private void calculateDefaultDown(){
        if(downClue != null){
            if((downClue.getX() == x) && (downClue.getY() == y)) defaultDown = true;
        }
        
        if (acrossClue != null){
            if((acrossClue.getX() == x) && (acrossClue.getY() == y)) defaultDown = false;
        }
         
        if (acrossClue == null) defaultDown = true;
        if (downClue == null) defaultDown = false;
           
        isDown = defaultDown;    
    }

    /**
     * @return true if the cell is surrently in focus
     */
    public boolean getIsFocus(){
        return isFocus;
    }

    /**
     * @param isFocus the value to set isFocus to
     */
    public void setFocus(boolean isFocus){
        this.isFocus = isFocus;
        if(isFocus) requestFocusInWindow();
    }

    /**
     * @isHighlighted the value to set isFocus to
     */
    public void setHighlighted(boolean isHighlighted){
        this.isHighlighted = isHighlighted;
    }

    /**
     * @return the x coordinate of the cell
     */
    public int getCellX(){
        return x;
    }

    /** 
     * @return the y coordinate of the cell
     */
    public int getCellY(){
        return y;
    }

    /**
     * @return true if isDown
     */
    public boolean getIsDown(){
        return isDown;
    }

    /**
     * @param isDown the value to set isDown to
     */
    public void setIsDown(boolean isDown){
        this.isDown = isDown;
    }

    /**
     * @return The current clue, depends on current isDown
     */
    public Clue getClue(){
        if((isDown) && (downClue != null)) return downClue;
        else if((!isDown) && (acrossClue != null)) return acrossClue;
            
        //Toggle isDown    
        isDown = !isDown;

        if((isDown) && (downClue != null)) return downClue;
        else if((!isDown) && (acrossClue != null)) return acrossClue;

        System.out.println("No Associated Clue");
        return null;
    }

    /**
     * Paints all the cell related stuff to the screen
     * @param g The Graphics object to draw it to
     */
    public void paint(Graphics g){
        //Fill in white
        if(isHighlighted){
            g.setColor(Color.YELLOW);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        else{
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        //Draw a red border to indicate that it is the focus
        if(isFocus){
            int thickness = 4;
            g.setColor(Color.RED);
            g.fillRect(0,0,getWidth()-0,getHeight()-0);
            g.setColor(Color.YELLOW);
            g.fillRect(thickness + 1, thickness + 1, getWidth() - (2 * thickness) - 1, getHeight() - (2 * thickness) - 1);
        }
        //Draw Border
        g.setColor(Color.BLACK);
        g.drawLine(0, 0, getWidth(), 0);
        g.drawLine(0, 0, 0, getHeight());

        //Draw the number if it is the first of a clue
        if(isFirst){
            g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, getHeight()/3));
            if (downClue != null)
                g.drawChars(clueNumber, 0, clueNumber.length , 3, g.getFontMetrics().getAscent()); 
            else if (acrossClue != null)
                g.drawChars(clueNumber, 0, clueNumber.length,  3, g.getFontMetrics().getAscent());
        }

        //Draw the character held in the cell
        if(character[0] != ' '){
            g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, getHeight()/2));
            //M is usually the widest character
            g.drawChars(character, 0, 1, getWidth() - g.getFontMetrics().stringWidth("M") - 5, getHeight() - g.getFontMetrics().getDescent() - 3);
        }
    }
}
