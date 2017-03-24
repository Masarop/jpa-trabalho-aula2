package br.com.sci.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Atendente extends BaseEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//-----------------------------Atributos---------------
		
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_atendente", nullable = false)
	private Long id;
	
	@Column(name="nm_atendente",nullable=false,length=50)
	private String nome;
	
	@Column(name="user",nullable=false,length=50)
	private String login;
	
	@Column(name="password",nullable=false,length=50)
	private String senha;
	
	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Atendente() {
		super();
	}

}
