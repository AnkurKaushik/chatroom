import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

public class ClientMain {
	private JTextArea incoming;
	private JTextField outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	JTextField ip;

	String ip1;
    String ip2;


	public void run() throws Exception {
		initView();
		setUpNetworking();
	}

	private void initView() {
		JFrame frame = new JFrame("Chat Client");
		JPanel mainPanel = new JPanel();
        Hashtable<String, String> logininformation = new Hashtable<String, String>();
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        //logic for ip stuff
		ip1 = JOptionPane.showInputDialog("Enter IP address");



//        //ip2 = JOptionPane.showInputDialog("Enter Username");
        //JOptionPane.showMessageDialog(frame, mainPanel, "login", JOptionPane.QUESTION_MESSAGE);
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

        //logininformation.put("user", username.getText());
        //logininformation.put("pass", new String(password.getPassword()));







        incoming = new JTextArea(15, 50);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);

		incoming.setBackground(Color.yellow);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		outgoing = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.setBackground(Color.blue);
        sendButton.setForeground(Color.white);
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		//ip = new JTextField(30);
		//mainPanel.add(ip);
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
