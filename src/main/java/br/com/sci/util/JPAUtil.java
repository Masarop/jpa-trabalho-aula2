package br.com.sci.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public enum JPAUtil {
	
	ISNTANCE;
	
	private EntityManagerFactory factory;

	private JPAUtil() {
		factory=Persistence.createEntityManagerFactory("JpaTrabalhoAula2");
	}
	
	public EntityManager getEntityManager(){
		return factory.createEntityManager();
	}

}
