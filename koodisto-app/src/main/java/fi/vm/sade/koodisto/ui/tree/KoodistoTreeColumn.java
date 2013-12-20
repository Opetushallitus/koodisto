/**
 * 
 */
package fi.vm.sade.koodisto.ui.tree;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

import fi.vm.sade.generic.common.I18N;
import fi.vm.sade.koodisto.service.types.common.KoodistoListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoRyhmaListType;
import fi.vm.sade.koodisto.service.types.common.KoodistoVersioListType;
import fi.vm.sade.koodisto.ui.KoodistoPresenter;
import fi.vm.sade.vaadin.Oph;

/**
 * @author tommiha
 * 
 */
@SuppressWarnings("serial")
public class KoodistoTreeColumn extends VerticalLayout {

    private KoodistoPresenter presenter;

    private Button createKoodistoButton;

    private KoodistoRyhmaListType selectedKoodistoRyhma;

    private AbstractKoodistoTree abstractKoodistoTree;

    public KoodistoTreeColumn(final KoodistoPresenter presenter) {
        super();

        this.presenter = presenter;
        createKoodistoButton = new Button(I18N.getMessage("koodistoTree.button.addKoodisto"));
        createKoodistoButton.setEnabled(false);
        createKoodistoButton.addStyleName(Oph.BUTTON_PLUS);
        createKoodistoButton.addStyleName("primary");
        createKoodistoButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                KoodistoTreeColumn.this.presenter.showCreateKoodistoView(selectedKoodistoRyhma);
            }
        });

        // show create koodisto button for users with crud rights
        if (presenter.userCanCreateReadUpdateAndDelete()) {
            this.addComponent(createKoodistoButton);
        }

        abstractKoodistoTree = new AbstractKoodistoTree(presenter) {

            @Override
            protected void koodistoRyhmaClicked(KoodistoRyhmaListType koodistoRyhmaDTO) {
                createKoodistoButton.setEnabled(true);
                if (selectedKoodistoRyhma != (KoodistoRyhmaListType) koodistoRyhmaDTO) {
                    presenter.hideKoodisto();
                    presenter.hideKoodi();
                }
                selectedKoodistoRyhma = (KoodistoRyhmaListType) koodistoRyhmaDTO;

            }

            @Override
            protected void koodistoVersioClicked(KoodistoListType koodistoDTO, KoodistoVersioListType koodistoVersioDTO) {
                createKoodistoButton.setEnabled(false);
                KoodistoTreeColumn.this.presenter.showKoodisto(koodistoDTO.getKoodistoUri(), koodistoVersioDTO.getVersio(), false);

            }
        };

        this.addComponent(abstractKoodistoTree);
        setMargin(true);
        addStyleName(Oph.CONTAINER_MAIN);
    }

    public void selectKoodistoTreeItem(String uri, int version) {
        abstractKoodistoTree.selectKoodistoTreeItem(uri, version);
    }

    public void refreshKoodistoTree() {
        abstractKoodistoTree.refresh();
    }
}
