package atla;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MulticastListeningThread extends Thread {
	
	private MulticastSocket multicastSocket = null;
	private InetAddress group = null;
	private byte[] messageByte = null;
	private ChatManager chatManager;
	
	public MulticastListeningThread(MulticastSocket multicastSocket, ChatManager chatManager, InetAddress group) {
		this.multicastSocket = multicastSocket;
		this.messageByte = new byte[1000];
		this.chatManager = chatManager;
		this.group = group;
	}
	
	public void run() {
		try {
			this.multicastSocket.joinGroup(group);
			DatagramPacket messageIn = null;
			
			chatManager.setStatusChat(true);
			
			while(chatManager.getStatusMulticast()) {
				messageIn = new DatagramPacket(messageByte, messageByte.length);
				multicastSocket.receive(messageIn);
				
				String message = new String(messageIn.getData(), messageIn.getOffset(), messageIn.getLength());
				
				if (message.matches(".*\\|\\|\\|join.*")) {
					System.out.println("Alguém entrou!");
					Pattern pattern = Pattern.compile(".*[a-z1-9]*.*");
					Matcher matcher = pattern.matcher(message);
					//String apelide = matcher.group(1);
					Peer peer = new Peer(messageIn.getAddress().toString(), "qualquercoisa");
					chatManager.getPeers().add(peer);
				}
				
				else if(message.matches(".*\\|\\|\\|leave")) {
					Pattern pattern = Pattern.compile("[a-z1-9]*");
					Matcher matcher = pattern.matcher(message);
					Peer peer = new Peer(messageIn.getAddress().toString(), matcher.group(1));
					chatManager.getPeers().remove(peer);
				}
				
				else if(message.matches(".*\\|\\|\\|.*")) {
					System.out.println("Multicast recebido: " + message);
				}
				
				else {
					System.out.println("Mensagem recebida em formato inapropriado. Erro de protocolo");
					String replyString = "Mensagem não processada. Erro de protocolo.";
					byte[] replyBytes = replyString.getBytes();
					DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length, messageIn.getAddress(), messageIn.getPort());
					this.multicastSocket.send(reply);
				}
				
			}
			multicastSocket.leaveGroup(group);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (multicastSocket != null) multicastSocket.close();
		}
	}

}
