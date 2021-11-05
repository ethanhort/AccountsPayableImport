package accountsPayable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

/**
 * Utility class to build and operate UI elements abstracted from main computation
 * @author Ethan Horton
 *
 */
public class UIHandler {

	//indices for input fields. used for array of text fields/array of values
	private static final int ID_TEXT = 0; 
	private static final int SESS_DATE_TEXT = 1; 
	private static final int SESS_DESCRIPTION_TEXT = 2;
	private static final int DOC_NUM_TEXT = 3;
	private static final int DOC_DATE_TEXT = 4;
	private static final int DOC_DESCRIPTION_TEXT = 5;
	private static final int EFFECTIVE_DATE_TEXT = 6;
	private static final int NUM_USER_INPUTS = 7; 

	private JFrame frame; 
	private String reportFilePath, distCodeFilePath; 
	private JTextField[] textFields = new JTextField[NUM_USER_INPUTS]; 
	private String[] userInputs = new String[NUM_USER_INPUTS]; 
	private boolean isFinished = false; 

	/**
	 * Basic constructor initializes frame in fullscreen mode as requested by client. 
	 */
	public UIHandler() {

		//Create Window that Program will be displayed in
		frame = new JFrame("Accounts Payable Import"); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		//create top level panel to contain all other UI components
		JPanel panel = new JPanel(); 
		frame.add(panel); 
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JLabel label = new JLabel("INPUT SHOULD NOT CONTAIN COMMAS"); 
		label.setFont(new Font("Serif", Font.PLAIN, 18));

		//create UI elements and add them to top-level panel
		//panel.add(fileBrowser()); //this needs to go lower
		panel.add(label); 
		panel.add(createTextFields()); 
		panel.add(submitPanel()); 

		frame.setVisible(true);
	}

	/**
	 * method checks if string is a double
	 * @param num string to be parsed
	 * @return true if string contains only numeric characters (and no decimals) false otherwise
	 */
	public boolean isNumeric(String num) {
		if (num == null) {
			return false; 
		}
		try {
			Double.parseDouble(num);
		}
		catch (NumberFormatException e){
			return false; 
		}
		return true; 
	}

	/**
	 * Create UI file browser to allow user to select report file for input
	 * @return JPanel containing file browser
	 */
	public JPanel fileBrowser() {

		//panel containing file browser button
		JPanel fileBrowserPanel = new JPanel();
		fileBrowserPanel.setBorder(BorderFactory.createEtchedBorder());
		fileBrowserPanel.setLayout(new BoxLayout(fileBrowserPanel, BoxLayout.X_AXIS));
		
		JPanel GLIBrowserPanel = new JPanel(); 
		GLIBrowserPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Please Select ADP Invoice GLI File"));
		
		JPanel distCodeBrowserPanel = new JPanel(); 
		distCodeBrowserPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Please Select Distribution Codes"));

		//initialize file chooser, button, and label to choose benefits GLI report
		JFileChooser reportChooser = new JFileChooser(); 
		JButton reportButton = new JButton("Browse"); 
		JLabel reportLabel = new JLabel(""); 

		//initialize file chooser, button, and label to choose distribution codes file
		JFileChooser distCodeChooser = new JFileChooser();
		JButton distCodeButton = new JButton("Browse");
		JLabel distCodeLabel = new JLabel(""); 

		//set file chooser so user can only choose excel spreadsheets
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".xlsx", "xlsx");
		reportChooser.setFileFilter(filter); 
		distCodeChooser.setFileFilter(filter);

		//action listener for report file browser button
		reportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//chosen = APPROVE_OPTION only if a file is chosen in the browser
				int chosen = reportChooser.showOpenDialog(null);

