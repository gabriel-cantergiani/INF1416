public class Arquivo {

	public String nome;
	public String path;
	public byte[] digest_bytes;
	public String digest_hex;
	public String status;
	public boolean arquivo_existe;

	public Arquivo(String path){

		this.path = path;
		this.status = null;
		this.nome = path.substring(path.lastIndexOf("/")+1);

	}


}
