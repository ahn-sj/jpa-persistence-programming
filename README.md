# Java Persistence Guide (01)

영속성
- SQL을 사용해 데이터베이스의 객체 인스턴스를 매핑하고 데이터베이스에 저장하는 것

영속성에 대한 고민
- SQL 또는 프로시저로 해결이 된 걸까 아니면 자바 프레임워크로 해결해야 하나?
- 각기 다른 데이터베이스 벤더마다 SQL 벤더에 대해 어떻게 이식성을 확보할 수 있을까?
  -> ORM 기술이라는 솔루션이 폭넓게 사용중임

1.1.3 자바에서의 SQL
자바 애플리케이션에서는 데이터베이스에 접근할 때 JDBC API를 통해 SQL문을 전달한다.
SQL을 생성하고, 쿼리 매개변수를 준비하고, 쿼리를 실행해서 결과를 순회하여 결과 집합에서 값을 조회하는 등의 작업을 하는 작업은 어떠한 기술을 쓰던간에 JDBC API를 사용해 인수를 바인딩한다.

1.2 패러다임의 불일치
- 데이터 타입의 세분성 불일치로 인한 어려움
- 자바에서의 상속과 DB에서의 상속. 더 나아가 다형성은?
- 자바에서의 동등성과 동일성 그리고 DB에서의 동등성과 동일성
    - 동일성은 캐싱과 트랜잭션과도 연관있음
- 객체지향 언어의 객체 참조 연관관계와 관계형 언어의 오래키 참조 연관관계의 불일치
- 객체와 DB의 데이터 탐색 문제

1.2.1 세분성 문제
> *세분성: 여러 개의 칼럼을 추가할지 또는 새로운 타입의 칼럼을 추가할지에 대한 선택

결론
- DB에는 새로운 타입을 추가하는 것이 극히 제한적이며 지원한다한들, 일반적인 관행도 아님
- 반면, 자바는 세분화된 클래스, 추상화 클래스 등 다양한 타입을 지원한다.

1.2.2 상속
> *상속: 상위클래스와 하위클래스를 사용해 타입 계층을 이룸*

결론
- DB는 일반적으로 상속을 구현하지 않고, 상속을 구현하더라도 다형성 문제가 존재함
- 자바는 상속, 다형성 등 하위 클래스가 상위 클래스를 상속하는 것을 지원함

1.2.3 동일성 문제
- 자바에서의 비교
    - 동일성: 메모리 위치 비교 (a == b)
    - 동등성: 값에 의한 비교  equals()
- DB에서의 비교
    - 키 값의 비교 -> 보통 외래키로 동일성 체크를 함

1.2.4 연관관계 문제
- 객체지향 언어: 객체 참조를 통해 연관관계가 있는 한 다른 인스턴스로 참조가 계속 가능하고, 양방향 관계를 맺을 수 있음
- 관계형 언어: 외래 키를 통해서만 연관관계를 나타내고, 양방향이 불가능함


---


1.3 ORM, JPA, Hibernate, Spring Data
- ORM 이란
    - 애플리케이션의 클래스와 데이터베이스의 스키마간의 매핑을 설명하는 메타데이터를 사용해 자바 애플리케이션의 객체를 RDB 테이블에 자동으로 영속화하는 기술
- JPA
    - 객체/관계형 매핑의 영속성을 관리하는 API 명세
- Hibernate
    - API 명세에 대한 구현체
- Spring Data
    - 데이터베이스에 대한 직접적인 접근(관리)을 간소화하는 것

하이버네이트를 효과적으로 사용하려면??
- SQL 문을 읽을 줄 알아야 하고,
- SQL 문이 성능에 미치는 영향을 알아야 하고,
- 상용구 코드(보일러 플레이트)와 생성된 쿼리가 어떻게 만들어지는지 예측 가능해야 함

- 영속성 컨텍스트는 엔티티 매니저의 라이프 사이클과 공유한다.
- 스프링 데이터 JPA는 data.jpa 인터페이스를 상속한 인터페이스를 프록시 클래스로 생성하고 메서드를 구현함
- JPA나 네이티브 하이버네이트를 직접 사용해서 상용구 코드나 명시적인 객체 생성/트랜잭션 제어를 하는 것과 달리 스프링 데이터 JPA는 상당히 짧아진다
```java
@Test  
void storeLoadMessage() {  
  
    // emf는 thread-safe하다.  
    final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ch02");  
  
    try {  
        // em -> DB 세션 생성 == 영속성 컨텍스트  
        final EntityManager em = emf.createEntityManager();  
  
        em.getTransaction().begin();  
        final Message message = new Message();  
        message.setText("Hello, JPA!");  
        em.persist(message);  
        em.getTransaction().commit();  
  
        em.getTransaction().begin();  
        final List<Message> messages = em.createQuery("SELECT m FROM Message m", Message.class)  
                .getResultList();  
        messages.get(messages.size() - 1).setText("Hello, JPA! Updated!");  
        em.getTransaction().commit();  
  
        assertAll(  
                () -> assertEquals(1, messages.size()),  
                () -> assertEquals("Hello, JPA! Updated!", messages.get(0).getText()));  
        em.close();  
    } finally {  
        emf.close();  
    }  
}

```

```java
@Test  
void storedLoadMessage() {  
    final Message message = new Message();  
    message.setText("Hello, Spring Data JPA!");  
  
    messageRepository.save(message);  
  
    final List<Message> messages = (List<Message>) messageRepository.findAll();  
  
    assertAll(  
            () -> assertEquals(1, messages.size()),  
            () -> assertEquals("Hello, Spring Data JPA!", messages.get(0).getText())  
    );  
  
}
```
각 접근 방식의 성능적으로 봤을 때에는 네이티브 하이버네이트와 JPA는 비슷한 성능을 가지고 스프링 데이터 JPA에 비해 빠르다. (p47~p50)

---

03. 도메인 모델과 메타데이터
    3.1.1 계층형 아키텍처
- 계층형 아키텍처는 다양한 관심사를 구현하는 코드 간의 인터페이스를 정의해서 다른 계층의 코드에 크게 영향을 주지않고도 한 가지 관심사가 구현되는 방식
- 계층간에 발생하는 의존성 규칙
    - 계층은 위에서 아래로 통신하고, 한 계층은 바로 아래 계층의 인터페이스에만 의존한다.
    - 각 계층은 바로 아래 계층을 제외하고는 다른 계층을 인식하지 못한다.


---

**키워드**
- CAP 이론

**읽어볼 내용**
- https://www.linkedin.com/posts/tobyilee_jparepository-no-repository-yes-activity-7178216827775320064-itGC?utm_source=share&utm_medium=member_desktop

