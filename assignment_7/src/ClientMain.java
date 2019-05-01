import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

public class ClientMain {
	private JTextArea incoming;
	private JTextField outgoing;
	private JPanel mainPanel;
	private BufferedReader reader;
	private PrintWriter writer;
	JTextField ip;
	static JComboBox<String> fonts;
	static JCheckBox jcb;


	String ip1;
    String ip2;


	public void run() throws Exception {
		initView();
		setUpNetworking();
	}

	private void initView() {
		JFrame frame = new JFrame("Chat Client");
		mainPanel = new JPanel();
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        //logic for ip stuff
		ip1 = JOptionPane.showInputDialog("Enter IP address");


        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Username", SwingConstants.RIGHT));
        label.add(new JLabel("Password", SwingConstants.RIGHT));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField username = new JTextField();
        controls.add(username);
        JPasswordField password = new JPasswordField();
        controls.add(password);
        panel.add(controls, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(frame, panel, "login", JOptionPane.QUESTION_MESSAGE);


        incoming = new JTextArea(15, 50);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);


		//going to make a dropdown to set font type
		String[] fontList = new String[] {"Times New Roman", "Arial", "Helveica", "Courier"};
		fonts = new JComboBox<>(fontList);
		fonts.setSelectedIndex(0);

		incoming.setBackground(Color.white);	//sets background color of the text area
		incoming.setForeground(Color.black);
		Font f = new Font((String)fonts.getSelectedItem(), Font.PLAIN, 12);
		incoming.setFont(f);



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
		fonts.addItemListener(itemListener);

		mainPanel.setBackground(Color.cyan);

		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(650, 500);

		frame.setVisible(true);


		//frame.getContentPane().setBackground(Color.BLUE);
		//frame.setBackground(Color.BLUE);

	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket(ip1, 4242);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}

	class SendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			writer.println(outgoing.getText());
			writer.flush();
			outgoing.setText("");
			outgoing.requestFocus();
		}
	}

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
			}
			else
			{
				incoming.setForeground(Color.black);
				incoming.setBackground(Color.white);
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
					
						incoming.append(message + "\n");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
