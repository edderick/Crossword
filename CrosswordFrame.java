import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

/**
 * The main window containing the corssword
 */
public class CrosswordFrame extends JFrame{
    
    /**
     * The main method, Called when the progam starts running, 
     * Sets up the main window
     */
    public static void main(String[] args){
        final CrosswordFrame cf = new CrosswordFrame();
        //Ensures that the initialization is run from the EDT
        SwingUtilities.invokeLater(
            new Runnable() {
                public void run(){
                    cf.init();
                }
            }
        );
    }

      //Swing Components 
    private JLabel titleLabel, nameLabel, acrossLabel, downLabel, solvedLabel;
    private JList acrossList, downList;
    private SolvedLog solvedLog;
    private JScrollPane acrossListPane, downListPane, solvedLogPane;
    private JButton loadButton, saveButton;
    private JCheckBox solvedClueCheckBox;
    private JPanel[] panel;
    private JTextField nameField;
    private GridPanel gridPanel;

    /**
     * Constructor for CrosswordFrame
     * Sets the title of the frame
     */
    public CrosswordFrame(){
        //Calls JFrame's constructor to set the title
        super("Crossword");
    }

   /**
    * Initialiizer for Frame, sets up the components etc.
    */
    public void init(){
        setSize(1050, 525);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    
        //Create the components
        createGrid();
        createLabels();
        createLists();
        createSolvedLog();
        createButtons();
        createCheckBoxes();
        createTextFields();
        createPanels();
        initGrid();
        
        repaint();
        setVisible(true);

        //Sets the focus to the name field, prevents funny behavoir in the gridpanel...
        nameField.requestFocus();
    }
   
    /**
     * Initializes the grid
     * Does the things that require the other components to have been built
     */
    private void initGrid(){
        //Ensures the Lists contain no elements (for loading)   
        ((DefaultListModel)acrossList.getModel()).removeAllElements();
        ((DefaultListModel)downList.getModel()).removeAllElements();

        //Add all the clues to the lists
        for(Clue c: gridPanel.getCrossword().getAcrossClues()){
            ((DefaultListModel)acrossList.getModel()).addElement(c);
        }
        for(Clue c: gridPanel.getCrossword().getDownClues()){
            ((DefaultListModel)downList.getModel()).addElement(c);
        }
        gridPanel.addList(acrossList);
        gridPanel.addList(downList);

        //Ensure the solved log is empty, and then adds all solved clues
        solvedLog.removeAllSolvedClues();
        for(Clue clue: gridPanel.getCrossword().getClues()){
            clue.addSolvedLog(solvedLog);
        }
    }

    /**
     * Creates all the labels that appear in the window
     */
    private void createLabels(){
        titleLabel = new JLabel(gridPanel.getCrossword().getTitle());
        nameLabel = new JLabel("Name: ");
        acrossLabel = new JLabel("Across");
        downLabel = new JLabel("Down");
        solvedLabel = new JLabel("Solved");
       }
    
    /**
     * Creates the lists containing the clues
     */
    private void createLists(){
        acrossList = new JList(new DefaultListModel());
        downList = new JList(new DefaultListModel());

        //Lists are placed inside a scroll pane to allow scrolling
        acrossListPane = new JScrollPane(acrossList);
        downListPane = new JScrollPane(downList);
    
        acrossListPane.setPreferredSize(new Dimension(300, 265));
        downListPane.setPreferredSize(new Dimension(300, 265));

        acrossList.addMouseListener(new ListListener(acrossList, false));
        downList.addMouseListener(new ListListener(downList, true));
    }

    /**
     * The listener for the lists
     * When the list is clicked the appropriate clue is highlighted in the grid
     */
    private class ListListener extends  MouseAdapter{
        private JList list;
        private boolean isDown;
        
        /**
         * Constructor for ListListener
         * @param list The list the listener is for
         * @param isDown indicates if the list is down
         */
        public ListListener(JList list, boolean isDown){
            super();
            this.list = list;
            this.isDown = isDown;
        }
        
        /**
         * The action to be performed on click
         * @param e The MouseEvent that triggered the action
         */
        public void mouseClicked(MouseEvent e){
            int index = list.getSelectedIndex();
                    if (index >= 0){
                        ListModel listModel = list.getModel();
                        Clue clue = (Clue) listModel.getElementAt(index);
                        int x = clue.getX();
                        int y = clue.getY();
            
                        gridPanel.setFocus(x,y, isDown);
                    }
        }
    }

