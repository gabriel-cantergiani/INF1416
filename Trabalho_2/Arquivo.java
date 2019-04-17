public class Arquivo {

	public String nome;
	public String path;
	public byte[] digest_bytes;
	public String digest_hex;
	public String status;
	public boolean arquivo_existe_na_lista;

	public Arquivo(String path){

		this.path = path;
		this.status = "";
		this.arquivo_existe_na_lista = false;
		this.nome = path.substring(path.lastIndexOf("/")+1);

	}


}
