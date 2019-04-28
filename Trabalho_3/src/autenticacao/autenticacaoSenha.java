package autenticacao;

import conexaoBD.conexaoBD;
import java.sql.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class autenticacaoSenha {
	
	private static autenticacaoSenha authSenha = null;
	private Random ran = null;;

	private autenticacaoSenha() {

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
				- Buscar SALT no banco de dados, juntar com as possiveis senha obtidas e gerar hash.
				- Comparar hash gerado com o hash guardado no banco
				- Testar os hashs de todas as possiveis combinações entre os pares de digitos.
				- Bloquear usuário caso haja mais de 3 tentativas e voltar para a etapa anterior.
				- Passar para proxima etapa caso senha esteja correta.
		*/

		System.out.println("Iniciando autenticacao da senha pessoal. Usuário: "+login_name);

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
				break;

			/* Adiciona par de digitos na senha */
			senha.add(digitosTeclado[numBotaoClicado-1]);

			/* Gera nova lista aletoria de pares para o teclado virtual */
			digitosTeclado = geraListaAleatoria();

			/* Contabiliza numero de digitos da senha */
			j+=1;
		}


		System.out.println("Senha digitada:");
		for(int i=0; i<senha.size(); i++){
			System.out.print("[ ");
			System.out.print(senha.get(i)[0]);
			System.out.print(" ou ");
			System.out.print(senha.get(i)[1]);
			System.out.print("] ,  ");
		}
		System.out.println();
		

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

}