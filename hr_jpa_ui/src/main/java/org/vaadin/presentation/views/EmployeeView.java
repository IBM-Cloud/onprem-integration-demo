package org.vaadin.presentation.views;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import backend.EmployeeService;
import model.Employee;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.presentation.AppUI;
import org.vaadin.presentation.ScreenSize;
import org.vaadin.presentation.views.EmployeeEvent.Type;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.fields.MValueChangeEvent;
import org.vaadin.viritin.fields.MValueChangeListener;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.FieldEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

/**
 * A view that lists Employees in a Table and lets user to choose one for
 * editing. There is also RIA features like on the fly filtering.
 */
@CDIView("employees")
@ViewMenuItem(icon = FontAwesome.USERS, order = ViewMenuItem.BEGINNING)
public class EmployeeView extends MVerticalLayout implements View {

    @Inject
    private EmployeeService service;

    @Inject
    EmployeeForm customerEditor;

    // Introduce and configure some UI components used on this view
    MTable<Employee> customerTable = new MTable(Employee.class).withFullWidth().
            withFullHeight();

    MHorizontalLayout mainContent = new MHorizontalLayout(customerTable).
            withFullWidth().withMargin(false);

    TextField filter = new TextField();

    Header header = new Header("Employees").setHeaderLevel(2);

    Button addButton = new MButton(FontAwesome.EDIT,
            new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    addEmployee();
                }
            });

    @PostConstruct
    public void init() {

        /*
         * Add value change listener to table that opens the selected customer into
         * an editor.
         */
        customerTable.addMValueChangeListener(new MValueChangeListener<Employee>() {

            @Override
            public void valueChange(MValueChangeEvent<Employee> event) {
                editEmployee(event.getValue());
            }
        });

        /*
         * Configure the filter input and hook to text change events to
         * repopulate the table based on given filter. Text change
         * events are sent to the server when e.g. user holds a tiny pause
         * while typing or hits enter.
         * */
        filter.setInputPrompt("Filter customers...");
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent textChangeEvent) {
                listEmployees(textChangeEvent.getText());
            }
        });


        /* "Responsive Web Design" can be done with plain Java as well. Here we
         * e.g. do selective layouting and configure visible columns in
         * table based on available width */
        layout();
        adjustTableColumns();
        /* If you wish the UI to adapt on window resize/page orientation
         * change, hook to BrowserWindowResizeEvent */
        UI.getCurrent().setResizeLazy(true);
        Page.getCurrent().addBrowserWindowResizeListener(
                new Page.BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(
                            Page.BrowserWindowResizeEvent browserWindowResizeEvent) {
                                adjustTableColumns();
                                layout();
                            }
                });

        listEmployees();
    }

    /**
     * Do the application layout that is optimized for the screen size.
     * <p>
     * Like in Java world in general, Vaadin developers can modularize their
     * helpers easily and re-use existing code. E.g. In this method we are using
     * extended versions of Vaadins basic layout that has "fluent API" and this
     * way we get bit more readable code. Check out vaadin.com/directory for a
     * huge amount of helper libraries and custom components. They might be
     * valuable for your productivity.
     * </p>
     */
    private void layout() {
        removeAllComponents();
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            addComponents(
                    new MHorizontalLayout(header, filter, addButton)
                    .expand(header)
                    .alignAll(Alignment.MIDDLE_LEFT),
                    mainContent
            );

            filter.setSizeUndefined();
        } else {
            addComponents(
                    header,
                    new MHorizontalLayout(filter, addButton)
                    .expand(filter)
                    .alignAll(Alignment.MIDDLE_LEFT),
                    mainContent
            );
        }
        setMargin(new MarginInfo(false, true, true, true));
        expand(mainContent);
    }

    /**
     * Similarly to layouts, we can adapt component configurations based on the
     * client details. Here we configure visible columns so that a sane amount
     * of data is displayed for various devices.
     */
    private void adjustTableColumns() {
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            customerTable.setVisibleColumns("firstName", "lastName", "hireDate");
            customerTable.setColumnHeaders("First name", "Last name", "Hire Date");
        } else {
            // Only show one (generated) column with combined first + last name
            if (customerTable.getColumnGenerator("name") == null) {
                customerTable.addGeneratedColumn("name",
                        new Table.ColumnGenerator() {
                            @Override
                            public Object generateCell(Table table, Object o,
                                    Object o2) {
                                Employee c = (Employee) o;
                                return c.getFirstName() + " " + c.getLastName();
                            }
                        });
            }
            if (ScreenSize.getScreenSize() == ScreenSize.MEDIUM) {
                customerTable.setVisibleColumns("name");
                customerTable.setColumnHeaders("Name");
            } else {
                customerTable.setVisibleColumns("name");
                customerTable.setColumnHeaders("Name");
            }
        }
    }

    /* ******* */
    // Controller methods.
    //
    // In a big project, consider using separate controller/presenter
    // for improved testability. MVP is a popular pattern for large
    // Vaadin applications.
    private void listEmployees() {
        // Here we just fetch data straight from the EJB.
        //
        // If you expect a huge amount of data, do proper paging,
        // or use lazy loading Vaadin Container like LazyQueryContainer
        // See: https://vaadin.com/directory#addon/lazy-query-container:vaadin
        customerTable.setBeans(new ArrayList<>(service.findAll()));
    }

    private void listEmployees(String filterString) {
        customerTable.setBeans(new ArrayList<>(service.findByName(filterString)));
    }

    void editEmployee(Employee customer) {
        if (customer != null) {
            openEditor(customer);
        } else {
            closeEditor();
        }
    }

    void addEmployee() {
        openEditor(new Employee());
    }

    private void openEditor(Employee customer) {
        customerEditor.setEntity(customer);
        // display next to table on desktop class screens
        if (ScreenSize.getScreenSize() == ScreenSize.LARGE) {
            mainContent.addComponent(customerEditor);
            customerEditor.focusFirst();
        } else {
            // Replace this view with the editor in smaller devices
            AppUI.get().getContentLayout().
                    replaceComponent(this, customerEditor);
        }
    }

    private void closeEditor() {
        // As we display the editor differently in different devices,
        // close properly in each modes
        if (customerEditor.getParent() == mainContent) {
            mainContent.removeComponent(customerEditor);
        } else {
            AppUI.get().getContentLayout().
                    replaceComponent(customerEditor, this);
        }
    }

    /* These methods gets called by the CDI event system, which is also
     * available for Vaadin UIs when using Vaadin CDI add-on. In this
     * example events are arised from EmployeeForm. The CDI event system
     * is a great mechanism to decouple components.
     */
    void saveEmployee(@Observes @EmployeeEvent(Type.SAVE) Employee customer) {
        listEmployees();
        closeEditor();
    }

    void resetEmployee(@Observes @EmployeeEvent(Type.REFRESH) Employee customer) {
        listEmployees();
        closeEditor();
    }

    void deleteEmployee(@Observes @EmployeeEvent(Type.DELETE) Employee customer) {
        closeEditor();
        listEmployees();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

}
