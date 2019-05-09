import autenticacao.identificacaoUsuario;

public class Main {
	
	public static void main(String args[]){
		
		/* Classe que implementa 1a etapa de autenticação */
		identificacaoUsuario idUsuario = identificacaoUsuario.getInstance();
		idUsuario.iniciarIdentificacao();
		
	}

}