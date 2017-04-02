package atla;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

public class ChatManager {
	private DatagramSocket udpSocket = null;
	private MulticastSocket multicastSocket = null;
	private UDPListeningThread udpThread = null;
	private MulticastListeningThread multicastThread = null;
	private LinkedList<Peer> peers = null;
	private String apelido;
	private int privatePort;
	private int multicastPort;
	private Scanner scanner = null;
	private String destinationIpString = null;
	private String messageString = null;
	private byte[] messageBytes = null;
	private boolean statusChat;
	private DatagramPacket request = null;
	private InetAddress group;
	
	
	public ChatManager(String apelido, int privatePort, int multicastPort) {
		setPeers(new LinkedList<>());
		this.apelido = apelido;
		this.privatePort = privatePort;
		this.multicastPort = multicastPort;
		this.scanner = new Scanner(System.in);
		
		try {
			this.group = InetAddress.getByName("225.1.2.3");
			udpSocket = new DatagramSocket(privatePort);
			multicastSocket = new MulticastSocket(multicastPort);
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void initialize() {
		udpThread = new UDPListeningThread(udpSocket, this);
		udpThread.start();
		
		multicastThread = new MulticastListeningThread(multicastSocket, this, group);
		multicastThread.start();
	}

	public LinkedList<Peer> getPeers() {
		return peers;
	}


	public void setPeers(LinkedList<Peer> peers) {
		this.peers = peers;
	}

	public void sendPrivateMessage() {
		DatagramSocket socketPrivate = null;
		InetAddress ipDestino = null;
		
		try {
			socketPrivate = new DatagramSocket();
			String formatedMessage = /*apelido + "|||" + */messageString;
			messageBytes = formatedMessage.getBytes();
			ipDestino = InetAddress.getByName(destinationIpString);
			request = new DatagramPacket(messageBytes, messageBytes.length, ipDestino, privatePort);
			socketPrivate.send(request);
			socketPrivate.close();
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void createPeersTest() {
		
		peers.add(new Peer("localhost", "henrique"));
		peers.add(new Peer("localhost", "vitorio"));
		peers.add(new Peer("localhost", "otavio"));
		peers.add(new Peer("localhost", "daniel"));
	}

	public void printNameOfPeers() {
		int i = 0;
		for(Peer peer : peers) {
			System.out.println("(" + i++ + ")" + peer.getApelido() + ":" + peer.getIp());
		}
	}

	public void sendMessageFor(int option) {
		Peer privatePeer = peers.get(option);
		this.destinationIpString = privatePeer.getIp();
		sendPrivateMessage();
	}

	public void requestMessage() {
		System.out.println("IP Destino: ");
		destinationIpString = scanner.nextLine();
		//destinationVerify();
		
		System.out.println("MSG: ");
		messageString = scanner.nextLine();
	}

	private void destinationVerify() {
		for(Peer peer : peers) {
			if(!peer.getIp().equals(destinationIpString)) {
				peers.add(new Peer(destinationIpString, ""));
			}
		}
	}

	public boolean getStatusMulticast() {
		return statusChat;
	}

	public void setStatusChat(boolean b) {
		this.statusChat = b;
	}

	public void sendGroupMessage() {
		System.out.println("MSG:");
		messageString = scanner.nextLine();
		String formatedMessage = apelido + "|||" + messageString;
		messageBytes = formatedMessage.getBytes();
		request = new DatagramPacket(messageBytes, messageBytes.length, group, multicastPort);
		try {
			multicastSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void controlMessage(String string) {
		
		String formatedMessage = apelido + "|||" + string;
		messageBytes = formatedMessage.getBytes();
		request = new DatagramPacket(messageBytes, messageBytes.length, group, multicastPort);
		try {
			multicastSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
