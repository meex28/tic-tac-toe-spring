package com.example.tictactoespring.game_session;

import com.example.tictactoespring.game_session.entities.GameSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class GameSessionRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public GameSessionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void save(GameSession gameSession){
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(gameSession);
        session.getTransaction().commit();
    }

    @Transactional
    public void delete(GameSession gameSession){
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(gameSession);
        session.getTransaction().commit();
    }

    @Transactional
    public void update(GameSession gameSession){
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.update(gameSession);
        session.getTransaction().commit();
    }

    public GameSession getByToken(String token){
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GameSession> cq = cb.createQuery(GameSession.class);
        Root<GameSession> rootHost = cq.from(GameSession.class);

        Predicate predicateByHost = cb.equal(rootHost.get("host").get("token"), token);
        Predicate predicateByGuestToken = cb.equal(rootHost.get("guest").get("token"), token);

        cq.where(predicateByHost);
        TypedQuery<GameSession> queryHost = session.createQuery(cq);

        cq.where(predicateByGuestToken);
        TypedQuery<GameSession> queryGuest = session.createQuery(cq);

        List<GameSession> result = queryHost.getResultList();
        result.addAll(queryGuest.getResultList());

        session.getTransaction().commit();

        return result.size()==0 ? null : result.get(0);
    }
}