package org.vaadin.backend;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.vaadin.backend.domain.Customer;
import org.vaadin.backend.domain.CustomerStatus;
import org.vaadin.backend.domain.Gender;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Stateless
public class CustomerService {

    @PersistenceContext(unitName = "customer-pu")
    private EntityManager entityManager;

    public void saveOrPersist(Customer entity) {
        if (entity.getId() > 0) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
    }

    public void deleteEntity(Customer entity) {
        if (entity.getId() > 0) {
            // reattach to remove
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }
    }

    public List<Customer> findAll() {
        CriteriaQuery<Customer> cq = entityManager.getCriteriaBuilder().
                createQuery(Customer.class);
        cq.select(cq.from(Customer.class));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Customer> findByName(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        filter = filter.toLowerCase();
        return entityManager.createNamedQuery("Customer.findByName",
                Customer.class)
                .setParameter("filter", filter + "%").getResultList();
    }

    /**
     * Sample data generation
     */
    public void ensureTestData() {
        if (findAll().isEmpty()) {
            final double latBase = 42.3791618;
            final double lonBase = -71.1138139;
            GeometryFactory factory = new GeometryFactory();
            final String[] names = new String[]{"Gabrielle Patel", "Brian Robinson", "Eduardo Haugen", "Koen Johansen", "Alejandro Macdonald", "Angel Karlsson", "Yahir Gustavsson", "Haiden Svensson", "Emily Stewart", "Corinne Davis", "Ryann Davis", "Yurem Jackson", "Kelly Gustavsson", "Eileen Walker", "Katelyn Martin", "Israel Carlsson", "Quinn Hansson", "Makena Smith", "Danielle Watson", "Leland Harris", "Gunner Karlsen", "Jamar Olsson", "Lara Martin", "Ann Andersson", "Remington Andersson", "Rene Carlsson", "Elvis Olsen", "Solomon Olsen", "Jaydan Jackson", "Bernard Nilsen"};
            Random r = new Random(0);
            for (String name : names) {
                String[] split = name.split(" ");
                Customer c = new Customer();
                c.setFirstName(split[0]);
                c.setLastName(split[1]);
                c.setEmail(split[0].toLowerCase() + "@" + split[1].
                        toLowerCase() + ".com");
                c.setStatus(CustomerStatus.values()[r.nextInt(CustomerStatus.
                        values().length)]);
                Calendar cal = Calendar.getInstance();
                int daysOld = 0 - r.nextInt(365 * 15 + 365 * 60);
                cal.add(Calendar.DAY_OF_MONTH, daysOld);
                c.setBirthDate(cal.getTime());

                c.setGender(r.nextDouble() < 0.7 ? Gender.Female : Gender.Male);

                double lat = latBase + 0.02 * r.nextDouble() - 0.02 * r.
                        nextDouble();
                double lon = lonBase + 0.02 * r.nextDouble() - 0.02 * r.
                        nextDouble();
                c.setLocation(factory.createPoint(new Coordinate(lon, lat)));
                saveOrPersist(c);
            }
        }
    }

    public void resetTestData() {
        if(!findAll().isEmpty()) {
            entityManager.createQuery("DELETE FROM Customer c WHERE c.id > 0").
                    executeUpdate();
        }
        ensureTestData();
    }

}