    /**
     * Creates the solved log
     */
    private void createSolvedLog(){
        solvedLog = new SolvedLog();
        solvedLogPane = new JScrollPane(solvedLog);
        solvedLog.setEditable(false);
        solvedLogPane.setPreferredSize(new Dimension(600, 140));
        //Since the checkbox is by default unticked, hide the solved log
        solvedLog.setVisible(false);
    }

    /**
     * Create the buttons and their listeners
     */
    private void createButtons(){
        loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener(){
            //Loads in new crossword object from file, and does all the intialization
            public void actionPerformed(ActionEvent e){
                Crossword cw =  CrosswordIO.readPuzzle();
                if (cw != null){              
                    //Remove old gridpanel, and add new one with new crossword
                    panel[3].remove(gridPanel);
                    gridPanel = new GridPanel(400,400, cw);
                    panel[3].add(gridPanel, BorderLayout.CENTER);

                    titleLabel = new JLabel(gridPanel.getCrossword().getTitle());

                    //Display the new grid panel 
                    repaint();
                    setVisible(true); 

                    //Set the username to that of the saved game
                    nameField.setText(cw.getUsername());
                    Clue.setCurrentSolverName(cw.getUsername());

                    //Do all the initliazation on the other components
                    initGrid();               
                }
            }
        });

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener(){
            //Write the current crossword to a file
            public void actionPerformed(ActionEvent e){
                CrosswordIO.writePuzzle(gridPanel.getCrossword());
            }
        });
    }

    /**
     * Create the check boxes
     */
    private void createCheckBoxes(){
        solvedClueCheckBox = new JCheckBox("Solved Clues Support");
        solvedClueCheckBox.addChangeListener(new ChangeListener(){
            //Shows and hides the solved log
            public void stateChanged(ChangeEvent e){
                if ( ((JCheckBox)e.getSource()).isSelected() ){
                    solvedLog.setVisible(true);
                }
                else solvedLog.setVisible(false);
            }
        });
    }

    /**
     * Create the text Fields
     */
    private void createTextFields(){
        nameField = new JTextField(10);
        nameField.addFocusListener(new FocusAdapter(){
            //When the user deselects the text field, the username should be updated
            public void focusLost(FocusEvent e){
                 String username = nameField.getText();
                 gridPanel.getCrossword().setUsername(username);
            }
        });
    }

    /**
     * Creates the grid using the example crossword
     */
    private void createGrid(){
        gridPanel = new GridPanel(400, 400, (new CrosswordExample()).getPuzzle());
    }

    /**
     * Creates all the panels
     * A tree of panels has been used to obtain the correct layout
     */
    private void createPanels(){
        panel = new JPanel[10];
        for(int i = 0; i < 10; i++){
            panel[i] = new JPanel();
        }

        //Force vertical layout with Border layout
        panel[1].setLayout(new BorderLayout());
        panel[3].setLayout(new BorderLayout());
        panel[2].setLayout(new BorderLayout());
        panel[7].setLayout(new BorderLayout());
        panel[8].setLayout(new BorderLayout());
        panel[9].setLayout(new BorderLayout());
     

        //Begin the tree
        panel[0].add(panel[1]); // Main <- Left
        panel[0].add(panel[2]); // Main <- Right
        panel[1].add(panel[3], BorderLayout.NORTH); // Left <- Grid
        panel[1].add(panel[4], BorderLayout.CENTER); // Left <- Name
        panel[1].add(panel[5], BorderLayout.SOUTH); // Left <- LoadSave
        panel[2].add(panel[6], BorderLayout.NORTH); // Right <- RightTop
        panel[2].add(panel[7], BorderLayout.SOUTH); // Right <- Solved
        panel[6].add(panel[8]); // RightTop <- Across
        panel[6].add(panel[9]); // RightTop <- Down

       
        //Add the components to the panels 
        panel[3].add(titleLabel, BorderLayout.NORTH);
        panel[3].add(gridPanel, BorderLayout.CENTER);
        panel[4].add(nameLabel);
        panel[4].add(nameField);
        panel[5].add(loadButton);
        panel[5].add(saveButton);
        panel[7].add(solvedLabel, BorderLayout.NORTH);
        panel[7].add(solvedLogPane, BorderLayout.CENTER);
        panel[7].add(solvedClueCheckBox, BorderLayout.SOUTH);
        panel[8].add(acrossLabel, BorderLayout.NORTH);
        panel[8].add(acrossListPane, BorderLayout.CENTER) ;
        panel[9].add(downLabel, BorderLayout.NORTH);
        panel[9].add(downListPane, BorderLayout.CENTER);

        //Add the main panel (and thus it's children) to the window
        getContentPane().add(panel[0]);
    }
}
