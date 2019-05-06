//import com.sun.javafx.logging.JFRInputEvent;
//import com.sun.jmx.mbeanserver.JmxMBeanServer;
//import com.sun.media.jfxmedia.events.PlayerStateEvent;
import javafx.embed.swing.JFXPanel;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.*;
import java.net.*;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Paths;
import java.util.Hashtable;


public class ClientMain {
	private JTextArea incoming;
	private JTextField outgoing;
	private JPanel mainPanel;
	private BufferedReader reader;
	private PrintWriter writer;
	JTextArea friends;
	static JComboBox<String> fonts;
	static JCheckBox jcb;
	static JTextField username;
	static JPasswordField password;
	static JLabel user;
	static JMenuBar jmb;
	static JMenuItem pass;
	static JMenuItem quit;
	static JMenuItem changePW;
    static JMenuItem load;
    static JMenuItem browse;
    static JMenuItem stop;

	String ip1;
    String passwordStr;
    static String musicPath;


    JFileChooser selection;


    static MediaPlayer mp;



    public void run() throws Exception {
		initView();

		//playMusic(); //calling this directly from the action listener now
		setUpNetworking();
	}

	private void initView() throws Exception {

		JFrame frame = new JFrame("Chat Client");
		frame.setPreferredSize(new Dimension(800, 800));
		frame.setMinimumSize(new Dimension(800, 800));
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(800,800));
		mainPanel.setMinimumSize(new Dimension(800,800));
        JPanel panel = new JPanel(new BorderLayout(5, 5));



        //logic for ip stuff
		ip1 = JOptionPane.showInputDialog("Enter IP address");





		jmb = new JMenuBar();
		frame.setJMenuBar(jmb);

		//This is the code for the File tab on the menu bar
        JMenu file = new JMenu("File");
        pass = new JMenuItem("Show Password");
        quit = new JMenuItem("Quit");
        changePW = new JMenuItem("Change Password");
		jmb.add(file);
		file.add(pass);
		file.add(changePW);
		file.add(quit);





		//this is the code for the Music tab of the menu bar

        JMenu music = new JMenu("Music");
        jmb.add(music);
        load = new JMenuItem("Load");
        browse = new JMenuItem("Browse");
        stop = new JMenuItem("Stop");
        //music.add(load); //seems redundant since we can browse for music
        music.add(browse);
        music.add(stop);
        selection = new JFileChooser();
        selection.setCurrentDirectory(new File("."));
        selection.setDialogTitle("Choose your character!");
        //selection.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        browse.addActionListener(browseMusic);
        load.addActionListener(loadMusic);
        stop.addActionListener(stopMusic);






        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Username", SwingConstants.RIGHT));
        label.add(new JLabel("Password", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        username = new JTextField();

        controls.add(username);
        password = new JPasswordField();
        controls.add(password);
        panel.add(controls, BorderLayout.CENTER);

		passwordStr = password.getText();

        JOptionPane.showMessageDialog(frame, panel, "Login", JOptionPane.QUESTION_MESSAGE);


        incoming = new JTextArea(25, 70);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);

		friends = new JTextArea(30,10);
		friends.setEditable(false);
		friends.setLineWrap(true);
		friends.setWrapStyleWord(true);
		friends.setSelectionStart(0);
		friends.append("Friends List");
		mainPanel.add(friends);

		//going to make a dropdown to set font type
		String[] fontList = new String[] {"Times New Roman", "Arial", "Helveica", "Courier"};
		fonts = new JComboBox<>(fontList);
		fonts.setSelectedIndex(0);

		incoming.setBackground(Color.white);	//sets background color of the text area
		incoming.setForeground(Color.black);
		Font f = new Font((String)fonts.getSelectedItem(), Font.PLAIN, 12);
		incoming.setFont(f);

		//Icon icon = new ImageIcon("C:\\Users\\a123a\\Documents\\GitHub\\project-7-chat-room-pr7-pair-29\\assignment_7\\src\\nothing.png");

		//emote = new JLabel("Icon");
		//emote.setIcon(icon);
		//((ImageIcon) icon).getImage().flush();
		//emote.setHorizontalAlignment(0);
		//emote.setVerticalAlignment(0);


		user = new JLabel("Username: " + username.getText());
		user.setForeground(Color.BLUE);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		outgoing = new JTextField(20);
		jcb = new JCheckBox("Night Mode");
		JButton sendButton = new JButton("Send");
		sendButton.setBackground(Color.blue);
        sendButton.setForeground(Color.white);
		sendButton.addActionListener(new SendButtonListener());
		outgoing.addActionListener(action);
		jcb.addItemListener(cbListner);
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		mainPanel.add(fonts);
		mainPanel.add(jcb);
		mainPanel.add(user);
		//mainPanel.add(emote);
		fonts.addItemListener(itemListener);
		quit.addActionListener(quitPgrm);
		pass.addActionListener(showpass);
		changePW.addActionListener(chPass);


		mainPanel.setBackground(Color.cyan);

		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(650, 500);

		//this makes the window actually visible
		frame.setVisible(true);
        outgoing.setText("nblahde blugugu");
		//adding the user data to username hashmap using IP address as a key
        //ServerMain.username.put(ServerMain.please.getLocalSocketAddress().toString(),user.getText());

		//frame.getContentPane().setBackground(Color.BLUE);
		//frame.setBackground(Color.BLUE);

	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket(ip1, 4243);
		String s = sock.getRemoteSocketAddress().toString();
        System.out.println("Remote Socket Address: " + s);
        System.out.println("Local Socket Address: " + sock.getLocalSocketAddress());

		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}

