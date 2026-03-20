package com.example.demo.repository;

import com.example.demo.entity.DoiTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoiTraRepository extends JpaRepository<DoiTra, String> {
    List<DoiTra> findAllByOrderByNgayYeuCauDesc();
}
