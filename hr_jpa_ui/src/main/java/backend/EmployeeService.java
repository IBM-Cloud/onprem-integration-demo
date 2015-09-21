package backend;


import com.vividsolutions.jts.geom.GeometryFactory;

import model.Employee;
import model.Salary;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Stateless
public class EmployeeService {

    @PersistenceContext(unitName = "customer-pu")
    private EntityManager entityManager;

    public void saveOrPersist(Employee entity) {
        if (entity.getEmpNo() > 0) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
    }

    public void deleteEntity(Employee entity) {
        if (entity.getEmpNo() > 0) {
            // reattach to remove
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }
    }

    public List<Employee> findAll() {
        CriteriaQuery<Employee> cq = entityManager.getCriteriaBuilder().
                createQuery(Employee.class);
        cq.select(cq.from(Employee.class));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Salary> findAllSalaries() {
        CriteriaQuery<Salary> cq = entityManager.getCriteriaBuilder().
                createQuery(Salary.class);
        cq.select(cq.from(Salary.class));
        return entityManager.createQuery(cq).getResultList();
    }
    
    public List<Employee> findByName(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        filter = filter.toLowerCase();
        return entityManager.createNamedQuery("Employee.findByName",
                Employee.class)
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
                Employee c = new Employee();
                c.setFirstName(split[0]);
                c.setLastName(split[1]);
                Calendar cal = Calendar.getInstance();
                int daysOld = 0 - r.nextInt(365 * 15 + 365 * 60);
                cal.add(Calendar.DAY_OF_MONTH, daysOld);
                c.setBirthDate(cal.getTime());



                saveOrPersist(c);
            }
        }
    }

    public void resetTestData() {
        if(!findAll().isEmpty()) {
            entityManager.createQuery("DELETE FROM Employee c WHERE c.id > 0").
                    executeUpdate();
        }
        ensureTestData();
    }

}
