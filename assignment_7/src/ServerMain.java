import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class ServerMain {
	private ArrayList<PrintWriter> clientOutputStreams;
	static ArrayList<String> listUsers = new ArrayList<>();
	static HashMap<String, String> username = new HashMap<>();
	int n = 0;
	public static void main(String[] args) {
		try {
			new ServerMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		listUsers.add("Ankur");
		listUsers.add("Nandakumar");
		listUsers.add("Walter");
		clientOutputStreams = new ArrayList<PrintWriter>();
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();

			n += 1;
			username.put(clientSocket.getRemoteSocketAddress().toString(), "Guest " + n);

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
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					//code to change username
					/*if(message.contains(":"))
					{
						switch(message)
						{
							case ":cat:": //do logic for cat
								break;
							case ":dog": //do logic for dog
								break;
							case ":panda:": //do logic for panda
								break;
						}
					}*/

						System.out.println("read " + message);
						notifyClients(ServerMain.username.get(s.toString()) + ": " + message);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
