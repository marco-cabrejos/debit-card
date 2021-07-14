package com.everis.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import com.everis.dto.RestResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
@Slf4j
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

	public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, Resources resourceProperties,
			ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
		super(errorAttributes, resourceProperties, applicationContext);
		this.setMessageWriters(configurer.getWriters());
	}

	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
	}

	private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		Map<String, Object> errorGeneral = getErrorAttributes(request, ErrorAttributeOptions.defaults());
		Map<String, Object> mapException = new HashMap<>();
		System.out.println(getError(request).toString());

		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		String statusCode = String.valueOf(errorGeneral.get("status"));

		switch (statusCode) {
		case "500":
			mapException.put("code", "500");
			mapException.put("excepcion", "Error general del backend");
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			break;
		case "400":
			Map<String, Object> mapAtributos = request.exchange().getAttributes();
			try {
				Exception exception = (Exception) mapAtributos
						.get("org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR");
				if (exception instanceof WebExchangeBindException) {
					WebExchangeBindException valorMapError = (WebExchangeBindException) mapAtributos
							.get("org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR");
					BindingResult result = valorMapError.getBindingResult();
					List<ObjectError> listaErrores = result.getAllErrors();
					List<String> mensajesError = listaErrores.stream().map(er -> er.getDefaultMessage())
							.collect(Collectors.toList());
					mapException.put("mensaje", mensajesError);
				} else if (exception instanceof ResponseStatusException) {
					mapException.put("mensaje", ((ResponseStatusException) exception).getReason());
				}
				mapException.put("code", "400");
				mapException.put("excepcion", "Peticion incorrecta");
				httpStatus = HttpStatus.BAD_REQUEST;
			} catch (Exception e) {
				mapException.put("error", "500");
				mapException.put("excepcion", "Error general del backend");
				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				log.error("",e);
			}
			break;
		default:
			mapException.put("code", "900");
			mapException.put("excepcion", errorGeneral.get("error"));
			httpStatus = HttpStatus.CONFLICT;
			break;
		}

		RestResponse rr = RestResponse.builder().errorDetail(mapException).build();

		return ServerResponse
				.status(httpStatus)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(rr));
	}

}
