package com.project.pcc.repository;

import com.project.pcc.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository  extends JpaRepository<Request, Long> {
}
