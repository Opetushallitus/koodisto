package fi.vm.sade.koodisto.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;

import fi.vm.sade.generic.ui.app.AbstractSpringContextApplication;
import fi.vm.sade.vaadin.Oph;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class KoodistoApplication extends AbstractSpringContextApplication implements ApplicationContext.TransactionListener {

    public KoodistoApplication() {
        super();
    }

    private static ThreadLocal<KoodistoApplication> tl = new ThreadLocal<KoodistoApplication>();

    @Autowired
    private KoodistoPresenter presenter;

    public static KoodistoApplication getInstance() {
        return tl.get();
    }

    @Override
    public void transactionStart(Application application, Object transactionData) {
        if (application == this) {
            tl.set(this);
        }
    }

    @Override
    public void transactionEnd(Application application, Object transactionData) {
        if (application == this) {
            tl.remove();
        }
    }

    @Override
    public synchronized void initialize() {
        setTheme(Oph.THEME_NAME);
        getContext().addTransactionListener(this);
        this.transactionStart(this, null);
        presenter.initialize(this);
    }

    public KoodistoPresenter getPresenter() {
        return presenter;
    }
}