				//get path of chosen file and display in UI
				if (chosen == JFileChooser.APPROVE_OPTION) {
					reportFilePath = reportChooser.getSelectedFile().getAbsolutePath();
					reportLabel.setText(reportFilePath); 
				}
			}
		});

		//action listener for dist code file browser button
		distCodeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//chosen = APPROVE_OPTION only if a file is chosen in the browser
				int chosen = distCodeChooser.showOpenDialog(null);

				//get path of chosen file and display in UI
				if (chosen == JFileChooser.APPROVE_OPTION) {
					distCodeFilePath = distCodeChooser.getSelectedFile().getAbsolutePath();
					distCodeLabel.setText(distCodeFilePath); 
				}
			}
		});
		
		GLIBrowserPanel.add(reportButton); 
		GLIBrowserPanel.add(reportLabel);
		
		distCodeBrowserPanel.add(distCodeButton);
		distCodeBrowserPanel.add(distCodeLabel); 

		//add components to appropriate containers 
		fileBrowserPanel.add(GLIBrowserPanel); 
		fileBrowserPanel.add(distCodeBrowserPanel); 
		return fileBrowserPanel; 
	}
	
	public void popUp(String msg) {
		
		JFrame errorFrame = new JFrame("Rounding Exceeded Tolerance"); 
		errorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(); 
		errorFrame.add(panel);

		//initialize the error messages that will be displayed
		JLabel errorLabel = new JLabel("Error: " + msg); 
		errorLabel.setForeground(Color.RED);

		//close program button functionality
		JButton close = new JButton("Close"); 
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				errorFrame.dispose();
			}
		}); 

		//add subcomponents to top level panel; 
		panel.add(errorLabel); 
		panel.add(close); 

		//set layout for popup and draw it to screen
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		errorFrame.setPreferredSize(new Dimension(600, 400));
		errorFrame.pack();
		errorFrame.setLocationRelativeTo(null); 
		errorFrame.setVisible(true);

		//wait for user to terminate the program
		for (;;) {
			try {
				TimeUnit.SECONDS.sleep(2 );
			} catch (Exception e) {

			}
		}
		
	}

	/**
	 * pop up a window with error message and terminate program execution
	 * @param msg text to be displayed in error message
	 * @throws Exception throws exception to halt program
	 */
	public static void handleError(String msg) {

		//create frame and top level panel
		JFrame errorFrame = new JFrame("Something went wrong"); 
		errorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(); 
		errorFrame.add(panel);

		//initialize the error messages that will be displayed
		JLabel errorLabel = new JLabel("Error: " + msg); 
		errorLabel.setForeground(Color.RED);

		//close program button functionality
		JButton close = new JButton("Close"); 
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}); 

		//add subcomponents to top level panel; 
		panel.add(errorLabel); 
		panel.add(close); 

		//set layout for popup and draw it to screen
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		errorFrame.setPreferredSize(new Dimension(600, 400));
		errorFrame.pack();
		errorFrame.setLocationRelativeTo(null); 
		errorFrame.setVisible(true);

		//wait for user to terminate the program
		for (;;) {
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (Exception e) {

			}
		}
	}

	/**
	 * create all text fields for inputting data, their individual containing panels, and the subpanel that contains each of those 
	 * individual panels 
	 * 
	 * Yes, this is mostly just copy/pasted, and yes, I could have written it more cleanly, but it's just UI stuff and I already 
	 * had it all written out from a previous project, so it was just easier.
	 * @return subpanel containing all text input panels 
	 */
	public JPanel createTextFields() {

		//Panel that contains all text submission components
		JPanel textPanel = new JPanel(); 
		textPanel.setBorder(BorderFactory.createEtchedBorder());
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
		
		//Panel that contains new text components
		JPanel newTextPanel = new JPanel(); 
		newTextPanel.setBorder(BorderFactory.createEtchedBorder());

		//create panels for inputting text
		JPanel IDPanel = new JPanel(); 
		IDPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Session ID"));
		textPanel.add(IDPanel);
		JTextField IDText = new JTextField("");
		IDText.setColumns(10);
		textFields[ID_TEXT] = IDText; 
		IDPanel.add(IDText);

		JPanel sessDatePanel = new JPanel(); 
		sessDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Session Date"));
		textPanel.add(sessDatePanel);
		JTextField sessDateText = new JTextField("");
		sessDateText.setColumns(10);
		textFields[SESS_DATE_TEXT] = sessDateText;
		sessDatePanel.add(sessDateText);

		JPanel sessDescriptionPanel = new JPanel(); 
		sessDescriptionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Session Description"));
		textPanel.add(sessDescriptionPanel);
		JTextField sessDescriptionText = new JTextField("");
		sessDescriptionText.setColumns(10);
		textFields[SESS_DESCRIPTION_TEXT] = sessDescriptionText;
		sessDescriptionPanel.add(sessDescriptionText);

		JPanel docNumPanel = new JPanel(); 
		docNumPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Document Number"));
		textPanel.add(docNumPanel);
		JTextField docNumText = new JTextField("");
		docNumText.setColumns(10);
		textFields[DOC_NUM_TEXT] = docNumText; 
		docNumPanel.add(docNumText);

		JPanel docDatePanel = new JPanel();
		docDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Document Date"));
		textPanel.add(docDatePanel);
		JTextField docDateText = new JTextField("");
		docDateText.setColumns(10);
		textFields[DOC_DATE_TEXT] = docDateText; 
		docDatePanel.add(docDateText);

		JPanel docDescriptionPanel = new JPanel(); 
		docDescriptionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Document Description"));
		textPanel.add(docDescriptionPanel);
		JTextField docDescriptionText = new JTextField("");
		docDescriptionText.setColumns(10);
		textFields[DOC_DESCRIPTION_TEXT] = docDescriptionText;
		docDescriptionPanel.add(docDescriptionText);

		JPanel effectiveDatePanel = new JPanel(); 
		effectiveDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Effective Date"));
		textPanel.add(effectiveDatePanel);
		JTextField effectiveDateText = new JTextField("");
		effectiveDateText.setColumns(10);
		textFields[EFFECTIVE_DATE_TEXT] = effectiveDateText;  
		effectiveDatePanel.add(effectiveDateText);

		return textPanel; 
	}

	/**
	 * create panel containing submit button that pulls data from text fields when user is finished entering them
	 * @return panel containing submit button
	 */
	public JPanel submitPanel() {
		JPanel submitPanel = new JPanel(); 

		//create submit button and submit label to display potential error messages
		JLabel submitLabel = new JLabel(""); 
		submitLabel.setForeground(Color.red);
		JButton submitButton = new JButton("Submit"); 

		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//ensure user has entered a valid filepath
				if(reportFilePath != null && distCodeFilePath != null) {

					//tracks whether user has entered values in all fields
					boolean finished = true; 

					//get inputs from text field
					parseUserInputs(); 

					//ensure that all text fields contain a value and that none contain commas
					for(int i = 0; i < userInputs.length; i++) {
						if(userInputs[i].length() == 0) {
							submitLabel.setText("None of these fields may be empty");
							finished = false; 
						}
						else if(userInputs[i].contains(",")) {
							submitLabel.setText("Please remove all commas from these fields");
							finished = false; 
						}
					}

					if(finished) {

						//close window if user successfully submits form
						frame.dispose();
						isFinished = true; 
					}
				}
				else {
					submitLabel.setText("Please ensure both a GLI and Distribution Code file have been selected.");
				}
			}
		});

		//add components to panel
		submitPanel.add(submitLabel); 
		submitPanel.add(submitButton); 
		return submitPanel; 
	}

	/**
	 * iterate through array of text fields and pull text from each into other array that can be accessed by 
	 * non-UI system components
	 */
	public void parseUserInputs() {
		for (int i = 0; i < textFields.length; i++) {
			userInputs[i] = textFields[i].getText().trim(); 
		}
	}

	/**
	 * getter for user inputs 
	 * @return array of user inputs
	 */
	public String[] getUserInputs() {
		return userInputs; 
	}

	/**
	 * getter for file path of NLS report
	 * @return string representation of filepath 
	 */
	public String getGLIFilePath() {
		return reportFilePath; 
	}
	
	public String getDistCodeFilePath() {
		return distCodeFilePath; 
	}

	/**
	 * indicates whether the user has finished inputting information into all UI elements
	 * @return true if all fields were successfully completed, false otherwise
	 */
	public boolean isFinished() {
		return isFinished;
	}
}
