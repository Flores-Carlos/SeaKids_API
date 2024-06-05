package com.gs.sea_kids.repo;

import com.gs.sea_kids.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepo extends JpaRepository<Login, Long>{
}
