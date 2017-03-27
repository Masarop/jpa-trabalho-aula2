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

public class TestPaciente {

	private EntityManager em;
	private static final String NM_PADRAO = "Wilson Pereira";
	private static final String DOC_PADRAO = "001.001.001-01";
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
	@Test
	public void deveSalvarPaciente() {
		Paciente paciente = new Paciente();
		paciente.setNome(NM_PADRAO);
		paciente.setDocumento(DOC_PADRAO);

		assertTrue("não deve ter ID definido", paciente.isTransient());

		em.getTransaction().begin();
		em.persist(paciente);
		em.getTransaction().commit();

		assertFalse("deve ter ID definido", paciente.isTransient());

	}

	@Test
	public void deveAlterarPaciente() {
		deveSalvarPaciente();

		TypedQuery<Paciente> query = em.createQuery("SELECT p FROM Paciente p", Paciente.class).setMaxResults(1);
		Paciente paciente = query.getSingleResult();

		assertNotNull("Deve ter encontrado um atendente", paciente);

		Integer versao = paciente.getVersion();

		em.getTransaction().begin();

		paciente.setNome("Nirson Prado");

		paciente = em.merge(paciente);

		em.getTransaction().commit();

		assertNotEquals("deve ter versao incrementada", versao.intValue(), paciente.getVersion().intValue());
	}

	@Test
	public void deveExcluirAtendente() {
		deveSalvarPaciente();

		TypedQuery<Long> query = em.createQuery("SELECT MAX(p.id) FROM Paciente p", Long.class);
		Long id = query.getSingleResult();

		em.getTransaction().begin();
		Paciente paciente = em.find(Paciente.class, id);
		em.remove(paciente);
		em.getTransaction().commit();
		Paciente pacienteExcluido = em.find(Paciente.class, id);

		assertNull("Não deve encontrar o atendente", pacienteExcluido);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarDocumento() {
		deveSalvarPaciente();

		Query query = em.createQuery("SELECT p.documento FROM Paciente p WHERE p.nome LIKE :nome");
		query.setParameter("nome", "%Wilson%");
		List<String> lisDOC = query.getResultList();

		assertFalse("verifica se há registros na lista", lisDOC.isEmpty());
	}
	
	
	
//----------------------------------------consultas-------------------------------------
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarPacienteComIdNome() {
		deveSalvarPaciente();

		Query query = em
				.createQuery("SELECT new Paciente(p.id, p.nome) FROM Paciente p WHERE p.documento = :documento");
		query.setParameter("documento", DOC_PADRAO);

		List<Paciente> pacientes = (List<Paciente>) query.getResultList();

		assertFalse("verifica se há registros na lista", pacientes.isEmpty());

		// pacientes.forEach(paciente -> assertNull("Não dever ter Sigla",
		// paciente.getSigServico()));

		for (Paciente paciente : pacientes) {
			assertNull("Verifica que o cpf deve estar null", paciente.getDocumento());
			paciente.setDocumento(DOC_PADRAO);
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarIdNome() {
		deveSalvarPaciente();

		Query query = em.createQuery("SELECT p.id, p.nome FROM Paciente p WHERE p.documento =:documento");
		query.setParameter("documento", DOC_PADRAO);

		List<Object[]> resultado = query.getResultList();

		assertFalse("verifica se há registros na lista", resultado.isEmpty());

		for (Object[] linha : resultado) {
			assertTrue("Verifica que o primeiro item é o ID", linha[0] instanceof Long);
			assertTrue("Verifica que o segundo item é nome", linha[1] instanceof String);

			Paciente paciente = new Paciente((Long) linha[0], (String) linha[1]);
			assertNotNull(paciente);

		}
	}

	@Test
	public void deveContarExistenciaPacientes() {
		// COUNT
		deveSalvarPaciente();

		Query query = em.createQuery("SELECT COUNT(p.id) FROM Paciente p WHERE p.documento = :documento");
		query.setParameter("documento", DOC_PADRAO);

		Long qtdResultados = (Long) query.getSingleResult();

		assertTrue("Verifica se há registros na lista", qtdResultados > 0L);

	}

		//-----------------------Criteria-----
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarTodosPacientes(){
		for (int i = 0; i < 10; i++) {
			deveSalvarPaciente();
		}
		
		
		Criteria criteria =createCriteria(Paciente.class, "P");
		
		List<Paciente> pacientes = criteria
							.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
							.list();
		System.out.println("Testando"+ pacientes.size());
		assertTrue("Verifica se a quantidade e pacientes é pelo menos 3", pacientes.size()>=3);
		pacientes.forEach(paciente -> assertFalse(paciente.isTransient()));
		
	}
	
	@Test
	public void deveConsultarMaiorIdPaciente(){
		for (int i = 0; i < 10; i++) {
			deveSalvarPaciente();
		}
		Criteria criteria = createCriteria(Paciente.class,"p").setProjection(Projections.max("p.id"));
		Long maiorId = (Long) criteria .setResultTransformer(Criteria.PROJECTION).uniqueResult();
		assertTrue("Verifica se o ID é maior que 2 (Salvou 3 pacientes",maiorId >=3);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarPacientePorNome(){
		
		for (int i = 0; i < 4; i++) {
			deveSalvarPaciente();
		}
		
		Criteria criteria = createCriteria(Paciente.class, "P")
					.add(Restrictions.ilike("P.nome", "Wilson",MatchMode.ANYWHERE))
					.addOrder(Order.asc("P.nome"));
		
		List<Paciente> pacientes = criteria
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.list();
		System.out.println("Testando"+ pacientes.size());
		assertTrue("Verifica se a quantidade de pacientes é pelo menos 3", pacientes.size()>=3);
		
		pacientes.forEach(paciente -> assertFalse(paciente.isTransient()));
	}
	
		///////////////////////////////////////
	
	
	//-----------------------------------------------
	
	@AfterClass
	public static void deveLimparBaseTeste() {
		EntityManager entityManager = JPAUtil.ISNTANCE.getEntityManager();
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM Paciente p");
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
