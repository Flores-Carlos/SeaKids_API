package com.gs.sea_kids.repo;

import com.gs.sea_kids.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepo extends JpaRepository<Cliente, Long>{
}
