package br.com.sci.modelo;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
	@Column(name = "singla_senha", nullable = false, length = 3)
	private String sigSenha;


	@Column(name = "num_senha", nullable = false, length = 3)
	private int numSenha;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_emissao", nullable = false)
	private Date dataEmissao;
	
	@Basic(fetch = FetchType.LAZY, optional = false)
	@Column(name="servico_senha", length = 50)
	private String servSenha;
		
	
//------------------------------Get & Set----------------------
	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

	
	
}
