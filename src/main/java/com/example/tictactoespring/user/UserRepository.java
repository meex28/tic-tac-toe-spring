package com.example.tictactoespring.user;

import com.example.tictactoespring.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public class UserRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void add(User user){
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
    }

    @Transactional
    public User get(String token){
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        User user = session.get(User.class, token);
        session.getTransaction().commit();
        return user;
    }

    @Transactional
    public boolean delete(String token){
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        User user = session.get(User.class, token);
        session.delete(user);
        session.getTransaction().commit();
        return true;
    }

    @Transactional
    public boolean delete(User user){
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.delete(user);
        session.getTransaction().commit();
        return true;
    }
}
