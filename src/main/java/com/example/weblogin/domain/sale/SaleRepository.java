package com.example.weblogin.domain.sale;

import com.example.weblogin.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Integer> {
    List<Sale> findAll();
    List<Sale> findSalesById(int id);
    Sale findBySellerId(int id);
}
