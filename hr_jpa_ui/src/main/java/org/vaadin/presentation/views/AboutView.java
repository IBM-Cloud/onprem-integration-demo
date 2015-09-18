package org.vaadin.presentation.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.vaadin.backend.CustomerService;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.cdiviewmenu.ViewMenuUI;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/*
 * A very simple view that just displays an "about text". The view also has 
 * a button to reset the demo date in the database.
 */
@CDIView("")
@ViewMenuItem(icon = FontAwesome.INFO)
public class AboutView extends MVerticalLayout implements View {

    @Inject
    CustomerService service;

    @PostConstruct
    void init() {
        add(new RichText().withMarkDownResource("/about.md"));
        /*
        int records = service.findAll().size();
        add(new Label("There are " + records + " records in the DB."));
        
        Button button = new Button("Fill test data into DB", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                service.resetTestData();
                ViewMenuUI.getMenu().navigateTo(CustomerListView.class);
            }
        });
        button.setStyleName(ValoTheme.BUTTON_LARGE);
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        add(button);
		*/
        setMargin(new MarginInfo(false, true, true, true));
        setStyleName(ValoTheme.LAYOUT_CARD);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }
}
