import java.io.File;
import java.util.Map;

import autenticacao.identificacaoUsuario;
import banco.conexaoBD;
import banco.Registro;
import banco.Mensagem;

public class Main {
	public static File arq;
	
	public static void main(String args[]){
		
		/* iniciando conexao com o banco q sera usada durante todo o sistema */
		conexaoBD.getInstance().getConnection();
		
		Map<Integer, String> mensagem;
		mensagem = (new Mensagem()).criaMap();
		
		arq = new File("log.txt");
				
		Registro registro = new Registro();
		registro.getMensagem(mensagem);
		registro.login_name = "";
		registro.insereRegistro(1001, "");
		
		/* Classe que implementa 1a etapa de autenticacao */
		identificacaoUsuario idUsuario = identificacaoUsuario.getInstance();
		idUsuario.iniciarIdentificacao();
	}

}