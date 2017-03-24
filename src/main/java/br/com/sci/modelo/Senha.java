package br.com.sci.modelo;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Senha extends BaseEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//-----------------------------Atributos---------------
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id_senha", nullable = false)
	private Long id;
	

	@Column(name = "num_senha", nullable = false, length = 3)
	private int numSenha;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_emissao", nullable = false)
	private Date dataEmissao;
		
	@ManyToOne(cascade = {CascadeType.PERSIST},fetch=FetchType.LAZY)
	@JoinColumn(name="id_servico",referencedColumnName="id_servico",nullable=false,insertable=true,updatable=false)
	private Servico servico;
	
	@ManyToOne(cascade = {CascadeType.PERSIST},fetch=FetchType.LAZY)
	@JoinColumn(name="id_paciente",referencedColumnName="id_paciente",nullable=false,insertable=true,updatable=false)
	private Paciente paciente;
	
//------------------------------Get & Set----------------------
	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	public int getNumSenha() {
		return numSenha;
	}

	public void setNumSenha(int numSenha) {
		this.numSenha = numSenha;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public void setId(Long id) {
		this.id = id;
	}



	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}
	
	
	
//--------------------------construtores-------------------------
	



	public Senha() {
	}
	
	
}
