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
        final String sql = "SELECT * FROM CUSTOMER AS c WHERE c.ID IN (" + ids + ")";
        final String sqlGen = generateTupledQuery("CUSTOMER", ids);
        //String u = union("CUSTOMER", ids);
        return jdbcTemplate.query(sqlGen, ((resultSet, i) -> {
            return new Customer(
                resultSet.getLong("ID"),
                resultSet.getString("NAME"),
                resultSet.getString("EMAIL"),
                resultSet.getDate("CREATED_DATE")
            );
        }));
    }

    public List<Long> getCustomerIdsIn(String ids) {
        final String sql = "SELECT * FROM CUSTOMER AS c WHERE c.ID IN (" + ids + ")";
        final String sqlGen = ge("CUSTOMER", ids);
        return jdbcTemplate.queryForList(sqlGen, Long.class);
    }

    String union(String table, String ids) {
        StringJoiner sj = new StringJoiner(" UNION ");
        List<List<String>> part = ListUtils.partition(Arrays.asList(ids.split(",")), 999);
        for(List<String> partition: part) {
            sj.add("SELECT * FROM " + table + " WHERE ID IN (" + partition.stream().collect(Collectors.joining(",")) + ")");
        }
        return sj.toString();
    }

    String generateTupledQuery(String table, String ids) {
        String tupleIdList = Arrays.stream(ids.split(",")).map(id -> "(" + id + ", 0)").collect(Collectors.joining(","));
        return "SELECT * FROM " + table + " WHERE (ID, 0) IN (" + tupleIdList + ")";
    }

    String ge(String table, String ids) {
        String tupleIdList = Arrays.stream(ids.split(",")).map(id -> "(" + id + ", 0)").collect(Collectors.joining(","));
        return "SELECT ID FROM " + table + " WHERE (ID, 0) IN (" + tupleIdList + ")";
    }
}
