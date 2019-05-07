package autenticacao;

import conexaoBD.conexaoBD;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class autenticacaoSenha {
	
	private static autenticacaoSenha authSenha = null;
	private Random ran = null;
	private MessageDigest md = null;
	private Timer timer;
	private TimerTask block;

	private autenticacaoSenha() {
		
		ran = new Random();

		try{
			md = MessageDigest.getInstance("SHA1");
		}
		catch (NoSuchAlgorithmException e){
			System.err.println(e);
			System.exit(1);
		}
	}

	/* SINGLETON */
	public static autenticacaoSenha getInstance() {
		if (authSenha == null)
			authSenha = new autenticacaoSenha();
		return authSenha;
	}

	protected void iniciarAutenticacaoSenha(String login_name, ResultSet dadosUsuario) {

		/* FALTA:
				- Criar interface com teclado virtual para receber (pares de) digitos da senha.
				- Bloquear usuário caso haja mais de 3 tentativas e voltar para a etapa anterior.
				- É necessario criar um evento que será chamado em um tempo estipulado para desbloquear o usuario.
				- Passar para proxima etapa caso senha esteja correta.
		*/

		System.out.println("Iniciando autenticacao da senha pessoal. Usuário: "+login_name);
		
		int tentativas = 0;

		while (tentativas < 3){

			/* Obtém senha através do teclado virtual numérico */
			List<int[]> senha = geraTecladoVirtual();

			System.out.println("Senha digitada:");
			for(int i=0; i<senha.size(); i++){
				System.out.print("[");
				System.out.print(senha.get(i)[0]);
				System.out.print(" ou ");
				System.out.print(senha.get(i)[1]);
				System.out.print("]  ");
			}
			System.out.println();

			try{
				if (verificaSenha(senha, dadosUsuario.getString("SENHA"), dadosUsuario.getString("SALT"))){
					/* PASSA PARA PROXIMA ETAPA */
					tentativas = 0;
					System.out.println("Senha correta!");
				}
				else{
					tentativas += 1;
					System.out.println("Senha incorreta!");
				}

			}
			catch (SQLException e) {
				System.err.println(e);
				System.out.println("Erro ao obter dados do usuário.");
				System.exit(1);
			}

		}// fim while

		/* Passou de 3 tentativas -> Bloqueia a conta */

		System.out.println("Número de tentativas excedido. Você está bloqueado por 2min.");
		
		String query = "UPDATE TABLE USUARIOS SET BLOQUEADO=1 WHERE LOGIN_NAME='"+login_name+"';";

		try {
			Connection conn = conexaoBD.getInstance().getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);

			if (stmt != null)
        		stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao atualizar usuário no banco de dados.");
			System.exit(1);
		}
		
		timer = new Timer();		
		
		timer.schedule(block, 120000);
		
		block = new TimerTask() {
			@Override
			public void run() {
				System.out.println("Você já pode tentar novamente!");
		        timer.cancel(); //Terminate the timer thread
		        
		        
		        String query = "UPDATE USUARIOS SET BLOQUEADO=0 WHERE LOGIN_NAME='"+login_name+"';";
				try {
					Connection conn = conexaoBD.getInstance().getConnection();
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(query);

					if (stmt != null)
		        		stmt.close();
				}
				catch (SQLException e) {
					System.err.println(e);
					System.out.println("Erro ao atualizar usuario no banco de dados.");
					System.exit(1);
				}
			
			}
		};
	
		return;

	}

	private int[][] geraListaAleatoria(){

		List<Integer> digitos = new ArrayList<Integer>();
		int [][] lst = new int[5][2];
		
		for(int i=0; i<5; i++)
			lst[i] = new int[2];

		for(int i=0; i<10; i++)
			digitos.add(i);

		for(int i=0; i<5; i++){
			lst[i][0] = digitos.remove(ran.nextInt(digitos.size()));
			lst[i][1] = digitos.remove(ran.nextInt(digitos.size()));
		}

		return lst;

	}

	private List<int[]> geraTecladoVirtual(){

		Scanner scanner = new Scanner(System.in);

		/* Gera lista aletoria de pares para o teclado virtual */
		int [][] digitosTeclado = geraListaAleatoria();

		/* Array que guarda a senha (em pares) */
		List<int[]> senha = new ArrayList<>();

		/* Começa a capturar os "cliques" em cada botao do teclado. Nessa simulaçao, captura do teclado o numero equivalente a cada botao */
		int numBotaoClicado;
		int j = 0;
		while(j<9){

			/* Imprime o teclado atual */
			System.out.println("Digite o numero de cada botão (que seria) pressionado no teclado (ate 9 digitos). Digite -1 para terminar (ENTER).");
			System.out.println("Teclado:");
			for(int i=0; i<5; i++){
				System.out.print("Botao "+(i+1)+": [");
				System.out.print(digitosTeclado[i][0]);
				System.out.print(" ou ");
				System.out.print(digitosTeclado[i][1]);
				System.out.print("]  ");
			}
			System.out.println();

			/* Captura digito referente ao numero do botao*/
			while((numBotaoClicado = scanner.nextInt()) != -1 && ( numBotaoClicado < 1 ||  numBotaoClicado > 5))
				System.out.println("Número do botão inválido!");
			

			if(numBotaoClicado == -1)
				if (senha.size() < 6){
					System.out.println("A senha deve conter pelo menos 6 dígitos.");
					continue;
				}
				else
					break;

			/* Adiciona par de digitos na senha */
			senha.add(digitosTeclado[numBotaoClicado-1]);

			/* Gera nova lista aletoria de pares para o teclado virtual */
			digitosTeclado = geraListaAleatoria();

			/* Contabiliza numero de digitos da senha */
			j+=1;
		}

		return senha;

	}


	private boolean verificaSenha(List<int[]> senhaTestada, String senhaUsuario, String saltUsuario){

		String senhaCorrente = "";
		int[] indices = new int[9];

		/* Testa todas as possiveis combinações de pares */
		for(indices[0]=0; indices[0]<2; indices[0]++)
			for(indices[1]=0; indices[1]<2; indices[1]++)
				for(indices[2]=0; indices[2]<2; indices[2]++)
					for(indices[3]=0; indices[3]<2; indices[3]++)
						for(indices[4]=0; indices[4]<2; indices[4]++)
							for(indices[5]=0; indices[5]<2; indices[5]++)
								for(indices[6]=0; indices[6]<2; indices[6]++) {
									for(indices[7]=0; indices[7]<2; indices[7]++) {
										for(indices[8]=0; indices[8]<2; indices[8]++) {
											for (int i=0; i<senhaTestada.size(); i++)				// Para cada possivel combinacao dos pares:
												senhaCorrente += senhaTestada.get(i)[indices[i]];	// Monta string com senha corrente

											// Chama funcao que obtem hash da senha+salt em string hex
											System.out.println(senhaCorrente);
											String valorCalculado = geraHashDaSenha(senhaCorrente, saltUsuario);
											// Verifica se valorCalculado Ã© igual a valorArmazenado da senha 
								        	if (valorCalculado.equals(senhaUsuario))
								        		return true;

											senhaCorrente = "";
											if (senhaTestada.size()<9)
												break;
										}
										if (senhaTestada.size()<8)
											break;
									}
									if (senhaTestada.size() < 7)
										break;
								}

							/*
							
							*/

		/* NÃ£o achou nenhuma combinação de digitos valida */
		return false;
			
	}


	private String geraHashDaSenha(String senhaCorrente, String saltUsuario){

		// junta senha com sal, e calcula hash
		String senhaTemperada = senhaCorrente + saltUsuario;
		md.update(senhaTemperada.getBytes());
		byte [] hashBytes = md.digest();
		StringBuilder sb = new StringBuilder();

		// Transforma bytes em string de hexadecimal
		for(byte b:hashBytes){
    		sb.append(String.format("%02X", b));	
    	}
    	String valorCalculado = sb.toString();

		md.reset();
		// retorna valor calculado para hex(hash(senha+salt))
		return valorCalculado;

	}

	private String geraSaltAleatorio(){

		String salt = "";
		String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		for (int i=0; i<10; i++)
			salt += caracteres.charAt(ran.nextInt(caracteres.length()));

		return salt;
	}


	private void criaUserTeste(){

		String salt = geraSaltAleatorio();
		String senha = "01234567";
		String login_name = "teste@teste.com";
		String nome = "Teste";
		int grupo = 2;
		byte certificado = 0x22;
		int bloqueado = 0;

		String senhaHash = geraHashDaSenha(senha, salt);

		System.out.println(salt);
		System.out.println(senhaHash);
		System.out.println(senhaHash.length());

		String insert = "INSERT INTO USUARIOS VALUES ('"+login_name+"','"+nome+"',"+grupo+",'"+salt+"','"+senhaHash+"',"+certificado+","+bloqueado+");";

		try {
			Connection conn = conexaoBD.getInstance().getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(insert);

			if (stmt != null)
        		stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao atualizar usuário no banco de dados.");
			System.exit(1);
		}

		System.out.println("Usuário criado com sucesso!");

	}




}
