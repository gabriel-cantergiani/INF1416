import java.security.*;
import javax.crypto.*;
import java.util.Scanner;
import java.io.*;

public class DigestCalculator {

	public static void main(String[] args) throws IOException{

		/* Verifica os argumentos recebimos pela linha de comandod */
	    if (args.length < 3) {
	      System.err.println("Sao necessarios pelo menos 3 argumentos! Modo de uso:");
	      System.err.println("java DigestCalculator <Tipo_Digest> <Caminho_ArqListDigest> <Caminho_Arq1> [...<Caminho_ArqN>]");
	      System.exit(1);
	    }

	    /* Obtém os argumentos */
	    String tipo_digest = args[0];
	    String caminho_listaDigest = args[1];
	    String [] caminho_arquivos = new String[args.length-2];

	    for(int i=0; i<args.length-2; i++)
	    	caminho_arquivos[i] = args[i+2];

	    System.out.println(tipo_digest);
	    System.out.println(caminho_listaDigest);

	    //MessageDigest md = MessageDigest.getInstance(tipo_digest);

		/* Percorrer os arquivos (da linha de comando) */
		/* Para cada arquivo de i=0 ate N: */
		for(int i=0; i<caminho_arquivos.length; i++){
			
			/* Abre o arquivo pelo caminho */
			Scanner input = new Scanner(new File(caminho_arquivos[i]));
			
			while(input.hasNextLine()) {
				System.out.println(input.next());
				
				//String line = input.NextLine();
				//md.update(line)
			}	

			/* Calcular Digest do conteudo do arquivo */
    			/* FALTA DETALHAR - > LEITURA EM BLOCOS !!!! */
    			/* Guardar em variavel na classe dos arquivos */

		}
		

	    /* Verificar colisao com outros arquivos passados na linha de comando */

	    /* Para cada arquivo de i=0 ate N: */
				/* Para cada arquivo de j=i ate N: */

							/* Comparar digest do arquivo i com digest do arquivo j */

							/* Se digests forem iguais :*/
									/* Marca o status dos dois arquivos como COLLISION */




		/* Verifica se arquivo está na lista de Digests */

	    /* Para cada 1 dos N arquivos: */
   
	    	/* Percorrer lista de Digests verificando o nome do arquivo e todos os digests presentes */
	    	/* variaveis: mesmo_arquivo=false, FILE_FOUND=false*/

	    	/* Para cada linha percorrida: */

			    	/* Se arquivo buscado for o mesmo da linha atual: */

			    			/* Setar variavel "mesmo_arquivo" como true */
			    			/* Setar variavel FILE_FOUND=true */

			    	/* Se o arquivo da linha atual for outro: */

			    			/* Setar variavel "mesmo_arquivo" como false */


	    			/* Verificar se nesta linha existe o mesmo tipo de digest pro arquivo em questão (MD5 ou SHA-1) */

	    			/* Se existir: */
			
							/* Comparar digest calculado com o digest guardado nesta linha */

							/* Se o digest for igual e a variavel "mesmo_arquivo" é true: */

								/* Este é o arquivo buscado  -> Colocar status OK */

							/* Senão: */

								/* Se a variavel "mesmo_arquivo" é true: */

										/* Digest é errado -> Colocar status NOT OK */

								/* Senão: */

										/* Outro arquivo tem o mesmo digest -> Colocar status COLLISION */


					/* Se não existir: */

							/* Pula para a proxima linha*/



			/* DEPOIS DE PERCORRER TODAS AS LINHAS... */


			/* Se o status for igual a DEFAULT ou NULL */
	
					/* Coloca status como NOT FOUND */

					/* Se a variavel FILE_FOUND=false */

						/* Arquivo não está na lista */
						/* Seta variavel interna do arquivo FILE_FOUND=false */

					/* Se a variavel FILE_FOUND=true */

						/* Arquivo está na lista mas não possui o hash procurado */
						/* Seta variavel interna do arquivo FILE_FOUND=true */
					
					/* Para simplificar os dois ifs acima basta fazer: Arquivo.FILE_FOUND = FILE_FOUND */



		/* DEPOIS DE CONFIGURAR TODOS OS STATUS DOS ARQUIVOS */

		/* Percorrer novamente os arquivos para escreve-los na lista de Digest e printa-los na tela */

		/* Para cada um dos N arquivos: */

			/* Se o status do arquivo for diferente de COLLISION: */

					/* Se o status do arquivo for NOT FOUND: */

							/* Se arquivo.FILE_FOUND=false */

									/* Vai para a ultima linha do arquivo e escreve no formato: */

									/*  <Nome_arquivo> <Tipo_Digest> <Digest_Hex> */


							/* Senão */

									/* Percorre lista de Digest procurando a linha do arquivo em questão */

									/* Para cada linha do da lista de digests: */

											/* Se a primeira palavra for igual ao nome do arquivo: */

												/* Vai para o final da linha e escreve no formato: */

												/* <Tipo_Digest> <Digest_Hex> */


					/* Senão (status é OK ou NOT OK) - */

		
			/* Imprime na tela no seguinte formato: */

			/* <Nome_Arquivo> <Tipo_Digest> <Digest_Hex_Arquivo> <STATUS> */


		/* FIM DO ALGORITMO */

	}
}

/*
public class Arquivo {

	public String nome;
	public byte[] digest_bytes;
	public String digest_hex;
	public String status;
	public boolean FILE_FOUND;

	public Arquivo(String path){

		this.status = null;
		this.FILE_FOUND = false;

	}


}
*/