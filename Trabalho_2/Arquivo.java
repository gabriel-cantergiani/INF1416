public class Arquivo {

	public String nome;
	public String path;
	public byte[] digest_bytes;
	public String digest_hex;
	public String status;

	public Arquivo(String path){

		this.path = path;
		this.status = "";
		this.nome = path.substring(path.lastIndexOf("/")+1);

	}


}
