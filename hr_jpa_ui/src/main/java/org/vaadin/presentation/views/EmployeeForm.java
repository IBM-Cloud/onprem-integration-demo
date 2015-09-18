package org.vaadin.presentation.views;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import backend.EmployeeService;
import model.Employee;
import org.vaadin.backend.domain.Gender;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.TypedSelect;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.inject.Inject;

/**
 * A UI component built to modify Employee entities. The used superclass
 * provides binding to the entity object and e.g. Save/Cancel buttons by
 * default. In larger apps, you'll most likely have your own customized super
 * class for your forms.
 * <p>
 * Note, that the advanced bean binding technology in Vaadin is able to take
 * advantage also from Bean Validation annotations that are used also by e.g.
 * JPA implementation. Check out annotations in Employee objects email field and
 * how they automatically reflect to the configuration of related fields in UI.
 * </p>
 */
public class EmployeeForm extends AbstractForm<Employee> {

    @Inject
    EmployeeService service;

    // Prepare some basic field components that our bound to entity property
    // by naming convetion, you can also use PropertyId annotation
    TextField firstName = new MTextField("First name").withFullWidth();
    TextField lastName = new MTextField("Last name").withFullWidth();
    DateField birthDate = new DateField("Birth day");

    OptionGroup gender = new OptionGroup("Gender");
    TextField email = new MTextField("Email").withFullWidth();

    @Override
    protected Component createContent() {

        setStyleName(ValoTheme.LAYOUT_CARD);

        return new MVerticalLayout(
                new Header("Edit customer").setHeaderLevel(3),
                new MFormLayout(
                        firstName,
                        lastName,
                        email,
                        birthDate,
                        gender
                ).withFullWidth(),
                getToolbar()
        ).withStyleName(ValoTheme.LAYOUT_CARD);
    }

    @PostConstruct
    void init() {
        setEagerValidation(true);

        gender.addItems((Object[]) Gender.values());
        gender.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        setSavedHandler(new SavedHandler<Employee>() {

            @Override
            public void onSave(Employee entity) {
                try {
                    // make EJB call to save the entity
                    service.saveOrPersist(entity);
                    // fire save event to let other UI components know about
                    // the change
                    saveEvent.fire(entity);
                } catch (EJBException e) {
                    /*
                     * The Employee object uses optimitic locking with the 
                     * version field. Notify user the editing didn't succeed.
                     */
                    Notification.show("The customer was concurrently edited "
                            + "by someone else. Your changes were discarded.",
                            Notification.Type.ERROR_MESSAGE);
                    refrehsEvent.fire(entity);
                }
            }
        });
        setResetHandler(new ResetHandler<Employee>() {

            @Override
            public void onReset(Employee entity) {
                refrehsEvent.fire(entity);
            }
        });
        setDeleteHandler(new DeleteHandler<Employee>() {
            @Override
            public void onDelete(Employee entity) {
                service.deleteEntity(getEntity());
                deleteEvent.fire(getEntity());
            }
        });
    }

    @Override
    protected void adjustResetButtonState() {
        // always enabled in this form
        getResetButton().setEnabled(true);
        getDeleteButton().setEnabled(getEntity() != null && getEntity().isPersisted());
    }

    /* "CDI interface" to notify decoupled components. Using traditional API to
     * other componets would probably be easier in small apps, but just
     * demonstrating here how all CDI stuff is available for Vaadin apps.
     */
    @Inject
    @EmployeeEvent(EmployeeEvent.Type.SAVE)
    javax.enterprise.event.Event<Employee> saveEvent;

    @Inject
    @EmployeeEvent(EmployeeEvent.Type.REFRESH)
    javax.enterprise.event.Event<Employee> refrehsEvent;

    @Inject
    @EmployeeEvent(EmployeeEvent.Type.DELETE)
    javax.enterprise.event.Event<Employee> deleteEvent;
}
