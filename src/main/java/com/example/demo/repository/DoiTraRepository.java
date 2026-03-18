package com.example.demo.repository;

import com.example.demo.entity.DoiTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoiTraRepository extends JpaRepository<DoiTra, String> {
}
