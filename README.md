# jpa-trabalho-aula02
Trabalho de conclusão da Disciplina de JPA da Pós-Gradução em Desenvolvimento de Software Full Stack
da Faculdade Delta
Qual a responsabilidade/objeto das anotações:

@MappedSuperclass
	R.: Mapear uma classe generica

@Version
	R.: mapeia um atributo com a versão de do resgistro permitindo identificar quando foi inserido ou alterado e dessa forma utilizar o Look otimista

@Entity
	R.: Mapeia uma classe como entidade

@Table
	R.: Permite passar alguns parametros como nome da tabela ao mapear uma entidade

@Id
	R.: Mapeia um atributo como PK da entidade

@GeneratedValue
	R.: Permite definir a estrategia da PK em uma entidade com por exemplo "GenerationType.AUTO"

@Column
	R.: Permite passar diversos parametros ao maper um atributo de uma entidade

@Basic
	R.: Extremamente parecido com o @Column, permite definir o carregamento em relacionamentos

@Temporal
	R.: Usado para campos do tipo data, time ou datetime


Qual a responsabilidade/objeto das anotações:

@ManyToOne
	R.: Utilizado para mapear um relacionamento Muitos-para-um, geralmente o lado forte de um relacionamento bi-direcional

@ManyToMany
	R.: utilizado para mapear um relacionamento multidirecional de muitos-para-muitos

@OneToOne
	R.: com uso semelhante ao @OneToMany é utilizada para mapear o lado fraco de um relacionamento bi-direcional, sendo utilizada no lado fraco
		onde recebe uma String que é o nome do atributo do lado forte, também permite a exclusão de registros lixo

@JoinColumn
	R.: Utilizadas para passar parametros sobre a coluna da FK

@JoinTable
	R.: Utilizada para mapear tabelas de junção em relacionamentos ManyToMany, tem os mesmos parametros de @Table outros mais

Qual a responsabilidade/objeto dos métodos do EntityManager:

isOpen
	R.: 

close
	R.: Fecha a instacia do EM

createQuery
	R.: permite cria uma query para diversas finalidades

find
	R.: permite encontrar determinado objeto/registro na instacia do EM

merge
	R.: utilizada para efetuar um update em um objeto/registro que esteja na instacia do EM

persist
	R.: usado para persistir/inserir um registro de entidade gerenciada na base de dados

remove
	R.: ao contrario do persist remove um resgistro/objeto da base de dados atraves de uma entidade gerenciada

Como instânciar Criteria do Hibernate através do EntityManager?
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

Como abrir uma transação?
	em.getTransaction().begin();


Como fechar uma transação?
	em.getTransaction().commit();

Como criar e executar uma query com JPQL?
Query query = em.createQuery("SELECT new Cliente(c.id, c.nome) FROM Cliente c WHERE c.cpf = :cpf");
	  query.setParameter("cpf", CPF_PADRAO);
	  List<Cliente> clientes = (List<Cliente>) query.getResultList();

Qual a responsabilidade dos valores FetchType.LAZY e FetchType.EAGER?
	R.: Define a prioridade de carregamento de atributos em relacionamentos

Qual a responsabilidade dos valores CascadeType.PERSIST e CascadeType.REMOVE?
	R.: Permitem respcticvamente persistir e remover registros em modo cascata em relacionamentos

Como fazer uma operação BATCH (DELETE ou UPDATE) através do EntityManager?
	R.: Primeiramente deve-se obter o id de objeto/registro de uma entidade gerenciada 
		para que então possa se utilizar o metodo merge o remove

Qual a explicação para a exception LazyInitializationException?
	R.: Ocorre quando tenta-se carregar atributos de relacionamentos que tenham o carregamento definido como FetchType.LAZY e não estão mais na instancia do EM
