import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class ServerMain {
	private ArrayList<PrintWriter> clientOutputStreams;
	static String usernameInput;
	static HashMap<String, String> username = new HashMap<>();
	//static HashMap<String, String> socketMap = new HashMap<>();
	static Socket please;
	static SocketAddress helpMe;
	static DataInputStream incomingData;
	static int n = 0;
	public static void main(String[] args) {
		try {
			new ServerMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {

		clientOutputStreams = new ArrayList<PrintWriter>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4243);
		while (true) {
			Socket clientSocket = serverSock.accept();

			n += 1;
			username.put(clientSocket.getLocalSocketAddress().toString(), "Guest " + n);
			username.put(clientSocket.getLocalSocketAddress().toString(), usernameInput);
			System.out.println("Remote Socket Address: " + clientSocket.getRemoteSocketAddress());
			System.out.println("Local Socket Address: " + clientSocket.getLocalAddress());
			//username.put(clientSocket.getRemoteSocketAddress().toString(),  + n);
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			clientOutputStreams.add(writer);

			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			System.out.println("got a connection " + clientSocket.getLocalAddress());
		}

	}

	private void notifyClients(String message) {


		for (PrintWriter writer : clientOutputStreams) {
			writer.println(message);
			writer.flush();
		}
	}

	class ClientHandler implements Runnable {
		private BufferedReader reader;
		private SocketAddress s;

		public ClientHandler(Socket clientSocket) throws IOException {
			Socket sock = clientSocket;
			s = clientSocket.getRemoteSocketAddress();
			please = sock;
			helpMe = s;
			System.out.println(s);
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {

						System.out.println("read " + message);
						System.out.println("Remote Socket Address: " + s);
						notifyClients(ServerMain.username.get(s.toString()) + ": " + message);


				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
