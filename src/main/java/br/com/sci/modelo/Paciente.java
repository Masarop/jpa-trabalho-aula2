package br.com.sci.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Paciente extends BaseEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	
//----------------------------Atributos-----------------------------
		
			
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id_paciente")
	private Long id;
	
	
	@Column(name = "desc_paciente", length=50)
	private String nome;
	
	@Column(name = "doc_paciente", length=50)
	private String documento;
	
	@OneToMany(mappedBy="paciente",fetch=FetchType.LAZY)
	private List<Senha> senhas;
	
	
	
	
	
//----------------------------------Get & Set / constructor--------------
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




	public String getDocumento() {
		return documento;
	}




	public void setDocumento(String documento) {
		this.documento = documento;
	}




	public List<Senha> getSenhas() {
		if(senhas==null){
			senhas=new ArrayList<>();
		}
		return senhas;
	}








	public void setId(Long id) {
		this.id = id;
	}




	public Paciente(Long id, String nome) {
		super();
		this.id = id;
		this.nome = nome;
	}




	public Paciente() {
		super();
	}

}
