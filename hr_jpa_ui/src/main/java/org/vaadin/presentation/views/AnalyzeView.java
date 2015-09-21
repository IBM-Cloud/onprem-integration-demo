package org.vaadin.presentation.views;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import model.Employee;
import model.Salary;

import org.vaadin.backend.CustomerService;
import org.vaadin.backend.domain.Customer;
import org.vaadin.backend.domain.CustomerStatus;
import org.vaadin.backend.domain.Gender;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.presentation.ScreenSize;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MMarginInfo;
import org.vaadin.viritin.layouts.MVerticalLayout;

import backend.EmployeeService;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.PlotOptionsFunnel;
import com.vaadin.addon.charts.model.PlotOptionsPie;
import com.vaadin.addon.charts.model.Stacking;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;

/**
 * An example view that just make some simple analysis for the data and displays
 * it in various charts.
 */
@CDIView("analyze")
@ViewMenuItem(icon = FontAwesome.BAR_CHART_O, order = 1)
public class AnalyzeView extends MVerticalLayout implements View {

    @Inject
    private EmployeeService service;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        removeAllComponents();

        setMargin(new MMarginInfo(false, true));
        add(new Header("Employee analysis").setHeaderLevel(2));

        List<Employee> employeeData = service.findAll();
        add(ageDistribution(employeeData));
        
