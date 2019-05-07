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
	private Connection conn;
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

		conn = conexaoBD.getInstance().getConnection();
	}

	/* SINGLETON */
	public static autenticacaoSenha getInstance() {
		if (authSenha == null)
			authSenha = new autenticacaoSenha();
		return authSenha;
	}

	protected void iniciarAutenticacaoSenha(String login_name) {

		/* FALTA:
				- Criar interface com teclado virtual para receber (pares de) digitos da senha.
				- Passar para proxima etapa caso senha esteja correta.
		*/

		
		System.out.println("Iniciando autenticacao da senha pessoal. Usu?io: "+login_name);
		String senhaUsuario = "";
		String saltUsuario = "";
		
		try {
			String query = "SELECT * FROM USUARIOS WHERE LOGIN_NAME='"+login_name+"';";
			Statement stmt = conn.createStatement();
			ResultSet dadosUsuario = stmt.executeQuery(query);

			if (!dadosUsuario.next())
				System.out.println("Usuário não encontrado!");

			senhaUsuario = dadosUsuario.getString("SENHA");
			saltUsuario = dadosUsuario.getString("SALT");

			stmt.close();
			dadosUsuario.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao buscar usuário no banco de dados.");
			System.exit(1);
		}			

		int tentativas = 0;

		while (tentativas < 3){

			/* Obt? senha atrav? do teclado virtual num?ico */
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

			if (verificaSenha(senha, senhaUsuario, saltUsuario)){
				/* PASSA PARA PROXIMA ETAPA */
				tentativas = 0;
				System.out.println("Senha correta!");
			}
			else{
				tentativas += 1;
				System.out.println("Senha incorreta!");
			}

		}// fim while

		/* Passou de 3 tentativas -> Bloqueia a conta */

		System.out.println("N?mero de tentativas excedido. Voc?est?bloqueado por 2min.");
		
		String query = "UPDATE USUARIOS SET BLOQUEADO=1 WHERE LOGIN_NAME='"+login_name+"';";

		try {
			Statement stmt = conn.createStatement();
			int res = stmt.executeUpdate(query);

			if (res == 0)
				System.out.println("Update não retornou resultados...");

			if (stmt != null)
        		stmt.close();
        	System.out.println("Usuario bloqueado!");
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao bloquear usuario no banco de dados.");
			System.exit(1);
		}
		
		timer = new Timer();		
		
		block = new TimerTask() {
			@Override
			public void run() {
				System.out.println("Voce ja pode tentar novamente!");
		        timer.cancel(); //Terminate the timer thread
		        
		        
		        String query = "UPDATE USUARIOS SET BLOQUEADO=0 WHERE LOGIN_NAME='"+login_name+"';";
				try {
					conn = conexaoBD.getInstance().getConnection();
					Statement stmt = conn.createStatement();
					stmt.executeUpdate(query);

					if (stmt != null)
		        		stmt.close();
				}
				catch (SQLException e) {
					System.err.println(e);
					System.out.println("Erro ao desbloquear usuario no banco de dados.");
					System.exit(1);
				}
			
			}
		};
		
		timer.schedule(block, 120000);
	
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

		/* Come? a capturar os "cliques" em cada botao do teclado. Nessa simula?o, captura do teclado o numero equivalente a cada botao */
		int numBotaoClicado;
		int j = 0;
		while(j<9){

			/* Imprime o teclado atual */
			System.out.println("Digite o numero de cada bot? (que seria) pressionado no teclado (ate 9 digitos). Digite -1 para terminar (ENTER).");
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
				System.out.println("N?mero do bot? inv?ido!");
			

			if(numBotaoClicado == -1)
				if (senha.size() < 6){
					System.out.println("A senha deve conter pelo menos 6 d?itos.");
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

		/* Testa todas as possiveis combina?es de pares */
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
											// Verifica se valorCalculado é igual a valorArmazenado da senha 
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

		/* Não achou nenhuma combina?o de digitos valida */
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


	// private void criaUserTeste(){
	private void desbloqueaUsersTeste(){

		// String salt = geraSaltAleatorio();
		// String senha = "01234567";
		// String login_name = "teste@teste.com";
		// String nome = "Teste";
		// int grupo = 2;
		// byte certificado = 0x22;
		// int bloqueado = 0;

		// String senhaHash = geraHashDaSenha(senha, salt);

		// System.out.println(salt);
		// System.out.println(senhaHash);
		// System.out.println(senhaHash.length());

		try {
			// String insert = "INSERT INTO USUARIOS VALUES ('"+login_name+"','"+nome+"',"+grupo+",'"+salt+"','"+senhaHash+"',"+certificado+","+bloqueado+");";
			String insert = "UPDATE USUARIOS SET BLOQUEADO=0 WHERE (LOGIN_NAME='admin@admin.com' OR LOGIN_NAME='teste@teste.com');";
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

		// System.out.println("Usuario criado com sucesso!");
		System.out.println("Usuarios desbloqueados com sucesso!");

	}




}
