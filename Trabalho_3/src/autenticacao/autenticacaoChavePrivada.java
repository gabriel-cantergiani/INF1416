package autenticacao;

import conexaoBD.conexaoBD;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.*;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import java.util.Base64;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class autenticacaoChavePrivada {
	
	private static autenticacaoChavePrivada authChavePrivada = null;
	Connection conn;

	private autenticacaoChavePrivada() {
		conn = conexaoBD.getInstance().getConnection();
	}

	/* SINGLETON */
	public static autenticacaoChavePrivada getInstance() {
		if (authChavePrivada == null)
			authChavePrivada = new autenticacaoChavePrivada();
		return authChavePrivada;
	}


	protected void iniciarAutenticacaoChavePrivada(String login_name) {

		/*FALTA
				- Abrir filechooser para selecionar arquivo .pem com a chave criptografada e codificada em b64
				- Usar chave privada para gerar uma assinatura digital de um array aletorio de 2048 bytes
				- Buscar certificado digital do usuario no banco, obter chave publica do certificado e usar chave publica para verificar assinatura digital
				- Se verificacao for negativa, bloquea usuario e retorna para etapa 1
				- Se for positiva, segue para o sistema
				- ## criar metodo para gerar uma chave privada, publica e certificado digital e guardar tudo no banco

		*/
		System.out.println("");
		System.out.println("#### AUTENTICACAO POR CHAVE PRIVADA - 3a ETAPA ####");
		System.out.println("");

		// JFileChooser fileChooser = new FileChooser();
		// fileChooser.setDialogTitle("Escolha o arquivo contendo a chave privada");
		// fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos PEM (Privacy Enhanced Mail)"), "pem");
		// int ret = fileChooser.showOpenDialog(JFRAME AQUI);

		// Obtem caminho para arquivo da chave privada
		Scanner scanner = new Scanner(System.in);
		System.out.print("Digite o caminho para o arquivo contendo a chave privada: ");
		String caminhoChave = scanner.nextLine();
		byte [] chavePrivadaCifrada = null;

		// Abre arquivo com chave privada
		try{
			Path path = Paths.get(caminhoChave);
			chavePrivadaCifrada = Files.readAllBytes(path);
		}
		catch(IOException e){
			System.err.println(e);
			System.out.println("Erro ao abrir arquivo da chave privada");
			System.exit(1);
		}

		// DECRIPTA

		Key chaveSimetrica;
		String chavePrivadaPEM_B64String = null;
		System.out.print("Digite a frase secreta de decriptação:");
		String fraseSecreta = scanner.nextLine();

		try{
			// Inicializa o gerador PRNG com a frase secreta (semente)
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(fraseSecreta.getBytes());

			// Gera a chave simetrica apartir do gerador inicializado anteriormente
			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
			keyGen.init(56, secureRandom);
			chaveSimetrica = keyGen.generateKey();

			// Decripta chave privada (codificada em B64) usando a chave simetrica gerada
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, chaveSimetrica);
	    	byte[] chavePrivadaPEM_B64 = cipher.doFinal(chavePrivadaCifrada);
	    	chavePrivadaPEM_B64String = new String(chavePrivadaPEM_B64, "UTF8");
	    	System.out.println( chavePrivadaPEM_B64String );

		}
		catch(InvalidKeyException e){
			System.err.println(e);
			System.exit(1);
		}
		catch(Exception e){
			System.err.println(e);
			System.exit(1);
		}
		

		// DECODIFICA
		try {
			chavePrivadaPEM_B64String = chavePrivadaPEM_B64String.replace("-----BEGIN PRIVATE KEY-----\n", "");
	  		chavePrivadaPEM_B64String = chavePrivadaPEM_B64String.replace("-----END PRIVATE KEY-----", "");
			byte[] chavePrivadaPKCS8 = Base64.getMimeDecoder().decode(chavePrivadaPEM_B64String);
	  		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	  		PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(chavePrivadaPKCS8));		
		}
		catch(Exception e) {
			System.err.println(e);
			System.exit(1);
		}
		
		System.out.println("Chave privada obtida com sucesso!");

	}


}