package com.nogetfedt.kea.repository;

import com.nogetfedt.kea.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Integer>
{
//can add more complex codes here, which the crud doesnt provide


}