        add(salaryDistribution(employeeData));
        //final Component funnel = createStatusFunnel(employeeData);
        final Component gender = genderDistribution(employeeData);
        //if (ScreenSize.getScreenSize() == ScreenSize.SMALL) {
            //addComponents(funnel, gender);
        	addComponents(gender);
//        } else {
//            //addComponent(new MHorizontalLayout(funnel, gender).withFullWidth());
//        }

    }

    private Component genderDistribution(List<Employee> employeeData) {
        int women = 0, men = 0;
        for (Employee c : employeeData) {
            if (c.getGender().equals("F")) {
                women++;
            } else {
                men++;
            }
        }
        Chart chart = getBasicChart(ChartType.PIE);

        Configuration conf = chart.getConfiguration();

        PlotOptionsPie plotOptions = new PlotOptionsPie();
        Labels dataLabels = new Labels();
        dataLabels.setEnabled(true);
        dataLabels.setFormat("{point.name}: {percentage:.0f}%");
        plotOptions.setDataLabels(dataLabels);
        conf.setPlotOptions(plotOptions);

        final DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("Men", men));
        series.add(new DataSeriesItem("Women", women));
        conf.setSeries(series);
        return wrapInPanel(chart, "Gender");
    }


    private static Panel wrapInPanel(Chart chart, String caption) {
        Panel panel = new Panel(caption, chart);
        panel.setHeight("300px");
        chart.setSizeFull();
        return panel;
    }

    private enum AgeGroup {

        Children(0, 15), Young(15, 30), MiddleAged(30, 60), Old(60, 100);

        private final int min;
        private final int max;

        AgeGroup(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public static AgeGroup getAgeGroup(Date birthDate) {
            int age = new Date().getYear() - birthDate.getYear();
            for (AgeGroup g : AgeGroup.values()) {
                if (age <= g.max && age > g.min) {
                    return g;
                }
            }
            return Old;
        }
    }
    
    private enum SalaryGroup {

        Fiftys(0, 60), Sixtys(60, 70), Seventies(70, 80), Eighties(80, 90), Nineties(90,100), Rich(100,1000000);

        private final int min;
        private final int max;

        SalaryGroup(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public static SalaryGroup getSalaryGroup(Salary s) {
            int salary = s.getSalary() / 1000;
            for (SalaryGroup g : SalaryGroup.values()) {
                if (salary <= g.max && salary > g.min) {
                    return g;
                }
            }
            return Rich;
        }
    }

    private Chart getBasicChart(ChartType type) {
        Chart chart = new Chart(type);
        // title from panel
        chart.getConfiguration().setTitle("");
        return chart;
    }
    
    private Component salaryDistribution(List<Employee> employeeData) {
        Integer[] menValues = new Integer[SalaryGroup.values().length];
        Integer[] womenValues = new Integer[SalaryGroup.values().length];
        for (int i = 0; i < SalaryGroup.values().length; i++) {
            menValues[i] = 0;
            womenValues[i] = 0;
        }
        
        for (Employee c : employeeData) {
        	if (c.getSalaries() != null && c.getSalaries().size()==1) {
            	SalaryGroup g = SalaryGroup.getSalaryGroup(c.getSalaries().get(0));
                if (c.getGender().equals("F")) {
                    womenValues[g.ordinal()]++;
                } else {
                    menValues[g.ordinal()]++;
                }
            }
        }
        
        Chart chart = getBasicChart(ChartType.COLUMN);
        
        Configuration conf = chart.getConfiguration();

        XAxis xAxis = new XAxis();
        String[] names = new String[SalaryGroup.values().length];
        for (SalaryGroup g : SalaryGroup.values()) {
            names[g.ordinal()] = String.format("%s-%s", g.min,
                    g.max);
        }
        xAxis.setCategories(names);
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("");

        Legend legend = new Legend();
        legend.setHorizontalAlign(HorizontalAlign.RIGHT);
        legend.setFloating(true);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(-5);
        legend.setY(5);
        conf.setLegend(legend);

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setStacking(Stacking.NORMAL);
        conf.setPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Men", menValues));
        conf.addSeries(new ListSeries("Women", womenValues));
        
        return wrapInPanel(chart, "Salary distribution");

    }

    private Component ageDistribution(List<Employee> employeeData) {
        Integer[] menValues = new Integer[AgeGroup.values().length];
        Integer[] womenValues = new Integer[AgeGroup.values().length];
        for (int i = 0; i < AgeGroup.values().length; i++) {
            menValues[i] = 0;
            womenValues[i] = 0;
        }
        for (Employee c : employeeData) {
            if (c.getBirthDate() != null) {
                AgeGroup g = AgeGroup.getAgeGroup(c.getBirthDate());
                if (c.getGender().equals("F")) {
                    womenValues[g.ordinal()]++;
                } else {
                    menValues[g.ordinal()]++;
                }
            }
        }

        Chart chart = getBasicChart(ChartType.COLUMN);

        Configuration conf = chart.getConfiguration();

        XAxis xAxis = new XAxis();
        String[] names = new String[AgeGroup.values().length];
        for (AgeGroup g : AgeGroup.values()) {
            names[g.ordinal()] = String.format("%s-%s", g.min,
                    g.max);
        }
        xAxis.setCategories(names);
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("");

        Legend legend = new Legend();
        legend.setHorizontalAlign(HorizontalAlign.RIGHT);
        legend.setFloating(true);
        legend.setVerticalAlign(VerticalAlign.TOP);
        legend.setX(-5);
        legend.setY(5);
        conf.setLegend(legend);

        PlotOptionsColumn plotOptions = new PlotOptionsColumn();
        plotOptions.setStacking(Stacking.NORMAL);
        conf.setPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Men", menValues));
        conf.addSeries(new ListSeries("Women", womenValues));

        return wrapInPanel(chart, "Age distribution");

    }

//    private Component createStatusFunnel(List<Employee> employeeData) {
//        int[] values = new int[CustomerStatus.values().length];
//        for (Customer c : employeeData) {
//            if (c.getStatus() != null) {
//                values[c.getStatus().ordinal()]++;
//            }
//        }
//        Chart chart = getBasicChart(ChartType.FUNNEL);
//        DataSeries dataSeries = new DataSeries();
//        dataSeries.add(new DataSeriesItem("Imported lead",
//                values[CustomerStatus.ImportedLead.ordinal()]));
//        dataSeries.add(new DataSeriesItem("Not contacted",
//                values[CustomerStatus.NotContacted.ordinal()]));
//        dataSeries.add(new DataSeriesItem("Contacted",
//                values[CustomerStatus.Contacted.ordinal()]));
//        dataSeries.add(new DataSeriesItem("Customer",
//                values[CustomerStatus.Customer.ordinal()]));
//
//        Configuration conf = chart.getConfiguration();
//        conf.getChart().setMarginRight(75);
//
//        PlotOptionsFunnel options = new PlotOptionsFunnel();
//        options.setNeckWidthPercentage(30);
//        options.setNeckHeightPercentage(30);
//
//        options.setWidthPercentage(70);
//
//        dataSeries.setPlotOptions(options);
//        conf.addSeries(dataSeries);
//
//        return wrapInPanel(chart, "Sales funnel");
//    }
}
