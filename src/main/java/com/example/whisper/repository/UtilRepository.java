package com.example.whisper.repository;

import com.example.whisper.entity.Utility;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilRepository extends CrudRepository<Utility, String> {

}