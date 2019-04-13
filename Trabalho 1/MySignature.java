import java.security.*;
import javax.crypto.*;

public class MySignature {

	private PrivateKey privateKey;
	private PublicKey publicKey;
	private String digestString;
	private byte[] digest;
	private Cipher cipher;
	public String digestAlg;
	public String cipherAlg;


	public static void main(String[] args) throws Exception{

		/* Recebe texto para assinatura da linha de comando: */
	    if (args.length !=1) {
	      System.err.println("Modo de uso: java MySignature <texto a ser assinado>");
	      System.exit(1);
	    }

	    /* Obtem texto plano e inicializa obtejo MySignature com algoritmos especificados */
	    byte[] text = args[0].getBytes("UTF8");
	    MySignature mySignature = MySignature.getInstance("SHA-256WithRSA") ;
	    byte [] signature;


	    /* Gera chaves assimetricas usando o objeto KeyPairGenerator e o algoritmo especificado */
	    System.out.println("\nIniciando geracao das chaves assimetricas...");
	    KeyPairGenerator keyGen = KeyPairGenerator.getInstance(mySignature.cipherAlg);
	    keyGen.initialize(1024);
	    KeyPair key = keyGen.generateKeyPair();
	    System.out.println("Chaves geradas!");


	    /* Inicializa assinatura no objeto mySignature, passando a chave privada e o texto que deverá ser assinado */
	    System.out.println("\nGuardando chave privada para assinatura...");
	    mySignature.initSign(key.getPrivate());
	    System.out.println("\nGerando o digest do texto que sera assinado...");
	    mySignature.update(text);


	    /* Gera a assinatura digital */
	    System.out.println("\nGerando a assinatura apartir do digest...");
	    signature = mySignature.sign();


	    /* Prepara a verificação usando a chave pública gerada e o texto original que será usado para comparação */
	    System.out.println("\nIniciando verificacao da assinatura...");
	    System.out.println("\nGuardando chave pública para verificacao...");
	    mySignature.initVerify(key.getPublic());
	    mySignature.update(text);

	    /* Verifica assinatura */
	    if(mySignature.verify(signature)){
	    	System.out.println( "\n\nAssinatura válida!\n" );
	    }
	    else{
	    	System.out.println( "\n\nAssinatura inválida!\n" );
	    }

	}

	private MySignature(String digestAlgorithm, String cipherAlgorithm){
		this.digestAlg = digestAlgorithm;
		this.cipherAlg = cipherAlgorithm;

		try{
			/* Obtem instancia do Cipher, que irá criptografar o digest usando a chave privada */
	    	this.cipher = Cipher.getInstance(this.cipherAlg+"/ECB/PKCS1Padding");
	    }
	    catch (NoSuchAlgorithmException e) {
			System.out.println("Nao e possivel utilizar o algoritmo especificado.");
			System.exit(1);
		}
		catch(NoSuchPaddingException e){
			System.out.println("Nao e possivel utilizar o padding especificado.");
			System.exit(1);
		}

	}

	public static MySignature getInstance(String algorithms){

		String[] algs = algorithms.split("With");
		return new MySignature(algs[0], algs[1]);

	}


	private void initSign(PrivateKey privKey){
		this.privateKey = privKey;
	    
	}


	private void update(byte[] plainText){

		MessageDigest messageDigest;

		try{
			/* Obtém instancia do objeto MessageDigest, que irá gerar o digest do texto plano */
			messageDigest = MessageDigest.getInstance(this.digestAlg);
			System.out.println("Provedor do Digest:");
			System.out.println( "\n" + messageDigest.getProvider().getInfo() );

			/* Calcula o digest do texto */
			messageDigest.update(plainText);
	     	this.digest = messageDigest.digest();

	     	/* Imprime o digest gerado */
	     	StringBuffer buf = new StringBuffer();
		    for(int i = 0; i < digest.length; i++) {
		       String hex = Integer.toHexString(0x0100 + (digest[i] & 0x00FF)).substring(1);
		       buf.append((hex.length() < 2 ? "0" : "") + hex);
		    }

		    System.out.println("\nDigest "+this.digestAlg+":");
		    digestString = buf.toString();
		    System.out.println( digestString );
		} 
		catch (NoSuchAlgorithmException e) {
			System.out.println("Nao e possivel utilizar o algoritmo especificado.");
			System.exit(1);
		}
		
	}

	private byte[] sign(){

		try{
			/* Prepara o objeto Cipher para criptografar o digest e gerar a assinatura */
			System.out.println("\nIniciando a criptografia do digest");
			this.cipher.init(Cipher.ENCRYPT_MODE, this.privateKey);
			byte[] signature = this.cipher.doFinal(this.digest);

			System.out.println("Criptografia do digest finalizada. Assinatura digital gerada!");

			/* Imprime a assinatura digital em Hex */
			StringBuffer buf = new StringBuffer();
		    for(int i = 0; i < signature.length; i++) {
		       String hex = Integer.toHexString(0x0100 + (signature[i] & 0x00FF)).substring(1);
		       buf.append((hex.length() < 2 ? "0" : "") + hex);
		    }

		    System.out.println("\nAssinatura digital:");
		    System.out.println( buf.toString() );

		    return signature;

		} 
		catch (InvalidKeyException e) {
			System.out.println("A chave utilizada e invalida.");
			System.exit(1);
		}
		catch (IllegalBlockSizeException e) {
			System.out.println("Tamanho de bloco invalido.");
			System.exit(1);
		}
		catch (Exception e){
			System.out.println("Ocorreu um erro desconhecido.");
			System.exit(1);
		}

		return null;

	}

	private void initVerify(PublicKey pubKey){
		this.publicKey = pubKey;
	}

	private boolean verify(byte[] signature){
		
		try{
			/* decriptar o digest usando o objeto Cipher e a chave publica */
			System.out.println("\nIniciando a decriptacao do digest");
			this.cipher.init(Cipher.DECRYPT_MODE, this.publicKey);
			byte[] newDigest = this.cipher.doFinal(signature);

			System.out.println("Decriptacao da assinatura finalizada. Novo digest gerado!");

			/* Imprime o novo digest em Hex */
			StringBuffer buf = new StringBuffer();
		    for(int i = 0; i < newDigest.length; i++) {
		       String hex = Integer.toHexString(0x0100 + (newDigest[i] & 0x00FF)).substring(1);
		       buf.append((hex.length() < 2 ? "0" : "") + hex);
		    }

		    System.out.println("\nNovo digest:");
		    System.out.println( buf.toString() );

		    /* Compara o novo digest gerado pela assinatura com o original */
		    if(digestString.equals(buf.toString()))
		    	return true;
		    else
		    	return false;

		} 
		catch (InvalidKeyException e) {
			System.out.println("A chave utilizada e invalida.");
			System.exit(1);
		}
		catch (IllegalBlockSizeException e) {
			System.out.println("Tamanho de bloco invalido.");
			System.exit(1);
		}
		catch (Exception e){
			System.out.println("Ocorreu um erro desconhecido.");
			System.exit(1);
		}

		return false;

	}

}
