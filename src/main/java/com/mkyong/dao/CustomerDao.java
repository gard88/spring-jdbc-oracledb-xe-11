package com.mkyong.dao;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.mkyong.model.Customer;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerDao
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Customer> getCustomersIn(String ids) {
        final String sql = "SELECT * FROM CUSTOMER c WHERE c.ID IN (" + ids + ")";   // Er akkurat som i fobid-backend og gir "ORA-01795 Maximum number...."-feilen
        //final String sql = union("CUSTOMER", ids);                                       // Denne gir ingen feilmeldinger.
        return jdbcTemplate.query(sql, ((resultSet, i) -> {
            return new Customer(
                resultSet.getLong("ID"),
                resultSet.getString("NAME"),
                resultSet.getString("EMAIL"),
                resultSet.getDate("CREATED_DATE")
            );
        }));
    }

    String union(String table, String ids) {
        StringJoiner sj = new StringJoiner(" UNION ");
        List<List<String>> part = ListUtils.partition(Arrays.asList(ids.split(",")), 999);
        for(List<String> partition: part) {
            sj.add("SELECT * FROM " + table + " WHERE ID IN (" + partition.stream().collect(Collectors.joining(",")) + ")");
        }
        return sj.toString();
    }
}
