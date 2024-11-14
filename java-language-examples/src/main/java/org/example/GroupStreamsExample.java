package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupStreamsExample {


    public static void main(String[] args) {
        groupEmployeesByDept(); // 1. single key
        /**
         * {COMP=[Employee{name='N8', role=SE, department=COMP, salary=30}, Employee{name='N9', role=SE, department=COMP, salary=30}, Employee{name='N10', role=MGR, department=COMP, salary=40}], MATH=[Employee{name='N1', role=INTERN, department=MATH, salary=10}, Employee{name='N2', role=INTERN, department=MATH, salary=10}, Employee{name='N3', role=ASE, department=MATH, salary=20}, Employee{name='N4', role=ASE, department=MATH, salary=20}, Employee{name='N5', role=SE, department=MATH, salary=30}, Employee{name='N6', role=SE, department=MATH, salary=30}, Employee{name='N7', role=MGR, department=MATH, salary=40}]}
         */
        groupEmployeeByDeptAndRole(); // 2. composite key
        /**
         * {[MATH, SE]=[Employee{name='N5', role=SE, department=MATH, salary=30}, Employee{name='N6', role=SE, department=MATH, salary=30}], [MATH, ASE]=[Employee{name='N3', role=ASE, department=MATH, salary=20}, Employee{name='N4', role=ASE, department=MATH, salary=20}], [COMP, SE]=[Employee{name='N8', role=SE, department=COMP, salary=30}, Employee{name='N9', role=SE, department=COMP, salary=30}], [MATH, INTERN]=[Employee{name='N1', role=INTERN, department=MATH, salary=10}, Employee{name='N2', role=INTERN, department=MATH, salary=10}], [COMP, MGR]=[Employee{name='N10', role=MGR, department=COMP, salary=40}], [MATH, MGR]=[Employee{name='N7', role=MGR, department=MATH, salary=40}]}
         */
        countEmployeeByDept(); // 3. groupby counting
        /**
         * {COMP=3, MATH=7}
         */
        groupEmployeeNamesByDept(); // 4. groupby mapping
        /**
         * {COMP=[N8, N9, N10], MATH=[N1, N2, N3, N4, N5, N6, N7]}
         */
        highestSalaryEmployeeInEachDept(); // 5. groupby maxBy
        /**
         * {COMP=Optional[Employee{name='N8', role=SE, department=COMP, salary=30}], MATH=Optional[Employee{name='N1', role=INTERN, department=MATH, salary=10}]}
         */
        lowestSalaryEmployeeInEachDept(); // 6. groupby minBy
        /**
         * {COMP=Optional[Employee{name='N8', role=SE, department=COMP, salary=30}], MATH=Optional[Employee{name='N1', role=INTERN, department=MATH, salary=10}]}
         */
        totalSalaryByDept(); // 7. groupby summitInt
        /**
         * {COMP=100, MATH=160}
         */
        groupByDeptThenGroupByRole(); // 8. groupby groupby
        /**
         * {COMP={SE=[Employee{name='N8', role=SE, department=COMP, salary=30}, Employee{name='N9', role=SE, department=COMP, salary=30}], MGR=[Employee{name='N10', role=MGR, department=COMP, salary=40}]}, MATH={SE=[Employee{name='N5', role=SE, department=MATH, salary=30}, Employee{name='N6', role=SE, department=MATH, salary=30}], ASE=[Employee{name='N3', role=ASE, department=MATH, salary=20}, Employee{name='N4', role=ASE, department=MATH, salary=20}], INTERN=[Employee{name='N1', role=INTERN, department=MATH, salary=10}, Employee{name='N2', role=INTERN, department=MATH, salary=10}], MGR=[Employee{name='N7', role=MGR, department=MATH, salary=40}]}}
         */
        groupByDeptThenGroupByRole1(); // 9. groupby groupby (custom class as key)
        /**
         * {COMP={MGR=[Employee{name='N10', role=MGR, department=COMP, salary=40}], SE=[Employee{name='N8', role=SE, department=COMP, salary=30}, Employee{name='N9', role=SE, department=COMP, salary=30}]}, MATH={MGR=[Employee{name='N7', role=MGR, department=MATH, salary=40}], ASE=[Employee{name='N3', role=ASE, department=MATH, salary=20}, Employee{name='N4', role=ASE, department=MATH, salary=20}], SE=[Employee{name='N5', role=SE, department=MATH, salary=30}, Employee{name='N6', role=SE, department=MATH, salary=30}], INTERN=[Employee{name='N1', role=INTERN, department=MATH, salary=10}, Employee{name='N2', role=INTERN, department=MATH, salary=10}]}}
         */
        groupByDeptWithTreeMap(); // 10. groupby treemap tolist

        keepLastOccurrance(); // 11. replace or skip in a map
    }

    private static void keepLastOccurrance() {
        List<Employee> list = getEmployeeList();
        Map<String, Employee> group = list
                .stream()
                .collect(
                        Collectors.toMap(
                                e -> e.name,
                                Function.identity(),
                                (oldValue, newValue) -> newValue,
                                LinkedHashMap::new // this line is optional
                        )
                );
        System.out.println(group);
    }

    private static void groupByDeptWithTreeMap() {
        // use Comparator.naturalOrder() or Comparator.reverseOrder()
        List<Employee> list = getEmployeeList();
//        Map<String, List<Employee>> group = list
//                .stream()
//                .collect(
//                        Collectors.groupingBy(e -> e.department.name(),
//                                () -> new TreeMap(Comparator.naturalOrder()),
//                                Collectors.toList()
//                        )
//                );
//        System.out.println(group);
    }

    private static void groupByDeptThenGroupByRole1() {
        // instead of string key we use the actual type
        List<Employee> list = getEmployeeList();
        Map<Department, Map<Role, List<Employee>>> group = list
                .stream()
                .collect(
                        Collectors.groupingBy(e -> e.department,
                                Collectors.groupingBy(e -> e.role)
                        )
                );
        System.out.println(group);
    }


    // nested group
    private static void groupByDeptThenGroupByRole() {
        List<Employee> list = getEmployeeList();
        Map<String, Map<String, List<Employee>>> group = list
                .stream()
                .collect(
                        Collectors.groupingBy(e -> e.department.name(),
                                Collectors.groupingBy(e -> e.role.name())
                        )
                );
        System.out.println(group);
    }


    private static void totalSalaryByDept() {
        List<Employee> list = getEmployeeList();
        Map<String, Integer> group = list
                .stream()
                .collect(
                        Collectors.groupingBy(e -> e.department.name(),
                                Collectors.summingInt(e -> e.salary)
                        )
                );
        System.out.println(group);
    }

    private static void lowestSalaryEmployeeInEachDept() {
        List<Employee> list = getEmployeeList();
        Map<String, Optional<Employee>> group = list
                .stream()
                .collect(
                        Collectors.groupingBy(e -> e.department.name(),
                                Collectors.minBy(Comparator.comparingInt(e -> e.salary))
                        )
                );
        System.out.println(group);
    }


    private static void highestSalaryEmployeeInEachDept() {
        List<Employee> list = getEmployeeList();
        Map<String, Optional<Employee>> group = list
                .stream()
                .collect(
                        Collectors.groupingBy(e -> e.department.name(),
                                Collectors.maxBy((e1, e2) -> {
                                    return Integer.compare(e1.salary, e2.salary);
                                })
                        )
                );
        System.out.println(group);
    }

    private static void groupEmployeeNamesByDept() {
        List<Employee> list = getEmployeeList();
        // in one dept we can have many employees.
        // we are interested for name of employees.
        // name of employee is string. so we choose list of string.
        Map<String, List<String>> group = list
                .stream()
                .collect(
                        Collectors.groupingBy(e -> e.department.name(),
                                Collectors.mapping(e -> e.name, Collectors.toList()))
                );
        System.out.println(group);
    }

    private static void countEmployeeByDept() {
        List<Employee> list = getEmployeeList();
        // for counting : count value is of type long
        Map<String, Long> group = list
                .stream()
                .collect(
                        Collectors.groupingBy(e -> e.department.name(), Collectors.counting())
                );
        System.out.println(group);
    }

    private static void groupEmployeeByDeptAndRole() {
        List<Employee> list = getEmployeeList();
        Map<List<String>, List<Employee>> group = list
                .stream()
                .collect(
                        Collectors.groupingBy(e -> Arrays.asList(e.department.name(), e.role.name()))
                );
        System.out.println(group);
    }

    private static void groupEmployeesByDept() {
        List<Employee> list = getEmployeeList();
        Map<String, List<Employee>> group = list
                .stream()
                .collect(
                        Collectors.groupingBy(e -> e.department.name())
                );
        System.out.println(group);
    }

    // -------------- set data ---------------
    private static List<Employee> getEmployeeList() {

        List<Employee> employees = new ArrayList<>();

        employees.add(createEmployee("N1", Role.INTERN, Department.MATH, 10));
        employees.add(createEmployee("N2", Role.INTERN, Department.MATH, 10));
        employees.add(createEmployee("N3", Role.ASE, Department.MATH, 20));
        employees.add(createEmployee("N4", Role.ASE, Department.MATH, 20));
        employees.add(createEmployee("N5", Role.SE, Department.MATH, 30));
        employees.add(createEmployee("N6", Role.SE, Department.MATH, 30));
        employees.add(createEmployee("N7", Role.MGR, Department.MATH, 40));
        employees.add(createEmployee("N7", Role.MGR, Department.MATH, 50));

        employees.add(createEmployee("N8", Role.SE, Department.COMP, 30));
        employees.add(createEmployee("N9", Role.SE, Department.COMP, 30));
        employees.add(createEmployee("N10", Role.MGR, Department.COMP, 40));
        employees.add(createEmployee("N10", Role.MGR, Department.COMP, 60));

        return employees;

    }

    private static Employee createEmployee(String name, Role role, Department department, int salary) {
        Employee e = new Employee(name, role, department, salary);
        return e;
    }


    private static class Employee {
        String name;
        Role role;
        Department department;
        int salary;

        public Employee(String name, Role role, Department department, int salary) {
            this.name = name;
            this.role = role;
            this.department = department;
            this.salary = salary;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "name='" + name + '\'' +
                    ", role=" + role +
                    ", department=" + department +
                    ", salary=" + salary +
                    '}';
        }
    }

    private static enum Department {
        MATH, COMP;
    }

    private static enum Role {
        MGR, SE, ASE, INTERN;
    }
}