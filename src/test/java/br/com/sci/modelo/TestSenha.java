package br.com.sci.modelo;


import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.sci.modelo.Senha;
import br.com.sci.util.JPAUtil;

public class TestSenha {
	private EntityManager em;
	
	@Test
	public void deveSalvarSenha(int ns){
		Senha senha = new Senha();
		senha.setSigSenha("A");
		senha.setNumSenha((ns++));
		senha.setDataEmissao(new Date());
		senha.setServSenha("Fisioterapia");
		assertTrue("Não deve ter ID definido", senha.isTransient());
		
		em.getTransaction().begin();
		em.persist(senha);
		em.getTransaction().commit();
		
		assertFalse("entidade agora tem id ainda", senha.isTransient());
	}
	@Test
	public void devePesquisarSenhas(){
		for(int i=0;i<10;i++){
			deveSalvarSenha(1);
		}
		
		TypedQuery<Senha> query = em.createQuery("SELECT s FROM Senha s", Senha.class);
		List<Senha> senhas = query.getResultList();
		
		assertFalse("deve ter encontrado um senha", senhas.isEmpty());
		assertTrue("deve ter encontrado vários senhas", senhas.size()>=10);
	}
	@Test
	public void deveAlterarSenha(){
		deveSalvarSenha(1);
		
		TypedQuery<Senha> query = em.createQuery("SELECT s FROM Senha s",Senha.class).setMaxResults(1);
		Senha senha = query.getSingleResult();
		
		assertNotNull("Deve ter encontrado um senha", senha);
		
		Integer versao = senha.getVersion();
		
		em.getTransaction().begin();
		
		senha.setDataEmissao(new Date());;
		
		senha = em.merge(senha);

		em.getTransaction().commit();
		
		assertNotEquals("deve ter versao incrementada", versao.intValue(), senha.getVersion().intValue());
	}
	
	@Test
	public void deveExcluirSenha(){
		deveSalvarSenha(1);
		
		TypedQuery<Long> query = em.createQuery("SELECT MAX(s.id) FROM Senha s",Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		Senha senha = em.find(Senha.class, id);
		em.remove(senha);
		em.getTransaction().commit();
		Senha senhaExcluida = em.find(Senha.class, id);
		
		assertNull("Não deve encontrar o senha",senhaExcluida);
	}
/*
	@AfterClass
	public static void deveLimparBaseTeste(){
		EntityManager entityManager = JPAUtil.ISNTANCE.getEntityManager();
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM Senha s");
		int qtdRegistrosExclidos = query.executeUpdate();
		entityManager.getTransaction().commit();
		
		assertTrue("Certifica que a base foi limpada",qtdRegistrosExclidos>0);
				
	}
	*/
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
