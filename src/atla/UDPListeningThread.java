package atla;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPListeningThread extends Thread {
	
	private DatagramSocket udpSocket = null;
	private byte[] buffer = null;
	private DatagramPacket request = null;
	private ChatManager chatManager= null;
	
	public UDPListeningThread(DatagramSocket udpSocket, ChatManager chatManager) {
		this.udpSocket = udpSocket;
		this.setChatManager(chatManager);
		buffer = new byte[1000];
		request = new DatagramPacket(buffer, buffer.length);
	}
	
	public void run() {
		String message = null;
			
		try {
			while(true) {
				this.udpSocket.receive(request);
				message = new String(request.getData());
				
				if(message.matches(".*\\|\\|\\|.*")) {
					System.out.println("Mensagem recebida: " + message);
				}
				else{
					System.out.println("Mensagem recebida em formato inapropriado. Erro de protocolo");
					String replyString = "Mensagem não processada. Erro de protocolo.";
					byte[] replyBytes = replyString.getBytes();
					DatagramPacket reply = new DatagramPacket(replyBytes, replyBytes.length, request.getAddress(), request.getPort());
					this.udpSocket.send(reply);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

	public void setChatManager(ChatManager chatManager) {
		this.chatManager = chatManager;
	}

}
