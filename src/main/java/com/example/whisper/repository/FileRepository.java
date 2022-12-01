package com.example.whisper.repository;

import com.example.whisper.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

    @Query(value = "select file from File file where file.message.id = :messageId and file.number = :number")
    Optional<File> findByMessageIdAndNumber(@Param("messageId") UUID messageId, @Param("number") String number);
}
