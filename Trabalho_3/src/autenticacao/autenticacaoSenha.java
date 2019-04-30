package autenticacao;

import conexaoBD.conexaoBD;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class autenticacaoSenha {
	
	private static autenticacaoSenha authSenha = null;
	private Random ran = null;

	private autenticacaoSenha() {

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
				- Buscar SALT no banco de dados, juntar com as possiveis senha obtidas e gerar hash.
				- Comparar hash gerado com o hash guardado no banco
				- Testar os hashs de todas as possiveis combinações entre os pares de digitos.
				- Bloquear usuário caso haja mais de 3 tentativas e voltar para a etapa anterior.
				- Passar para proxima etapa caso senha esteja correta.
		*/

		System.out.println("Iniciando autenticacao da senha pessoal. Usuário: "+login_name);

		int tentativas = 0;

		while (tentativas < 3){

			/* Obtém senha através do teclado virtual numérico */
			List<int[]> senha = geraTecladoVirtual();

			System.out.println("Senha digitada:");
			for(int i=0; i<senha.size(); i++){
				System.out.print("[ ");
				System.out.print(senha.get(i)[0]);
				System.out.print(" ou ");
				System.out.print(senha.get(i)[1]);
				System.out.print("] ,  ");
			}
			System.out.println();

			

			if (verificaSenha(senha, dadosUsuario)){
				/* PASSA PARA PROXIMA ETAPA */
				tentativas = 0;
			}
			else
				tentativas += 1;

		}

		/* Passou de 3 tentativas -> Bloqueia a conta */

		String query = "UPDATE TABLE USUARIOS SET BLOQUEADO=1 WHERE LOGIN_NAME='"+login_name+"';";

		try {
			Connection conn = conexaoBD.getInstance().getConnection();
			Statement stmt = conn.createStatement();
			stmt.executeQuery(query);

			if (stmt != null)
        		stmt.close();
		}
		catch (SQLException e) {
			System.err.println(e);
			System.out.println("Erro ao atualizar usuário no banco de dados.");
			System.exit(1);
		}
		
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

		ran = new Random();
		Scanner scanner = new Scanner(System.in);

		/* Gera lista aletoria de pares para o teclado virtual */
		int [][] digitosTeclado = geraListaAleatoria();

		/* Array que guarda a senha (em pares) */
		List<int[]> senha = new ArrayList<>();

		/* Começa a capturar os "cliques" em cada botao do teclado. Nessa simulação, captura do teclado o numero equivalente a cada botão */
		int numBotaoClicado;
		int j = 0;
		while(j<9){

			/* Imprime o teclado atual */
			System.out.println("Digite o número de cada botão (que seria) pressionado no teclado (até 9 digitos). Digite -1 para terminar (ENTER).");
			System.out.println("Teclado:");
			for(int i=0; i<5; i++){
				System.out.print("Botão "+(i+1)+": [ ");
				System.out.print(digitosTeclado[i][0]);
				System.out.print(" ou ");
				System.out.print(digitosTeclado[i][1]);
				System.out.print("] ,    ");
			}
			System.out.println();

			/* Captura digito referente ao numero do botao*/
			numBotaoClicado = scanner.nextInt();

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


	private boolean verificaSenha(List<int[]> senha, ResultSet dadosUsuario){

		String senhaCorrente = "";
		int[] indices = new int[5];
		MessageDigest md = null;

		try{
			md = MessageDigest.getInstance("SHA1");
		}
		catch (NoSuchAlgorithmException e){
			System.err.println(e);
			System.exit(1);
		}

		/* Testa todas as possiveis combinações de pares */
		for(indices[0]=0; indices[0]<2; indices[0]++)
			for(indices[1]=0; indices[1]<2; indices[1]++)
				for(indices[2]=0; indices[2]<2; indices[2]++)
					for(indices[3]=0; indices[3]<2; indices[3]++)
						for(indices[4]=0; indices[4]<2; indices[4]++){
							/* Para cada possível combinação dos pares: */
							/* Monta string com senha corrente */							
							for (int i=0; i<senha.size(); i++)
								senhaCorrente += senha.get(i)[indices[i]];

							try{
								String senhaTemperada = senhaCorrente + dadosUsuario.getString("SALT");
								md.update(senhaTemperada.getBytes());
								byte [] senhaBytes = md.digest();
								StringBuilder sb = new StringBuilder();

								for(byte b:senhaBytes){
					        		sb.append(String.format("%02X", b));	
					        	}
					        	String valorCalculado = sb.toString();

					        	/* Verifica se valorCalculado é igual a valorArmazenado da senha */
					        	if (valorCalculado.equals(dadosUsuario.getString("SENHA")))
					        		return true;

								md.reset();
							}
							catch (SQLException e) {
								System.err.println(e);
								System.out.println("Erro ao obter dados do usuário.");
								System.exit(1);
							}
							
						}


		/* Não achou nenhuma combinação de digitos válida */
		return false;
			
	}



}