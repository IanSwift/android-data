package io.swift.kata.androiddata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NamesRepository extends JpaRepository<Name, Long>{
}
