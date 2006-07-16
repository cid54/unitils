package be.ordina.unitils.testing.dao.hibernate;

import be.ordina.unitils.testing.dao.BaseDatabaseTestCase;
import be.ordina.unitils.util.PropertiesUtils;

import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * Base class for DAO tests that use Hibernate.
 */
public abstract class BaseHibernateTestCase extends BaseDatabaseTestCase {

    private static final String PROPKEY_HIBERNATE_CONFIGFILES = "hibernatetestcase.hibernate.cfg.configfiles";

    private static final String PROPKEY_HIBERNATE_CONFIGURATION_CLASS = "hibernatetestcase.hibernate.cfg.configurationclass";

    /**
     * Implementation of <code>HibernateSessionManager</code> for unit testing purposes.
     */
    protected static UnitTestHibernateSessionManager unitTestHibernateSessionManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Make sure Hibernate makes use of the same database connection as the unit test
        if (unitTestHibernateSessionManager == null) {
            Configuration configuration = createHibernateConfiguration();
            unitTestHibernateSessionManager = new UnitTestHibernateSessionManager(configuration);
            HibernateSessionManager.injectInstance(unitTestHibernateSessionManager);
            injectSessionManager(unitTestHibernateSessionManager);
        }
        unitTestHibernateSessionManager.injectConnection(getConnection().getConnection());
    }

    private Configuration createHibernateConfiguration() {
        String configurationClassName = PropertiesUtils.getPropertyRejectNull(properties,
                PROPKEY_HIBERNATE_CONFIGURATION_CLASS);
        List<String> configFiles = PropertiesUtils.getCommaSeperatedStringsRejectNull(properties,
                PROPKEY_HIBERNATE_CONFIGFILES);
        try {
            Configuration configuration = (Configuration) Class.forName(configurationClassName).newInstance();
            for (String configFile : configFiles) {
                configuration.configure(configFile);
            }
            // Hook method to perform extra configuration
            performExtraHibernateConfiguration(configuration);
            return configuration;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid configuration class " + configurationClassName, e);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        unitTestHibernateSessionManager.flushSession();
        unitTestHibernateSessionManager.closeSession();

        super.tearDown();
    }

    /**
     * You should override this method if you want to add some extra stuff to the Hibernate configuration object
     * before the <code>SessionFactory</code> is created. For example, if you want to add you entity class or mapping
     * files using the <code>addClass</code> or <code>addFile</code> methods.
     * @param configuration
     */
    protected void performExtraHibernateConfiguration(Configuration configuration) {
        // Empty implementation
    }

    /**
     * This method should be overwritten if your regular code doesn't make use of Unitils
     * <code>HibernateSessionManager</code>, but you do want to make use of Unitils
     * <code>UnitTestHibernateSessionManager</code>.
     * <p>
     * This method should make sure that your DAO classes make
     * use of the Hibernate session provided by the <code>UnitTestHibernateSessionManager</code>.
     */
    protected void injectSessionManager(UnitTestHibernateSessionManager unitTestSessionManager) {
    }

    /**
     * Hibernate guarantees that within the context of a single HibernateSession,
     * no two different objects of the same entity exist.
     * <p/>
     * Call this method in your tests if you want to test update behavior, after calling the method that performs
     * the update, to avoid getting the retrieved instance from the session cache (first-level).
     */
    protected void clearSession() {
        // Flush all pending saves, updates and deletes to the database.
        unitTestHibernateSessionManager.flushSession();
        // Remove all objects from the Session cache
        unitTestHibernateSessionManager.clearSession();
    }

}