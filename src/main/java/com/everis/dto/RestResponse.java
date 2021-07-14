package com.everis.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class RestResponse {
	private Object data;
	private String errorMessage;
	private Map<String, Object> errorDetail;
}
