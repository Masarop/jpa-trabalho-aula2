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
import javax.persistence.OneToMany;


@Entity
public class Servico extends BaseEntity<Long> {

	
	
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
//----------------------------Atributos-----------------------------
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id_servico")
	private Long id;
	
	@Column(name="nm_servico", nullable=false)
	private String descricao;
	
	@Column(name="local_atendimento", nullable=true)
	private String local;
	
	@Column(name="sigla_servico", nullable=false)
	private String sigServico;
	
	@OneToMany(mappedBy="servico",fetch=FetchType.LAZY)
	private List<Senha> senhas;
	

	
	//----------------------------------Get & Set--------------
	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getSigServico() {
		return sigServico;
	}

	public void setSigServico(String sigServico) {
		this.sigServico = sigServico;
	}

	public List<Senha> getSenhas() {
		return senhas;
	}

	public void setSenhas(List<Senha> senhas) {
		this.senhas = senhas;
	}


	public Servico(Long id, String descricao) {
		super();
		this.id=id;
		this.descricao = descricao;
		//this.sigServico = sigServico;
	}

	public Servico() {
	}

}
