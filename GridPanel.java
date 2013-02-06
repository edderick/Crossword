import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JTextArea;
import javax.swing.JList;

/**
 * GridPanel represents a crossword on screen
 */
public class GridPanel extends JPanel{

    private int width, height;
    private Crossword crossword;
    private CharacterCell focus;
    private ArrayList<JList> list = new ArrayList<JList>();
    private boolean currentlyDown; //Needed for progreesing to next cell
    private Cell[][] cell;

    /**
     * Constructor for gridpanel
     * @param width Width of the onscreen component
     * @param height Height of the onscreen component
     * @param crossword The crossword associated with the panel
     */
    public GridPanel(int width, int height, Crossword crossword){
        super();        
        this.width = width;
        this.height = height;
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        
        this.crossword = crossword;
        
        //Create cells and panel to put them in
        cell = new Cell[crossword.getSize()][crossword.getSize()];
        setLayout(new GridLayout(crossword.getSize(), crossword.getSize() ));

        //Fill all the cells with solid cells
        for(int i = 0; i < crossword.getSize(); i++){
            for (int j = 0; j < crossword.getSize(); j++){
                cell[i][j] =  new SolidCell();
            }
        }

        //Replace the cells that represent part of a clue with character cells
        for(Clue c: crossword.getAcrossClues()){
            for(int i = 0; i < c.getSize(); i++){
                if (cell[c.getX() + i][c.getY()] instanceof CharacterCell){
                    ((CharacterCell) cell[c.getX() + i][c.getY()] ).addAcrossClue(c);
                }
                else {
                    cell[c.getX() + i][c.getY()] = new CharacterCell(c.getX() + i, c.getY());
                    ((CharacterCell)cell[c.getX() + i][c.getY()] ).addAcrossClue(c);
                }
            }
        }
        for(Clue c: crossword.getDownClues()){
            for(int i = 0; i < c.getSize(); i++){
                if (cell[c.getX()][c.getY() + i] instanceof CharacterCell){
                    ((CharacterCell)cell[c.getX()][c.getY() + i]).addDownClue(c);
                }
                else{ 
                    cell[c.getX()][c.getY() + i] = new CharacterCell( c.getX(), c.getY() + i);
                    ((CharacterCell)cell[c.getX()][c.getY() + i]).addDownClue(c);
                }
            }
        }
        
        //Add the cells to the grid
        for(int i = 0; i < crossword.getSize(); i++){
            for (int j = 0; j < crossword.getSize(); j++){
                add(cell[j][i]);
            }
        }
          
    }

    /**
     * Highlights or unhighlights the cells associated with a clue
     * @param clue the clue to be highlighted
     * @param isHighlighted flag to indicate if it should be highlighted or unhighlighted
     */
    public void setHighlightedClue(Clue clue, boolean isHighlighted){
        for(int i = 0; i < clue.getSize(); i++){
            if(clue.getIsDown()) ((CharacterCell)cell[clue.getX()][clue.getY() + i]).setHighlighted(isHighlighted);
            else ((CharacterCell)cell[clue.getX() + i][clue.getY()]).setHighlighted(isHighlighted);
        }
    }

    /**
     * Highlights the given cell and its clue
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param isDown true if the clue is down
     */
    public void setFocus(int x, int y, boolean isDown){
        //Check that the given cell is a character cell
        if(cell[x][y] instanceof CharacterCell){
            CharacterCell sender = ((CharacterCell)cell[x][y]);
            
            //As long as the current cell isn't being selected again, 
            //remove the focus from the old cell
            if (focus != null){
                setHighlightedClue(focus.getClue(), false);
                if(focus != sender){
                    focus.setFocus(false);
                }
            }
            
            //Sets the focus and highlighting to the gievn cell
            sender.setFocus(true);
            sender.setIsDown(isDown);
            setHighlightedClue(sender.getClue(), true);
            focus = sender;
            repaint();

            currentlyDown = isDown;

            //Set the given cell as the selection in the lists
            for(JList l: list){
                l.clearSelection(); 
                l.setSelectedValue(sender.getClue(), true);
            }

        }else System.err.println("Not a character cell");

    } 

    /**
     * Focusses the next cell in the clue
     */
    public void focusNextCell(){
        //If a cell is currently focused
        if(focus != null){
            int x = focus.getCellX();
            int y = focus.getCellY();

            if(currentlyDown) y++;
            else x++;
       
            int size = crossword.getSize();

            if((x < size) && (y < size)){        
                if(cell[x][y] instanceof CharacterCell) setFocus(x,y,currentlyDown);        
            }
        }
    }
 
    /**
     * Focusses the next cell in the clue
     */
    public void focusPreviousCell(){
        //If a cell is currently focused
        if(focus != null){
            int x = focus.getCellX();
            int y = focus.getCellY();

            if(currentlyDown) y--;
            else x--;
       
            int size = crossword.getSize();

            if((x >= 0) && (y >= 0)){        
                if(cell[x][y] instanceof CharacterCell) setFocus(x,y,currentlyDown);        
            }
        }
    }      

    /**
     * @return the crossword associated with the gridpanel
     */
    public Crossword getCrossword(){
        return crossword;
    }

    /**
     * Adds a list to the list of subscribed lists
     * Subscribed lists can interact with the grid
     * @param list The list to add
     */
    public void addList(JList list){
        this.list.add(list);
    }

    /**
     * Draws to the screen the stuff associated with the gird panel
     * @param g The graphics to draw to
     */
    public void paint(Graphics g){
        //Paints the JPanel stuff
        super.paint(g);
        g.setColor(Color.BLACK);
        //Draws lines along the side and bottom of the panel
        //Makes up for lines missed by the cells
        g.drawLine(getWidth() - 2, 2, getWidth() - 2, getHeight() - 2);
        g.drawLine(2, getHeight() - 2, getWidth() - 2, getHeight() -2);
    }
}
