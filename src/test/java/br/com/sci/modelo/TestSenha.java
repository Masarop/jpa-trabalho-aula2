package br.com.sci.modelo;

import static org.junit.Assert.*;

import java.util.Date;
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

import br.com.sci.modelo.Senha;
import br.com.sci.util.JPAUtil;

public class TestSenha {
	private EntityManager em;
	private static final String NM_PADRAO = "Wilson Pereira";
	private static final String DOC_PADRAO = "001.001.001-01";
	private static final String SERVICO_PADRAO = "Fisioterapia";
	private static final String SIG_PADRAO = "AAA";
	private static final int SENHA_PADRAO = 111;
	// -------------------------------------Builders-------------------------------------

	private Senha criarSenha() {
		return criarSenha(null, null, null, null, 0);
	}

	private Senha criarSenha(String nmPac, String docPac, String nmServ, String sigServ, int numeroSenha) {

		Paciente paciente = new Paciente();
		paciente.setNome(nmPac == null ? NM_PADRAO : nmPac);
		paciente.setDocumento(docPac == null ? DOC_PADRAO : docPac);

		Servico servico = new Servico();
		servico.setDescricao(nmServ == null ? SERVICO_PADRAO : nmServ);
		servico.setSigServico(sigServ == null ? SIG_PADRAO : sigServ);

		assertTrue("não deve ter ID definido", paciente.isTransient());
		assertTrue("não deve ter ID definido", servico.isTransient());

		Senha senha = new Senha();
		senha.setDataEmissao(new Date());
		senha.setNumSenha(numeroSenha == 0 ? SENHA_PADRAO : numeroSenha);
		senha.setPaciente(paciente);
		senha.setServico(servico);

		return senha;
	}

	public void deveSalvarSenha(int ns) {
		Senha senha = criarSenha(null, null, null, null, ns);
		assertTrue("Não deve ter ID definido", senha.isTransient());

		em.getTransaction().begin();
		em.persist(senha);
		em.getTransaction().commit();

		assertFalse("entidade agora tem id definido", senha.isTransient());
	}

	//////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void deveSalvarVendaComRelacionamentoEmCascata() {
		Senha senha = criarSenha();

		assertTrue("não deve ter ID definido", senha.isTransient());

		em.getTransaction().begin();
		em.persist(senha);
		em.getTransaction().commit();