	public static void playMusic()
	{

		File f = new File(musicPath);
		URI u = f.toURI();
		Media m = new Media(u.toString());

        mp = new MediaPlayer(m);
		mp.play();
	}



	class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			writer.println(outgoing.getText());
			writer.flush();
			outgoing.setText("");
			outgoing.requestFocus();
		}
	}


	ActionListener showpass = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(passwordStr.equals(""))
				passwordStr = password.getText();
			JOptionPane.showMessageDialog(null, "Password: " + passwordStr);
		}
	};

	ActionListener quitPgrm = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};
	ActionListener chPass = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			passwordStr = JOptionPane.showInputDialog(null, "New Password: ");
		}
	};


    ActionListener stopMusic = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e ) {
            
            mp.stop();
        }
    };

    ActionListener loadMusic = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            musicPath = JOptionPane.showInputDialog(null, "Enter Music File Path: ");
        }
    };

    ActionListener browseMusic = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            new JFXPanel(); //this allows the music to work
            selection.showOpenDialog(mainPanel);
            musicPath = selection.getSelectedFile().getAbsolutePath();
            playMusic();

        }
    };


	Action action = new AbstractAction()
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			writer.println(outgoing.getText());
			writer.flush();
			outgoing.setText("");
			outgoing.requestFocus();
		}
	};

	ItemListener itemListener = new ItemListener() {
		public void itemStateChanged(ItemEvent itemEvent) {
			Font f = new Font((String)fonts.getSelectedItem(), Font.PLAIN, 12);
			incoming.setFont(f);
		}
	};

	ItemListener cbListner = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(jcb.isSelected())
			{
				incoming.setForeground(Color.white);
				incoming.setBackground(Color.black);
				friends.setForeground(Color.white);
				friends.setBackground(Color.black);
			}
			else
			{
				incoming.setForeground(Color.black);
				incoming.setBackground(Color.white);
				friends.setForeground(Color.black);
				friends.setBackground(Color.white);

			}
		}
	};

	public static void main(String[] args) {
		try {
			new ClientMain().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {

					System.out.println(message);
					if(message.equals("Guest 1: :cat:") || message.equals("Guest 2: :cat:"))
					{
						System.out.println("   /\\_/\\   ");
						System.out.println("  / o o \\  ");
						System.out.println(" (   \"   ) ");
						System.out.println("  \\~(*)~/  ");
						System.out.println("   // \\\\   ");
						incoming.append("   /\\_/\\   " + "\n");
						incoming.append("  / o o \\  " + "\n");
						incoming.append(" (   \"   ) " + "\n");
						incoming.append("  \\~(*)~/  " + "\n");
						incoming.append("   // \\\\   " + "\n");

						incoming.append("Cat" + "\n");

					}
					else if(message.equals("Guest 1: :dog:") || message.equals("Guest 2: :dog:"))
					{
						System.out.println("            /~~~~~~~~\\                           _");
						System.out.println("    ##\\__/ @)      ~~~~~~~~\\                     \\ \\ ) )");
						System.out.println("    |              /~~\\~~~~~                ((    |  \\");
						System.out.println("     \\    /           |                          /   |");
						System.out.println("      (~~~   /         \\____________/~~~~~~~~~~~~   /");
						System.out.println("       ~~~~|~                                     /");
						System.out.println("           :                                      |");
						System.out.println("            \\                                     |");
						System.out.println("            |                               /      \\");
						System.out.println("             \\  \\_         :         \\     /~~~\\    |");
						System.out.println("             /   :~~~~~|   :~~~~~~~~~~|   :     :   :");
						System.out.println("            /    :    /    :         /    :    /    :");
						System.out.println("        (~~~     )(~~~     )     (~~~     )(~~~     )");

						incoming.append("            /~~~~~~~~\\                           _" + "\n");
						incoming.append("    ##\\__/ @)      ~~~~~~~~\\                     \\ \\ ) )" + "\n");
						incoming.append("    |              /~~\\~~~~~                ((    |  \\" + "\n");
						incoming.append("     \\    /           |                          /   |" + "\n");
						incoming.append("      (~~~   /         \\____________/~~~~~~~~~~~~   /" + "\n");
						incoming.append("       ~~~~|~                                     /" + "\n");
						incoming.append("           :                                      |" + "\n");
						incoming.append("            \\                                     |" + "\n");
						incoming.append("            |                               /      \\" + "\n");
						incoming.append("             \\  \\_         :         \\     /~~~\\    |" + "\n");
						incoming.append("            /    :    /    :         /    :    /    :" + "\n");
						incoming.append("        (~~~     )(~~~     )     (~~~     )(~~~     )" + "\n");

						incoming.append("Dog" + "\n");

					}
					else if(message.length() >= 19)
					{
						if (message.substring(9, 18).equals("addFriend"))
						{
							System.out.print("reached");
							friends.append(message.substring(19, message.length()) + "\n");
							incoming.append(message.substring(19, message.length()) + " was added as a friend" + "\n");
						}
					}
					else
						incoming.append(message + "\n");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
