package br.com.sci.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

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
	
	//---------------------------------------------------
	
	@ManyToMany(cascade={CascadeType.PERSIST},fetch=FetchType.LAZY)
	@JoinTable(name="atendentes_servico", joinColumns=@JoinColumn(name="id_atendente"),
							  inverseJoinColumns=@JoinColumn(name="id_servico"))
	private List<Servico> servicos;
	
	
	//---------------------------------------------------
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
	
	public List<Servico> getServicos() {
		if(servicos==null){
			servicos=new ArrayList<>();
		}
		return servicos;
	}


	public Atendente(Long id, String nome) {
		super();
		this.id = id;
		this.nome = nome;
	}

	public Atendente() {
		super();
	}

}
