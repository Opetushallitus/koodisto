package fi.vm.sade.koodisto.util;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.EntityManager;
import java.sql.Connection;

/**
 * Spring framework transactional test extension for JUnit4. Cleans the database
 * for DBUnit tests and inserts data set defined in {@link DataSetLocation}
 * annotation. Supports only JTA datasources.
 */
public class JtaCleanInsertTestExecutionListener extends TransactionalTestExecutionListener {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private SessionImpl session;
    private IDatabaseConnection con;
    private Connection jdbcConn;


    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        super.beforeTestMethod(testContext);

        // location of the data set
        String dataSetResourcePath = null;

        // first, the annotation on the test class
        DataSetLocation dsLocation = testContext.getTestInstance().getClass().getAnnotation(DataSetLocation.class);

        if (dsLocation != null) {
            // found the annotation
            dataSetResourcePath = dsLocation.value();
            log.info("Annotated test, using data set: " + dataSetResourcePath);
        }

        if (dataSetResourcePath != null) {
            Resource dataSetResource = testContext.getApplicationContext().getResource(dataSetResourcePath);
            FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
            builder.setColumnSensing(true);
            IDataSet dataSet = builder.build(dataSetResource.getInputStream());

            LocalContainerEntityManagerFactoryBean emf = testContext.getApplicationContext().getBean(
                    LocalContainerEntityManagerFactoryBean.class);

            EntityManager entityManager = emf.getObject().createEntityManager();

            session = entityManager.unwrap(SessionImpl.class);
            jdbcConn = session.connection();
            con = new DatabaseConnection(jdbcConn);
            DatabaseConfig dbConfig = con.getConfig();
            dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
            DatabaseOperation.CLEAN_INSERT.execute(con, dataSet);
//            con.close();
            if (session.getSession().isOpen()) {
                session.getSession().close();
            }
        } else {
            log.info(testContext.getClass().getName() + " does not have any data set, no data injection.");
        }
    }

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        super.beforeTestClass(testContext);


    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        super.afterTestClass(testContext);
//        if (!jdbcConn.isClosed()) {
//            jdbcConn.close();
//        }
//        con.close();
    }
}