		assertFalse("deve ter ID definido", senha.isTransient());
		assertFalse("deve ter ID definido", senha.getPaciente().isTransient());
		assertFalse("deve ter ID definido", senha.getServico().isTransient());
	}

	@Test
	public void deveSalvarSenha() {
		deveSalvarSenha(1);
	}

	@Test
	public void deveAlterarSenha() {
		deveSalvarSenha();

		TypedQuery<Senha> query = em.createQuery("SELECT s FROM Senha s", Senha.class).setMaxResults(1);
		Senha senha = query.getSingleResult();

		assertNotNull("Deve ter encontrado um senha", senha);

		Integer versao = senha.getVersion();

		em.getTransaction().begin();

		senha.setNumSenha(senha.getNumSenha() + 50);

		senha = em.merge(senha);

		em.getTransaction().commit();

		assertNotEquals("deve ter versao incrementada", versao.intValue(), senha.getVersion().intValue());
	}

	@Test
	public void deveExcluirSenha() {
		deveSalvarSenha();

		TypedQuery<Long> query = em.createQuery("SELECT MAX(s.id) FROM Senha s", Long.class);
		Long id = query.getSingleResult();

		em.getTransaction().begin();
		Senha senha = em.find(Senha.class, id);
		em.remove(senha);
		em.getTransaction().commit();
		Senha senhaExcluida = em.find(Senha.class, id);

		assertNull("Não deve encontrar o senha", senhaExcluida);
	}

	// -------------------------------CONSULTAS------------------------------
	/**
	 * Retorna todas as Senhas em uma lista de objetos
	 */
	@Test
	public void devePesquisarSenhas() {
		for (int i = 0; i < 10; i++) {
			deveSalvarSenha(i);
		}

		TypedQuery<Senha> query = em.createQuery("SELECT s FROM Senha s", Senha.class);
		List<Senha> senhas = query.getResultList();

		assertFalse("deve ter encontrado um senha", senhas.isEmpty());
		assertTrue("deve ter encontrado vários senhas", senhas.size() >= 10);
	}

	@Test
	public void deveConsultarQuantidadeDeServicosComSenhas() {

		for (int i = 0; i < 20; i++) {
			deveSalvarSenha(800 + i);
		}

		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(ss.id) ");
		jpql.append("   FROM Senha s ");
		jpql.append("   INNER JOIN s.servico ss ");
		jpql.append("   WHERE s.servico.descricao = :servico ");

		Query query = em.createQuery(jpql.toString());
		query.setParameter("servico", SERVICO_PADRAO);

		Long qtdsenhas = (Long) query.getSingleResult();

		assertEquals("quantidade de servicos deve ser igual a quantidade da lista de produtos", qtdsenhas.intValue(),20);
		System.out.println("Testeando COUNT:"+qtdsenhas.intValue());
	}

	@Test
	public void deveConsultarQuantidadeDePacientesComSenhas() {

		for (int i = 0; i < 10; i++) {
			Senha senha = criarSenha("Lucas", null, null, null, 300 + i);
			assertTrue("Não deve ter ID definido", senha.isTransient());

			em.getTransaction().begin();
			em.persist(senha);
			em.getTransaction().commit();

			assertFalse("entidade agora tem id definido", senha.isTransient());
		}

		StringBuilder jpql = new StringBuilder();
		jpql.append(" SELECT COUNT(p.id) ");
		jpql.append("   FROM Senha s ");
		jpql.append("   INNER JOIN s.paciente p ");
		jpql.append("   WHERE s.paciente.nome = :nome ");

		Query query = em.createQuery(jpql.toString());
		query.setParameter("nome", "Lucas");

		Long qtdsenhas = (Long) query.getSingleResult();

		// System.out.println("Testeando COUNT:"+qtdsenhas.intValue());

		assertEquals("quantidade de servicos deve ser igual a quantidade da lista de produtos", qtdsenhas.intValue(),
				10);
	}

	/////////////////////////////////////////////////////////////////////////////////////////

	// -----------------------Criteria-----
	// -------------------------------Builders-----------------------------

	private Session getSession() {
		return (Session) em.getDelegate();
	}

	@SuppressWarnings("unused")
	private Criteria createCriteria(Class<?> clazz) {
		return getSession().createCriteria(clazz);
	}

	private Criteria createCriteria(Class<?> clazz, String alias) {
		return getSession().createCriteria(clazz, alias);
	}

	//////////////////////////////////////////////////////////////////////
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarTodosSenhas() {
		for (int i = 0; i < 10; i++) {
			deveSalvarSenha(i);
		}

		Criteria criteria = createCriteria(Senha.class, "A");

		List<Senha> senhas = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		System.out.println("Testando" + senhas.size());
		
		assertTrue("Verifica se a quantidade e senhas é pelo menos 3", senhas.size() >= 3);
		senhas.forEach(senha -> assertFalse(senha.isTransient()));

	}

	@Test
	public void deveConsultarMaiorIdSenha() {
		for (int i = 0; i < 10; i++) {
			deveSalvarSenha(i);
		}
		Criteria criteria = createCriteria(Senha.class, "a").setProjection(Projections.max("a.id"));
		Long maiorId = (Long) criteria.setResultTransformer(Criteria.PROJECTION).uniqueResult();
		assertTrue("Verifica se o ID é maior que 2 (Salvou 3 senhas", maiorId >= 3);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarSenhaPorNome() {

		for (int i = 0; i < 4; i++) {
			deveSalvarSenha(SENHA_PADRAO);
		}

		Criteria criteria = createCriteria(Senha.class, "a")
				.add(Restrictions.in("a.numSenha", SENHA_PADRAO))
				.addOrder(Order.asc("a.numSenha"));

		List<Senha> senhas = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		System.out.println("Testando" + senhas.size());
		assertTrue("Verifica se a quantidade de senhas é pelo menos 3", senhas.size() >= 3);

		senhas.forEach(senha -> assertFalse(senha.isTransient()));
	}

	///////////////////////////////////////

	@AfterClass
	public static void deveLimparBaseTeste() {
		EntityManager entityManager = JPAUtil.ISNTANCE.getEntityManager();
		entityManager.getTransaction().begin();
		Query query = entityManager.createQuery("DELETE FROM Senha s");
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
