package edu.yu.cs.com.project.department;

import edu.yu.cs.com.project.people.Employee;

import java.util.ArrayList;

public class Department {

    private String department;
    private Employee[] employees;
    private Employee manager;
    private double deparmentSalary;

    /**
     * Constructor for Department
     * param department should be the name of the department
     * param employees takes in an array of Employee, who work in this department
     * */
    public Department(String department, Employee[] employees){
        this.department = department;
        this.employees = employees;
    }

    public void setDeparmentSalary(double deparmentSalary) {
        this.deparmentSalary = deparmentSalary;
    }

    public ArrayList<Employee> getWhoIsOn(){
        ArrayList<Employee> results = new ArrayList<>();
        for(Employee e : employees){
            if(e.isOn()){
                results.add(e);
            }
        }
        return results;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }
}
