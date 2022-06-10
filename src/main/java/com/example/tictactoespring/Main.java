package com.example.tictactoespring;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Configuration configuration = new Configuration().configure().addAnnotatedClass(User.class);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        User user = new User();
        user.setToken("token");
        user.setNickname("orzel");
        user.setCreated(new Date());
        Transaction transaction = session.beginTransaction();

        session.save(user);

        transaction.commit();
    }
}
