package com.app.ecom_application.repository;

import com.app.ecom_application.model.Order;
import com.app.ecom_application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.beans.JavaBean;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

}
