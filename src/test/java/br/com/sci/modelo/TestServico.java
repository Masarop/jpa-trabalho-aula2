package br.com.sci.modelo;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.sci.util.JPAUtil;

public class TestServico {
	
	private EntityManager em;
	private static final String SERVICO_PADRAO = "Fisioterapia";
	private static final String SIG_PADRAO = "A";
	@Test
	public void deveSalvarServico(){
		Servico servico = new Servico();
				servico.setDescricao(SERVICO_PADRAO);
				servico.setSigServico(SIG_PADRAO);
		
				assertTrue("não deve ter ID definido",servico.isTransient());
				
				em.getTransaction().begin();
				em.persist(servico);
				em.getTransaction().commit();
				
			assertFalse("deve ter ID definido", servico.isTransient());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarSigsenha() {
		deveSalvarServico();

		Query query = em
				.createQuery("SELECT s.sigServico FROM Servico s WHERE s.descricao LIKE :descricao");
		query.setParameter("descricao", "%Fisioterapia%");
		List<String> lisCPF = query.getResultList();

		assertFalse("verifica se há registros na lista", lisCPF.isEmpty());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarServicoComIdDescricao() {
		deveSalvarServico();

		Query query = em.createQuery("SELECT new Servico(s.id, s.descricao) FROM Servico s WHERE s.sigServico = :sigServico");
		query.setParameter("sigServico", SIG_PADRAO);

		
		List<Servico> servicos = (List<Servico>) query.getResultList();

		assertFalse("verifica se há registros na lista", servicos.isEmpty());

		servicos.forEach(servico -> assertNull("Não dever ter Sigla", servico.getSigServico()));
		/*
		 * for(Cliente cliente: clientes){
		 * assertNull("Verifica que o cpf deve estar null", cliente.getCpf());
		 * cliente.setCpf(CPF_PADRAO); }
		 */
	}
	
	
	
	
	
	@AfterClass
	public static void deveLimparBaseTeste(){
		EntityManager entityManager = JPAUtil.ISNTANCE.getEntityManager();
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM Servico s");
		int qtdRegistrosExclidos = query.executeUpdate();
		entityManager.getTransaction().commit();
		
		assertTrue("Certifica que a base foi limpada",qtdRegistrosExclidos>0);
				
	}
	
	@Before
	public void instanciarEntityManager(){
		em=JPAUtil.ISNTANCE.getEntityManager();
	}
	
	@After
	public void fecharEntityManagaer(){
		if(em.isOpen()){
			em.close();
		}
	}
	
	

}
