package org.example.btg.controller.dto;

public record PaginationResponse(Integer page, Integer pageSize, Integer totalElements, Integer totalPages) {
}
