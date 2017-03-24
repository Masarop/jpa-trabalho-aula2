package br.com.sci.modelo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.sci.modelo.Atendente;
import br.com.sci.util.JPAUtil;

public class TestAtendente {
	
private EntityManager em;
	
	
	@Test
	public void deveSalvarAtendente(){
		Atendente atendente = new Atendente();
		atendente.setNome("Wilson");
		atendente.setLogin("Wilsoooon");
		atendente.setSenha("12345678");
		assertTrue("Não deve ter ID definido", atendente.isTransient());
		
		em.getTransaction().begin();
		em.persist(atendente);
		em.getTransaction().commit();
		
		assertFalse("entidade agora tem id ainda", atendente.isTransient());
	}
	@Test
	public void devePesquisarAtendentes(){
		for(int i=0;i<10;i++){
			deveSalvarAtendente();
		}
		
		TypedQuery<Atendente> query = em.createQuery("SELECT a FROM Atendente a", Atendente.class);
		List<Atendente> atendentes = query.getResultList();
		
		assertFalse("deve ter encontrado um atendente", atendentes.isEmpty());
		assertTrue("deve ter encontrado vários atendentes", atendentes.size()>=10);
	}
	@Test
	public void deveAlterarAtendente(){
		deveSalvarAtendente();
		
		TypedQuery<Atendente> query = em.createQuery("SELECT a FROM Atendente a",Atendente.class).setMaxResults(1);
		Atendente atendente = query.getSingleResult();
		
		assertNotNull("Deve ter encontrado um atendente", atendente);
		
		Integer versao = atendente.getVersion();
		
		em.getTransaction().begin();
		
		atendente.setNome("Wilsooooooooon");
		
		atendente = em.merge(atendente);

		em.getTransaction().commit();
		
		assertNotEquals("deve ter versao incrementada", versao.intValue(), atendente.getVersion().intValue());
	}
	
	@Test
	public void deveExcluirAtendente(){
		deveSalvarAtendente();
		
		TypedQuery<Long> query = em.createQuery("SELECT MAX(a.id) FROM Atendente a",Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		Atendente atendente = em.find(Atendente.class, id);
		em.remove(atendente);
		em.getTransaction().commit();
		Atendente atendenteExcluido = em.find(Atendente.class, id);
		
		assertNull("Não deve encontrar o atendente",atendenteExcluido);
	}

	@AfterClass
	public static void deveLimparBaseTeste(){
		EntityManager entityManager = JPAUtil.ISNTANCE.getEntityManager();
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM Atendente a");
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
