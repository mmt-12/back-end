package com.memento.server.hello;

public record HelloResponse(
	Long id,
	Integer price,
	String name
) {
	public static HelloResponse from(HelloRequest request) {
		return new HelloResponse(request.id(), request.price() + 100, request.name());
	}
}
