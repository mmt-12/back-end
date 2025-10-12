package com.memento.server.api.controller.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.memento.server.api.controller.health.dto.HealthResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HealthController {
  
	@GetMapping("/api/v1/health")
  public ResponseEntity<HealthResponse> healthCheck() {
    return ResponseEntity.ok(HealthResponse.builder()
      .status("OK")
      .build());
  }

}
