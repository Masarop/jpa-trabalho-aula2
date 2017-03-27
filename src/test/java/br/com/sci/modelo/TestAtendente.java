package br.com.sci.modelo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

import br.com.sci.modelo.Atendente;
import br.com.sci.util.JPAUtil;

public class TestAtendente {

	private EntityManager em;
	private static final String NM_PADRAO = "Nirson Pereira";
	private static final String LOGIN_PADRAO = "Nirso";
	private static final String SENHA_PADRAO = "12345678";

	private Servico criarServico(String descricao, String sigServico) {
		Servico servico = new Servico();
		servico.setDescricao(descricao);
		servico.setSigServico(sigServico);
		return servico;
	}

	private Atendente criarAtendente() {
		return criarAtendente(null, null, null);
	}

	private Atendente criarAtendente(String nm, String login, String senha) {
		Atendente atendente = new Atendente();
		atendente.setLogin(login == null ? LOGIN_PADRAO : login);
		atendente.setNome(nm == null ? NM_PADRAO : nm);
		atendente.setSenha(senha == null ? SENHA_PADRAO : senha);
		return atendente;
	}

	@Test
	public void deveSalvarVendaComRelacionamentoEmCascata() {
		Atendente atendente = criarAtendente();
		
		atendente.getServicos().add(criarServico("Consulta Ortopedia", "COO"));
		atendente.getServicos().add(criarServico("Exame Raio-X", "ERX"));
		
		assertTrue("não deve ter ID definido", atendente.isTransient());
		
		em.getTransaction().begin();
		em.persist(atendente);
		em.getTransaction().commit();
		
		assertFalse("deve ter ID definido",atendente.isTransient());
		
		atendente.getServicos().forEach(
				servico -> assertFalse("deve ter ID definido",servico.isTransient())
				);
	}

	@Test
	public void deveSalvarAtendente() {
		Atendente atendente = criarAtendente();
		assertTrue("Não deve ter ID definido", atendente.isTransient());

		em.getTransaction().begin();
		em.persist(atendente);
		em.getTransaction().commit();

		assertFalse("entidade agora tem id ainda", atendente.isTransient());
	}

	

