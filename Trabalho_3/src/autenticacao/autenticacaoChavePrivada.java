package autenticacao;

import sistema.MenuPrincipal;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.*;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import banco.*;

import java.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class autenticacaoChavePrivada {
	
	private static autenticacaoChavePrivada authChavePrivada = null;
	private Scanner scanner;
	private Signature signature;
	private byte[] arrayAleatorio;
	Connection conn;

	private autenticacaoChavePrivada() {
		conn = conexaoBD.getInstance().getConnection();
		try {
			signature = Signature.getInstance("MD5WithRSA");
		}
		catch(Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	/* SINGLETON */
	public static autenticacaoChavePrivada getInstance() {
		if (authChavePrivada == null)
			authChavePrivada = new autenticacaoChavePrivada();
		return authChavePrivada;
	}


	protected void iniciarAutenticacaoChavePrivada(Usuario usuario) {

		/*FALTA
				- Abrir filechooser para selecionar arquivo .pem com a chave criptografada e codificada em b64
				- Criar interface
				- Se verificacao for positiva, segue para o sistema

		*/
		System.out.println("");
		System.out.println("#### AUTENTICACAO POR CHAVE PRIVADA - 3a ETAPA ####");
		System.out.println("");

		int tentativas = 0;

		while (tentativas < 3){

			// OBTEM CHAVE PRIVADA CIFRADA
			byte [] chavePrivadaCifrada = obtemChavePrivadaCifrada();
			
			// DECRIPTA
			String chavePrivadaPEM_B64String = decriptaChavePrivada(chavePrivadaCifrada);
				
			// DECODIFICA
			PrivateKey privateKey = decodificaChavePrivada(chavePrivadaPEM_B64String);
			
			// GERA ASSINATURA DE ARRAY ALEATORIO
			byte [] assinatura = geraAssinatura(privateKey);
			
			// BUSCA CERTIFICADO DIGITAL NO BANCO E OBTEM CHAVE PUBLICA
			PublicKey publicKey = obtemChavePublica(usuario.certificado);
			
			// VERIFICA ASSINATURA
			if(verificaAssinatura(assinatura, publicKey)){
				System.out.println("Chave privada autenticada com sucesso! Acesso concedido!");
				tentativas = 0;
				// Passa para a proxima etapa
				MenuPrincipal.getInstance().iniciarMenuPrincipal(usuario);
				return;

			}
			else{
				System.out.println("Chave privada invalida!");
				tentativas += 1;
			}
		}// fim while

		// Passou de 3 tentativas -> bloqueia usuario
		usuario.bloqueiaUsuario();

	}

	private byte [] obtemChavePrivadaCifrada(){

		// JFileChooser fileChooser = new FileChooser();
		// fileChooser.setDialogTitle("Escolha o arquivo contendo a chave privada");
		// fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos PEM (Privacy Enhanced Mail)"), "pem");
		// int ret = fileChooser.showOpenDialog(JFRAME AQUI);

		// Obtem caminho para arquivo da chave privada
		scanner = new Scanner(System.in);
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

		return chavePrivadaCifrada;
	}

	private String decriptaChavePrivada(byte [] chavePrivadaCifrada){

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
			Key chaveSimetrica = keyGen.generateKey();

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
			System.out.println("Erro ao decriptar arquivo da chave privada.");
			System.exit(1);
		}

		return chavePrivadaPEM_B64String;
	}

	private PrivateKey decodificaChavePrivada(String chavePrivadaPEM_B64String){

		PrivateKey privateKey = null;

		try {
			chavePrivadaPEM_B64String = chavePrivadaPEM_B64String.replace("-----BEGIN PRIVATE KEY-----\n", "");
	  		chavePrivadaPEM_B64String = chavePrivadaPEM_B64String.replace("-----END PRIVATE KEY-----", "");
			byte[] chavePrivadaPKCS8 = Base64.getMimeDecoder().decode(chavePrivadaPEM_B64String);
	  		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	  		privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(chavePrivadaPKCS8));

	  		System.out.println("Chave privada obtida com sucesso!");		
		}
		catch(Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		return privateKey;

	}

	private byte [] geraAssinatura(PrivateKey privateKey){

		arrayAleatorio = new byte[2048];
		SecureRandom secureRandom = new SecureRandom();
		byte[] assinatura = null;

		secureRandom.nextBytes(arrayAleatorio);

		try {
	    	signature.initSign(privateKey);
	    	signature.update(arrayAleatorio);
	    	assinatura = signature.sign();
		}
		catch(Exception e) {
			System.err.println(e);
			System.exit(1);
		}
		
		return assinatura;
	}

	private PublicKey obtemChavePublica(byte [] certificadoPEM){

		PublicKey publicKey = null;

		try {
			// correcao no arquivo...
			String s1 = new String(certificadoPEM, "UTF8");
			int index = s1.indexOf("-----BEGIN CERTIFICATE-----\n");
			String certificadoPEM_B64String = s1.substring(index);

			// remove headers do arquivo PEM
			certificadoPEM_B64String = certificadoPEM_B64String.replace("-----BEGIN CERTIFICATE-----\n", "");
			certificadoPEM_B64String = certificadoPEM_B64String.replace("-----END CERTIFICATE-----", "");

			// Obtem certificado no formato x509
			byte[] certificadoBytes = Base64.getMimeDecoder().decode(certificadoPEM_B64String);
	  		CertificateFactory cf = CertificateFactory.getInstance("x509");
	  		X509Certificate certificado = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificadoBytes));

	  		publicKey = certificado.getPublicKey();
	  		
	  		System.out.println("Chave publica do usuario obtida com sucesso!");
	  		
		}
		catch(Exception e) {
			System.err.println(e);
			System.exit(1);
		}
		
		return publicKey;
		
	}

	private boolean verificaAssinatura(byte [] assinatura, PublicKey publicKey){

		try{
			
			signature.initVerify(publicKey);
			signature.update(arrayAleatorio);
			
			if (signature.verify(assinatura))
				return true;
			else
				return false;
    	}
    	catch (Exception e) {
    		System.err.println(e);
    		System.out.println( "Erro ao verificar assinatura" );
			System.exit(1);
    	}
		
		return false;

	}


}