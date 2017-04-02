package atla;

public class Peer {
	
	private String apelido = null;
	private String ip = null;
	
	public Peer(String ip, String apelido) {
		this.setApelido(apelido);
		this.setIp(ip);
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