	@Test
	public void deveAlterarAtendente() {
		deveSalvarAtendente();

		TypedQuery<Atendente> query = em.createQuery("SELECT a FROM Atendente a", Atendente.class).setMaxResults(1);
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
	public void deveExcluirAtendente() {
		deveSalvarAtendente();

		TypedQuery<Long> query = em.createQuery("SELECT MAX(a.id) FROM Atendente a", Long.class);
		Long id = query.getSingleResult();

		em.getTransaction().begin();
		Atendente atendente = em.find(Atendente.class, id);
		em.remove(atendente);
		em.getTransaction().commit();
		Atendente atendenteExcluido = em.find(Atendente.class, id);

		assertNull("Não deve encontrar o atendente", atendenteExcluido);
	}
	//---------------------------consultas
	@Test
	public void devePesquisarAtendentes() {
		for (int i = 0; i < 2; i++) {
			deveSalvarAtendente();
		}

		TypedQuery<Atendente> query = em.createQuery("SELECT a FROM Atendente a", Atendente.class);
		List<Atendente> atendentes = query.getResultList();

		assertFalse("deve ter encontrado um atendente", atendentes.isEmpty());
		assertTrue("deve ter encontrado vários atendentes", atendentes.size() >= 2);
	}
	@SuppressWarnings("unchecked")
	
	@Test
	public void deveConsultarIdDescricao(){
		criarAtendente();
		
		Query query = em.createQuery("SELECT a.id, a.nome FROM Atendente a WHERE a.login =:login");
		query.setParameter("login", LOGIN_PADRAO);
		
		List<Object[]> resultado = query.getResultList();
		
		assertFalse("verifica se há registros na lista",resultado.isEmpty());
		
		for (Object[] linha : resultado){
			assertTrue("Verifica que o primeiro item é o ID",linha[0] instanceof Long);
			assertTrue("Verifica que o segundo item é o nome",linha[1] instanceof String);
			
			Atendente atendente = new Atendente((Long) linha[0], (String) linha[1]);
			assertNotNull(atendente);
			
		}
	}
	@Test
	public void deveConsultarQuantidadeServicosAtendidos(){
		
		Atendente atendente = criarAtendente("Nerson Pereira", "Nerson", "12345678");
		
		for (int i=0;i<10;i++){
			atendente.getServicos().add(criarServico("Servico_"+i, "Sigla_"+i));
		}
		em.getTransaction().begin();
		em.persist(atendente);
		em.getTransaction().commit();
		
		assertFalse("deve ter ID definido",atendente.isTransient());
		
		int qtdServicosAdicionados = atendente.getServicos().size();
		
		assertTrue("Lista de servico deve ter servicos",qtdServicosAdicionados>0);
		
		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(s.id) ");
		jpql.append("   FROM Atendente a ");
		jpql.append("   INNER JOIN a.servicos s ");
		jpql.append("   WHERE a.login = :login ");
		
		Query query = em.createQuery(jpql.toString());
		query.setParameter("login", "Nerson");
		
		Long qtdservicosatendidos = (Long) query.getSingleResult();
		
		assertEquals("quantidade de servicos deve ser igual a quantidade da lista de produtos", qtdservicosatendidos.intValue(), qtdServicosAdicionados);

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
public void deveConsultarTodosAtendentes(){
	for (int i = 0; i < 10; i++) {
		deveSalvarAtendente();
	}
	
	
	Criteria criteria =createCriteria(Atendente.class, "A");
	
	List<Atendente> atendentes = criteria
						.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
						.list();
	System.out.println("Testando"+ atendentes.size());
	assertTrue("Verifica se a quantidade e atendentes é pelo menos 3", atendentes.size()>=3);
	atendentes.forEach(atendente -> assertFalse(atendente.isTransient()));
	
}

@Test
public void deveConsultarMaiorIdAtendente(){
	for (int i = 0; i < 10; i++) {
		deveSalvarAtendente();
	}
	Criteria criteria = createCriteria(Atendente.class,"a").setProjection(Projections.max("a.id"));
	Long maiorId = (Long) criteria .setResultTransformer(Criteria.PROJECTION).uniqueResult();
	assertTrue("Verifica se o ID é maior que 2 (Salvou 3 atendentes",maiorId >=3);
}

@SuppressWarnings("unchecked")
@Test
public void deveConsultarAtendentePorNome(){
	
	for (int i = 0; i < 4; i++) {
		deveSalvarAtendente();
	}
	
	Criteria criteria = createCriteria(Atendente.class, "a")
				.add(Restrictions.ilike("a.nome", NM_PADRAO,MatchMode.ANYWHERE))
				.addOrder(Order.asc("a.nome"));
	
	List<Atendente> atendentes = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
	System.out.println("Testando"+ atendentes.size());
	assertTrue("Verifica se a quantidade de atendentes é pelo menos 3", atendentes.size()>=3);
	
	atendentes.forEach(atendente -> assertFalse(atendente.isTransient()));
}

	///////////////////////////////////////
	@AfterClass
	public static void deveLimparBaseTeste() {
		EntityManager entityManager = JPAUtil.ISNTANCE.getEntityManager();
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM Atendente a");
		int qtdRegistrosExclidos = query.executeUpdate();
		entityManager.getTransaction().commit();

		assertTrue("Certifica que a base foi limpada", qtdRegistrosExclidos > 0);

	}

	@Before
	public void instanciarEntityManager() {
		em = JPAUtil.ISNTANCE.getEntityManager();
	}

	@After
	public void fecharEntityManagaer() {
		if (em.isOpen()) {
			em.close();
		}
	}

}
