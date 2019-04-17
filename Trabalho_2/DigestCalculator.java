import java.security.*;
import javax.crypto.*;
import java.util.Scanner;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;


public class DigestCalculator {

	public static void main(String[] args) throws IOException{

		/* Verifica os argumentos recebimos pela linha de comando */
	    if (args.length < 3) {
	      System.err.println("Sao necessarios pelo menos 3 argumentos! Modo de uso:");
	      System.err.println("java DigestCalculator <Tipo_Digest> <Caminho_ArqListDigest> <Caminho_Arq1> [...<Caminho_ArqN>]");
	      System.exit(1);
	    }

	    /* Obtém os argumentos e declara/inicializa variaveis */
	    String tipo_digest = args[0];
	    List<Arquivo> arquivos = new ArrayList<Arquivo>();
	    File lista_digest = new File(args[1]);
	    Scanner lista;

	    byte[] bytes = new byte[8092];
	    MessageDigest md = null;

	    /* Inicializa lista de arquivos */
	    for(int i=0; i<args.length-2; i++)
	    	arquivos.add(new Arquivo(args[i+2]));

	    /* Inicializa MessageDigest */
	    try{
	    	md = MessageDigest.getInstance(tipo_digest);   
	    }
	    catch (NoSuchAlgorithmException e) {
			System.err.println("Nao e possivel utilizar o algoritmo especificado.");
			System.err.println("Tipos de digest suportados: 'MD5' ou 'SHA1'");
			System.err.println("Modo de uso: java DigestCalculator <Tipo_Digest> <Caminho_ArqListDigest> <Caminho_Arq1> [...<Caminho_ArqN>]");
			System.exit(1);
		}


	    /*--------------------- Percorrendo arquivos para calcular Digest e verificar colisao com outros arquivos passados na linha de comando -----------*/

		for(int i=0; i<arquivos.size(); i++){


			/* Abre o arquivo pelo caminho */
			InputStream in = new FileInputStream(arquivos.get(i).path);

   			/* Calcular Digest do conteudo do arquivo - LENDO DE 8092 EM 8092 BYTES */
    		for (int n; (n = in.read(bytes)) != -1;) 
      			md.update(bytes, 0, n);

   			/* Guardar em variavel na classe dos arquivos */
			arquivos.get(i).digest_bytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for(byte b:arquivos.get(i).digest_bytes){
        		sb.append(String.format("%02X", b));	
        	}
        	arquivos.get(i).digest_hex = sb.toString();

        	// System.out.println(arquivos.get(i).digest_hex);

			md.reset();
			in.close();


			/* Verificando se já existe arquivo igual ou arquivo com o mesmo digest */
			for(int j=0; j<i; j++){

				/* verifica se existe já existe um arquivo igual (mesmo path) passado na linha de comando. Se existir, remove esta segunda instância */
				if (arquivos.get(i).path.equals(arquivos.get(j).path)) {
					arquivos.remove(i);
					i--;
					break;
				}

				/* Comparando digest do arquivo i com digest do arquivo j */
				if((arquivos.get(i).digest_hex).equals(arquivos.get(j).digest_hex)){
					arquivos.get(i).status = "COLLISION";
					arquivos.get(j).status = "COLLISION";
					/*  Digests iguais --> COLLISION */
					//COLISION = Status do arquivo cujo digest calculado colide com o digest de outro arquivo de nome diferente encontrado no arquivo ArqListaDigest ou com o digest de um dos arquivos fornecidos na linha de comando.
				}				
			}

		}
	
		/*------------------------------------------ Percorrendo lista de Digests -------------------------------------------------*/


		/* Procurando arquivo por arquivo... */
	    for(int i=0; i<arquivos.size();i++){

	    	if (arquivos.get(i).status.equals("COLLISION"))
	    		continue;
	    		/* Se arquivo já estiver marcado com colisão, não é preciso buscá-lo na lista pois seu status já está definido */


	    	//abrindo arquivo de lista de digests para leitura
	    	lista = new Scanner(lista_digest);

	    	/* Percorrendo linha por linha do arquivo... */
	    	while(lista.hasNextLine()){

	    		boolean linha_do_arquivo_buscado = false;

			   	/* Se arquivo buscado for o mesmo da linha atual: */
			   	if(lista.findInLine(arquivos.get(i).nome) != null){

			   		linha_do_arquivo_buscado = true;
			   		arquivos.get(i).arquivo_existe_na_lista = true;

			   	}

	    		/* Verifica se nesta linha existe o mesmo tipo de digest buscado (MD5 ou SHA-1) */
		   		if(lista.findInLine(tipo_digest) != null){
	    		/* Se existir: */

					/* Comparar digest calculado com o digest guardado nesta linha */
					if(lista.findInLine(arquivos.get(i).digest_hex) != null){
						
						if(linha_do_arquivo_buscado)
							arquivos.get(i).status = "OK";
							/* Este é o arquivo buscado  -> Colocar status OK */
							//OK = Status do arquivo cujo digest calculado é igual ao digest fornecido no arquivo ArqListaDigest e não colide com o digest de outro arquivo na linha de comando.
						else
							arquivos.get(i).status = "COLLISION";
							/* Outro arquivo tem o mesmo digest -> COLLISION */
					}			
					else{
	
						if(linha_do_arquivo_buscado)
							arquivos.get(i).status = "NOT OK";
							/* Digest é errado -> Colocar status NOT OK */
							//NOT OK = Status do arquivo cujo digest não é igual ao digest fornecido no arquivo ArqListaDigest e não colide com o digist de outro arquivo na linha de comando.
						
					}
				}

				/* Pula para a proxima linha*/
				if (lista.hasNextLine())
					lista.nextLine();
			}

			/* DEPOIS DE PERCORRER TODAS AS LINHAS... */

			/* Se o arquivo (ou digest) não tiver sido encontrado na lista  -> NOT FOUND */						
			if (arquivos.get(i).status.equals(""))
				arquivos.get(i).status = "NOT FOUND";
			//NOT FOUND = Status do arquivo cujo digest não foi encontrado no arquivo ArqListaDigest e não colide com o digest de outro arquivo na linha de comando

			lista.close();
		} 



		/* Percorrer novamente os arquivos para escreve-los na lista de Digest e printa-los na tela */

		/* Para cada um dos N arquivos: */
		for(int i=0; i<arquivos.size(); i++){

			//abrindo arquivo de lista de digests para leitura
		    lista = new Scanner(lista_digest);

			// Escreve na lista arquivos não encontrados
			if (arquivos.get(i).status.equals("NOT FOUND")) {

				/* Obtem string com o arquivo inteiro para alterá-lo e reescrevê-lo */
				String arquivo_inteiro = new String(Files.readAllBytes( Paths.get(args[1]) ));
  	
  				/* Obtém índices relativos à linha do arquivo e à posição em que será escrito */
				int indice_linha_arquivo = arquivo_inteiro.indexOf(arquivos.get(i).nome);
				int indice_fim_linha = arquivo_inteiro.indexOf("\n", indice_linha_arquivo);
				FileWriter writer = new FileWriter(lista_digest);

				/* Se arquivo não existe na lista */
				if (indice_linha_arquivo == -1){
					/* Escreve no final do arquivo */
					writer.write(arquivo_inteiro+"\n"+arquivos.get(i).nome+" "+tipo_digest+" "+arquivos.get(i).digest_hex);

				}
				else{

					/* Escreve na linha do arquivo e concatena com a parte anterior e posterior */
					String string_antes = arquivo_inteiro.substring(0,indice_linha_arquivo-1);
					String linha_do_arquivo = arquivo_inteiro.substring(indice_linha_arquivo, indice_fim_linha-1);
					String string_depois = arquivo_inteiro.substring(indice_fim_linha);

					linha_do_arquivo += " "+tipo_digest+" "+arquivos.get(i).digest_hex;
					writer.write(string_antes+linha_do_arquivo+string_depois);

				}

				writer.close();
			}
		
			/* Imprime na tela: */

			System.out.println(arquivos.get(i).nome+" "+tipo_digest+" "+arquivos.get(i).digest_hex+" "+arquivos.get(i).status);
			/* <Nome_Arquivo> <Tipo_Digest> <Digest_Hex_Arquivo> <STATUS> */

		}
		/* FIM DO ALGORITMO */

	}
}
