import java.awt.Graphics;
import java.awt.Color;

/**
 * A solid cell is any cell that is not part of a cleu
 */
public class SolidCell extends Cell{
   /**
    * Fills in the cell with a black background
    * @param g The graphics object it is drawn on
    */
   public void paint(Graphics g){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
