package be.ordina.unitils.testing.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 *
 */
public class DefaultHibernateSessionManager extends HibernateSessionManager {

    private Configuration configuration;

    private SessionFactory sessionFactory;

    // private static final ThreadLocal<Session> threadSession = new ThreadLocal<Session>();

    public DefaultHibernateSessionManager() {
        configuration = new AnnotationConfiguration();
        sessionFactory = configuration.configure().buildSessionFactory();
    }

    @Override
    public Session doGetSession() throws HibernateException {
        return sessionFactory.getCurrentSession();
    }

    public Configuration doGetConfiguration() {
        return configuration;
    }
}