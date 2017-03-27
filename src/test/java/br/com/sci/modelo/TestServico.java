package br.com.sci.modelo;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.com.sci.util.JPAUtil;

public class TestServico {
	
	private EntityManager em;
	private static final String SERVICO_PADRAO = "Fisioterapia";
	private static final String SIG_PADRAO = "AAA";
	
	
	
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
	
	@Test
	public void deveAlterarServico(){
		deveSalvarServico();
		
		TypedQuery<Servico> query = em.createQuery("SELECT s FROM Servico s",Servico.class).setMaxResults(1);
		Servico servico = query.getSingleResult();
		
		assertNotNull("Deve ter encontrado um servico", servico);
		
		Integer versao = servico.getVersion();
		
		em.getTransaction().begin();
		
		servico.setDescricao("Avaliação fisioterapia");
		
		servico = em.merge(servico);

		em.getTransaction().commit();
		
		assertNotEquals("deve ter versao incrementada", versao.intValue(), servico.getVersion().intValue());
	}
	
	@Test
	public void deveExcluirServico(){
		deveSalvarServico();
		
		TypedQuery<Long> query = em.createQuery("SELECT MAX(s.id) FROM Servico s",Long.class);
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		Servico servico = em.find(Servico.class, id);
		em.remove(servico);
		em.getTransaction().commit();
		Servico servicoExcluido = em.find(Servico.class, id);
		
		assertNull("Não deve encontrar o servico",servicoExcluido);
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

		//servicos.forEach(servico -> assertNull("Não dever ter Sigla", servico.getSigServico()));
		
		  for(Servico servico: servicos){
		  assertNull("Verifica que o cpf deve estar null", servico.getSigServico());
		  servico.setSigServico(SIG_PADRAO); }
		 
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarIdDescricao(){
		deveSalvarServico();
		
		Query query = em.createQuery("SELECT s.id, s.descricao FROM Servico s WHERE s.sigServico =:sigServico");
		query.setParameter("sigServico", SIG_PADRAO);
		
		List<Object[]> resultado = query.getResultList();
		
		assertFalse("verifica se há registros na lista",resultado.isEmpty());
		
		for (Object[] linha : resultado){
			assertTrue("Verifica que o primeiro item é o ID",linha[0] instanceof Long);
			assertTrue("Verifica que o segundo item é descricao",linha[1] instanceof String);
			
			Servico servico = new Servico((Long) linha[0], (String) linha[1]);
			assertNotNull(servico);
			
		}
	}
	
	
	 
	  	@Test
	public void deveContarExistenciaServico() {
		// COUNT
		deveSalvarServico();
		
		Query query = em.createQuery("SELECT COUNT(s.id) FROM Servico s WHERE s.sigServico = :sigServico");
		query.setParameter("sigServico", SIG_PADRAO);

		Long qtdResultados = (Long) query.getSingleResult();

		assertTrue("Verifica se há registros na lista", qtdResultados > 0L);

	}
	  
	  //-----------------------Criteria-----
		//-------------------------------Builders-----------------------------
		
		
			private Session getSession(){
				return (Session) em.getDelegate();
			}
			
			@SuppressWarnings("unused")
			private Criteria createCriteria(Class<?> clazz){
				return getSession().createCriteria(clazz);
				}
			private Criteria createCriteria(Class<?> clazz, String alias){
				return getSession().createCriteria(clazz, alias);
			}
			
			
			
			
		//////////////////////////////////////////////////////////////////////
@SuppressWarnings("unchecked")
@Test
public void deveConsultarTodosServicos(){
for (int i = 0; i < 10; i++) {
	deveSalvarServico();
}


Criteria criteria =createCriteria(Servico.class, "A");

List<Servico> servicos = criteria
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.list();
System.out.println("Testando"+ servicos.size());
assertTrue("Verifica se a quantidade e servicos é pelo menos 3", servicos.size()>=3);
servicos.forEach(servico -> assertFalse(servico.isTransient()));

}

@Test
public void deveConsultarMaiorIdServico(){
for (int i = 0; i < 10; i++) {
	deveSalvarServico();
}
Criteria criteria = createCriteria(Servico.class,"a").setProjection(Projections.max("a.id"));
Long maiorId = (Long) criteria .setResultTransformer(Criteria.PROJECTION).uniqueResult();
assertTrue("Verifica se o ID é maior que 2 (Salvou 3 servicos",maiorId >=3);
}

@SuppressWarnings("unchecked")
@Test
public void deveConsultarServicoPorNome(){

for (int i = 0; i < 10; i++) {
	deveSalvarServico();
}

Criteria criteria = createCriteria(Servico.class, "a")
			.add(Restrictions.ilike("a.descricao", SERVICO_PADRAO,MatchMode.ANYWHERE))
			.addOrder(Order.asc("a.id"));

List<Servico> servicos = criteria
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
			.list();
System.out.println("Testando"+ servicos.size());
assertTrue("Verifica se a quantidade de servicos é pelo menos 3", servicos.size()>=3);

servicos.forEach(servico -> assertFalse(servico.isTransient()));
}

///////////////////////////////////////
	  
	
	
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
