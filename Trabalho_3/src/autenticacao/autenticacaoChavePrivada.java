package autenticacao;

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
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import banco.*;
import sistema.MenuPrincipal;
import Interface.MenuFrame;

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
	private Connection conn;
	private MenuFrame frame;
	private JPanel painel;
	int tentativas;
	private byte [] chavePrivadaCifrada;
	private ActionListener cliqueBuscaChave = null;
	private ActionListener cliqueDecriptaChave = null;

	private autenticacaoChavePrivada() {
		conn = conexaoBD.getInstance().getConnection();
		frame = MenuFrame.getInstance();
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


	public void iniciarAutenticacaoChavePrivada(Usuario usuario) {

		/*FALTA
				- Abrir filechooser para selecionar arquivo .pem com a chave criptografada e codificada em b64
				- Criar interface
				- Se verificacao for positiva, segue para o sistema

		*/
		System.out.println("");
		System.out.println("#### AUTENTICACAO POR CHAVE PRIVADA - 3a ETAPA ####");
		System.out.println("");

		painel = new JPanel();
		painel.setLayout(null);
		tentativas = 0;
		int larguraFrame = frame.getWidth();
		int alturaFrame = frame.getHeight();
		
		JLabel label = new JLabel();
		label.setText("Caminho do arquivo contendo a chave privada:");
		label.setFont(new Font("Verdana",1,larguraFrame/40));
		label.setBounds(frame.getWidth()/2 - 350, alturaFrame/10, 700, 80);
		painel.add(label);

		JTextArea input = new JTextArea(4,10);
		input.setEditable(true);
		input.setFont(new Font("Verdana",1,larguraFrame/45));
		input.setBounds((10*larguraFrame - 8*larguraFrame)/20, label.getY()+100, 8*larguraFrame/10, 3*alturaFrame/10);
		input.setLineWrap(true);
		painel.add(input);

		JButton botao = new JButton("Buscar");
		botao.setFont(new Font("Verdana",1,larguraFrame/40));
		botao.setBounds((10*larguraFrame - 2*larguraFrame)/20, input.getY()+250, 2*larguraFrame/10, 70);
		painel.add(botao);


		cliqueBuscaChave = new ActionListener() {

			public void actionPerformed(ActionEvent event){

				// OBTEM CHAVE PRIVADA CIFRADA
				try{
					Path path = Paths.get(input.getText());
					chavePrivadaCifrada = Files.readAllBytes(path);
				}
				catch(IOException e){
					System.err.println(e);
					System.out.println("Erro ao abrir arquivo da chave privada");
					
					JOptionPane.showMessageDialog(frame, "Erro ao abrir arquivo! Tente novamente.");
					return;				
				}

				JOptionPane.showMessageDialog(frame, "Arquivo aberto com sucesso!");
				label.setText("Digita a frase secreta de decriptação:");
				label.setBounds(larguraFrame/2 - 225, alturaFrame/10, 550, 80);
				input.setText("");
				input.setBounds((10*larguraFrame - 6*larguraFrame)/20, label.getY()+100, 6*larguraFrame/10, 60);
				botao.setText("Decriptar");
				botao.setBounds((10*larguraFrame - 2*larguraFrame)/20, input.getY()+100, 2*larguraFrame/10, 70);
				botao.removeActionListener(this);
				botao.addActionListener(cliqueDecriptaChave);

			}

		};

		 
		cliqueDecriptaChave = new ActionListener() {

			public void actionPerformed(ActionEvent event){

					String msg;

					// DECRIPTA
					String chavePrivadaPEM_B64String = decriptaChavePrivada(chavePrivadaCifrada, input.getText());
				
					if (chavePrivadaPEM_B64String != null) {
						// DECODIFICA
						PrivateKey privateKey = decodificaChavePrivada(chavePrivadaPEM_B64String);
						// guarda chave privada no usuario para uso futuro
						usuario.chavePrivada = privateKey;
						
						// GERA ASSINATURA DE ARRAY ALEATORIO
						byte [] assinatura = geraAssinatura(privateKey);
						
						// BUSCA CERTIFICADO DIGITAL NO BANCO E OBTEM CHAVE PUBLICA
						PublicKey publicKey = obtemCertificado(usuario.certificado).getPublicKey();
						// guarda chave publica no usuario para uso futuro
						usuario.chavePublica = publicKey;
						
						// VERIFICA ASSINATURA
						if(verificaAssinatura(assinatura, publicKey)){
							JOptionPane.showMessageDialog(frame, "Chave privada autenticada com sucesso! Acesso concedido!");
							tentativas = 0;

							// Remove painel atual
							frame.remove(painel);
							frame.revalidate();
							frame.repaint();

							// Passa para a proxima etapa
							usuario.incrementaAcessosUsuario();
							MenuPrincipal.getInstance().iniciarMenuPrincipal(usuario);
							return;
						}
						else {
							tentativas += 1;
							msg = "Chave privada inválida!";
						}
						
					}
					else {
						tentativas += 1;
						msg = "Frase secreta incorreta!";
					}


					if (tentativas == 3){
						usuario.bloqueiaUsuario();
						JOptionPane.showMessageDialog(frame, msg+" Número de tentativas excedido! Usuário bloqueado por 2 minutos.");
						// Remove painel atual
						frame.remove(painel);
						frame.revalidate();
						frame.repaint();
						identificacaoUsuario.getInstance().iniciarIdentificacao();
						return;
					}

					label.setText("Caminho do arquivo contendo a chave privada:");
					label.setBounds(frame.getWidth()/2 - 350, alturaFrame/10, 700, 80);
					input.setText("");
					input.setBounds((10*larguraFrame - 8*larguraFrame)/20, label.getY()+100, 8*larguraFrame/10, 3*alturaFrame/10);
					botao.setText("Buscar");
					botao.setBounds((10*larguraFrame - 2*larguraFrame)/20, input.getY()+250, 2*larguraFrame/10, 70);
					botao.removeActionListener(this);
					botao.addActionListener(cliqueBuscaChave);
					JOptionPane.showMessageDialog(frame, msg+" Você tem mais "+(3-tentativas)+" tentativa(s).");
			}

		};
		
		
		botao.addActionListener(cliqueBuscaChave);
		
		frame.getContentPane().add(painel);
		frame.revalidate();
		frame.repaint();


	}

	private byte [] obtemChavePrivadaCifrada(){

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
			return null;
		}

		return chavePrivadaCifrada;
	}

	private String decriptaChavePrivada(byte [] chavePrivadaCifrada, String fraseSecreta){

		String chavePrivadaPEM_B64String = null;

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
			return null;
		}
		catch(Exception e){
			System.err.println(e);
			System.out.println("Erro ao decriptar arquivo da chave privada.");
			return null;
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

	public X509Certificate obtemCertificado(byte [] certificadoPEM){

		X509Certificate certificado = null;

		try {
			// correcao no arquivo...
			String certificadoPEM_B64String = new String(certificadoPEM, "UTF8");
			int index = certificadoPEM_B64String.indexOf("-----BEGIN CERTIFICATE-----");
			if (index != -1)
				certificadoPEM_B64String = certificadoPEM_B64String.substring(index);


			// remove headers do arquivo PEM
			certificadoPEM_B64String = certificadoPEM_B64String.replace("-----BEGIN CERTIFICATE-----", "");
			certificadoPEM_B64String = certificadoPEM_B64String.replace("-----END CERTIFICATE-----", "");
			
			// Obtem certificado no formato x509
			byte[] certificadoBytes = Base64.getMimeDecoder().decode(certificadoPEM_B64String);
			CertificateFactory cf = CertificateFactory.getInstance("x509");			
			certificado = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(certificadoBytes));
	  		
		}
		catch(Exception e) {
			System.err.println(e);
			System.exit(1);
		}
		
		return certificado;
		
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