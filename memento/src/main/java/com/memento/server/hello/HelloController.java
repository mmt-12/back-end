package com.memento.server.hello;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {

	@PostMapping
	public ResponseEntity<HelloResponse> hello(@Valid @RequestBody HelloRequest request){
		return ResponseEntity.ok(HelloResponse.from(request));
	}
}
