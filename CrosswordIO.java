import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;
import javax.swing.JFileChooser;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * This class handles all of the crossword IO
 */
public class CrosswordIO{
    
    /**
     * Reads a puzzle from file
     * @return a crossword object
     */
    public static Crossword readPuzzle(){
        JFileChooser jfc = new JFileChooser();            
        if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            try{
                File f = jfc.getSelectedFile();
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Crossword result = (Crossword) ois.readObject();
                ois.close();
                return result;
            }catch(IOException e){
                System.err.println("An IO error occured");
            }
            catch(ClassNotFoundException e){
                System.err.println("A class not found error occured");    
            }
        }
        return null;
    }

    /**
     * Writes a crossword 
     * @param crossword The crossword object that is to be saved
     */
    public static void writePuzzle(Crossword crossword){
        JFileChooser jfc = new JFileChooser();
        if(jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
            try{
                File f = jfc.getSelectedFile();
                FileOutputStream fos = new FileOutputStream(f);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(crossword);

                oos.flush();
                oos.close();
            }catch(IOException e){
                System.err.println("An IO error occured"); 
            }
        }
    }
}

