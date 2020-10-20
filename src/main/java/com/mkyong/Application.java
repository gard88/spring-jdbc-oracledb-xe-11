package com.mkyong;

import com.mkyong.dao.CustomerDao;
import com.mkyong.dao.CustomerRepository;
import com.mkyong.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.System.exit;

//for jsr310 java 8 java.time.*
//@EntityScan(
//        basePackageClasses = { SpringBootConsoleApplication.class, Jsr310JpaConverters.class }
//)
@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    DataSource dataSource;

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerDao customerDao;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void after() {


    }

    @Transactional
    @Override
    public void run(String... args) throws Exception {

        System.out.println("Save customers");
        Set<Customer> customers = createCustomer();
        for(Customer customer: customers) {
            customerRepository.save(customer);
        }

        System.out.println("DATASOURCE = " + dataSource);

        System.out.println("\n1.findAll()...");
        Iterable<Customer> loadedCustomers = customerRepository.findAll();
        String skipXIds = StreamSupport.stream(loadedCustomers.spliterator(), false)
            .skip(500)
            .map(customer -> String.valueOf(customer.getId().intValue()))
            .collect(Collectors.joining(","));


//        List<Customer> lstXCustomers = customerRepository.getCustomersIn(skipXIds);
        List<Customer> lstXCustomers = customerDao.getCustomersIn(skipXIds);
        //List<Long> customerId = customerDao.getCustomerIdsIn(skipXIds);
        exit(0);
    }

    Set<Customer> createCustomer() {
        Set<Customer> customerSet = new HashSet<>();
        for(int i = 0; i < 2000; i++) {
            customerSet.add(new Customer("John", "john@google.com", new Date()));
        }
        return customerSet;

    }





}
