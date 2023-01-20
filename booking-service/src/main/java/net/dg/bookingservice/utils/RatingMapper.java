package net.dg.bookingservice.utils;

import net.dg.bookingservice.dto.Rating;

import java.util.List;

public class RatingMapper {


    public static Rating map(List<Rating> ratings) {
        Customer customer = new Customer();
        for (Customer c : customers) {
            if (c.getAccounts() != null) customer.setAccounts(c.getAccounts());
            if (c.getAge() != 0) customer.setAge(c.getAge());
            if (c.getFirstName() != null) customer.setFirstName(c.getFirstName());
            if (c.getLastName() != null) customer.setFirstName(c.getLastName());
        }
        return customer;
    }
}
