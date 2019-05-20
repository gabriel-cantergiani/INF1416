package autenticacao;

import java.security.MessageDigest;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import banco.*;
import Interface.MenuFrame;

public class autenticacaoSenha {
	
	private static autenticacaoSenha authSenha = null;
	Connection conn;
	MenuFrame frame;
	JPanel painel;
	int tentativas;

	private autenticacaoSenha() {
		conn = conexaoBD.getInstance().getConnection();
		frame = MenuFrame.getInstance();
	}

	/* SINGLETON */
	public static autenticacaoSenha getInstance() {
		if (authSenha == null)
			authSenha = new autenticacaoSenha();
		return authSenha;
	}

	protected void iniciarAutenticacaoSenha(Usuario usuario) {
		
		Registro registro = new Registro();
		registro.login_name = usuario.login_name;
		registro.insereRegistro(3001, "");

		/* FALTA:
				- Criar interface com teclado virtual para receber (pares de) digitos da senha.
				- Detectar numero de botao invalido (ex: caracteres..)
		*/
		System.out.println("");
		System.out.println("#### AUTENTICACAO POR SENHA - 2a ETAPA ####");
		System.out.println("");
		System.out.println("Iniciando autenticacao da senha pessoal. Usuario: "+usuario.login_name);


		painel = new JPanel();
		painel.setLayout(null);
		tentativas = 0;
		int larguraFrame = frame.getWidth();
		int alturaFrame = frame.getHeight();

		/* Array que guarda a senha (em pares) */
		List<int[]> senha = new ArrayList<>();
		
		JLabel labelLogin = new JLabel("Senha de 6 a 8 digitos:", SwingConstants.CENTER);
		labelLogin.setFont(new Font("Verdana",1,larguraFrame/40));
		labelLogin.setBounds(larguraFrame/2 - 165, alturaFrame/10, 330, 80);
		labelLogin.setBounds((10*larguraFrame - 8*larguraFrame)/20, alturaFrame/10, 8*larguraFrame/10, 80);
		painel.add(labelLogin);

		/* Lista de botoes do teclado */
		JButton [] botoes = new JButton[5];
		
		
		ActionListener cliqueBotaoDigito = new ActionListener() {

			public void actionPerformed(ActionEvent e){

				if (senha.size() >= 8){
					JOptionPane.showMessageDialog(frame, "A senha deve conter no maximo 8 digitos!");
					return;
				}

				JButton botao = (JButton) e.getSource();
				int [] par = obtemParSenha( botao.getText() );
				senha.add(par);

				atualizaBotoes(botoes);
			}

		};

		ActionListener cliqueBotaoOK = new ActionListener() {

			public void actionPerformed(ActionEvent e){

				if (senha.size() < 6){
					JOptionPane.showMessageDialog(frame, "A senha deve conter no minimo 6 digitos!");
					return;
				}

				if (verificaSenha(senha, usuario.senhaHash, usuario.salt)){
					/* PASSA PARA PROXIMA ETAPA */
					tentativas = 0;
					registro.login_name = usuario.login_name;
					registro.insereRegistro(3003, "");
					
					// Remove painel atual
					frame.remove(painel);
					frame.revalidate();
					frame.repaint();
					
					registro.login_name = usuario.login_name;
					registro.insereRegistro(3002, "");
					
					autenticacaoChavePrivada.getInstance().iniciarAutenticacaoChavePrivada(usuario);
					
					return;
				}
				else{
					tentativas += 1;
					
					if(tentativas == 1) {
						registro.login_name = usuario.login_name;
						registro.insereRegistro(3004, "");
					}
					
					else if(tentativas == 2) {
						registro.login_name = usuario.login_name;
						registro.insereRegistro(3005, "");
					}

					else if(tentativas == 3){
						
						registro.login_name = usuario.login_name;
						registro.insereRegistro(3006, "");
						
						usuario.bloqueiaUsuario();
						JOptionPane.showMessageDialog(frame, "Numero de tentativas excedido! Usuario bloqueado por 2 minutos.");
						// Remove painel atual
						frame.remove(painel);
						frame.revalidate();
						frame.repaint();
						
						registro.login_name = usuario.login_name;
						registro.insereRegistro(3007, "");
						
						identificacaoUsuario.getInstance().iniciarIdentificacao();
						return;
					}

					senha.clear();
					JOptionPane.showMessageDialog(frame, "Senha incorreta! Voce tem mais "+(3-tentativas)+" tentativa(s).");
					atualizaBotoes(botoes);
				}

			}

		};

		for(int i=0; i<5; i++){
			botoes[i] = new JButton("Botao");
			botoes[i].setFont(new Font("Verdana",1,15));
			botoes[i].setBounds(larguraFrame/10 + i*17*(larguraFrame/100) , labelLogin.getY()+100, 12*larguraFrame/100, 12*larguraFrame/100);
			botoes[i].addActionListener(cliqueBotaoDigito);
			painel.add(botoes[i]);
		}

		atualizaBotoes(botoes);

		JButton botaoOK = new JButton("OK");
		botaoOK.setFont(new Font("Verdana",1,20));
		botaoOK.setBounds((100*larguraFrame - 12*larguraFrame)/200, labelLogin.getY()+300, 12*larguraFrame/100, 12*larguraFrame/100);
		botaoOK.addActionListener(cliqueBotaoOK);
		painel.add(botaoOK);


		frame.getContentPane().add(painel);
		frame.revalidate();
		frame.repaint();

	}

	private int[][] geraListaAleatoria(){

		List<Integer> digitos = new ArrayList<Integer>();
		int [][] lst = new int[5][2];
		Random ran = new Random();
		
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

	private void atualizaBotoes(JButton [] botoes){

		/* Gera lista aletoria de pares para o teclado virtual */
		int [][] digitosTeclado = geraListaAleatoria();

		for(int i=0; i<5; i++)
			botoes[i].setText(digitosTeclado[i][0]+" ou "+digitosTeclado[i][1]);

		painel.repaint();

	}

	private int[] obtemParSenha(String par){

		int [] parInt = new int[2];

		parInt[0] = Character.getNumericValue(par.charAt(0));
		parInt[1] = Character.getNumericValue(par.charAt(5));

		return parInt;
	}

	private boolean verificaSenha(List<int[]> senhaTestada, String senhaUsuario, String saltUsuario){

		String senhaCorrente = "";
		int[] indices = new int[9];

		/* Testa todas as possiveis combinacoes de pares */
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
											String valorCalculado = geraHashDaSenha(senhaCorrente, saltUsuario);
											System.out.println(senhaCorrente+"   :  "+valorCalculado);
											// Verifica se valorCalculado eh igual a valorArmazenado da senha 
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

		/* Nao achou nenhuma combinacao de digitos valida */
		return false;
			
	}


	public static String geraHashDaSenha(String senhaCorrente, String saltUsuario){

		// junta senha com sal, e calcula hash
		String senhaTemperada = senhaCorrente + saltUsuario;
		MessageDigest md = null;
		try{
			md = MessageDigest.getInstance("SHA1");
		}
		catch (Exception e){
			System.err.println(e);
			System.exit(1);
		}
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

	public static String geraSaltAleatorio(){

		String salt = "";
		String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random ran = new Random();
		
		for (int i=0; i<10; i++)
			salt += caracteres.charAt(ran.nextInt(caracteres.length()));

		return salt;
	}


}
