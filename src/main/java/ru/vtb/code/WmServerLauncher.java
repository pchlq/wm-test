package ru.vtb.code;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.vtb.code.utils.UtilsHelper;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


@Component
@Getter
public class WmServerLauncher {

	@Autowired
	private Settings settings;

	@PostConstruct
	void stubRequest() throws IOException, InvocationTargetException, IllegalAccessException {

		Method[] methods = settings.getClass().getDeclaredMethods();

		for (Method method : methods) {
			if (method.getName().startsWith("getGet")) {
				Map<String, ?> env = (Map<String, ?>) method.invoke(settings);
				if (settings.getEnvs().contains(env.get("name"))) {

					Optional<Object> optionalRequestHeadersMap = Optional.ofNullable(env.get("request-headers"));
					Map<String, String> requestHeadersMap = new HashMap<>();
					optionalRequestHeadersMap.ifPresent(o -> requestHeadersMap.putAll((Map<String, String>) o));

					Optional<Object> optionalQueryParamsMap = Optional.ofNullable(env.get("query-params"));
					Map<String, String> queryParamsMap = new HashMap<>();
					optionalQueryParamsMap.ifPresent(o -> queryParamsMap.putAll((Map<String, String>) o));

					String requestBody = Optional.ofNullable((String) env.get("request-body")).orElse("");

					UtilsHelper.startWireMockServer(
						(String) env.get("url"),
						(int) env.get("port"),
						(String) env.get("method"),
						(int) env.get("httpStatus"),
						(int) env.get("fixedDelay"),
						(String) env.get("file-response"),
						(String) env.get("description"),
						queryParamsMap,
						(Map<String, Map<String, String>>) env.get("httpStatus-response"),
						requestHeadersMap,
						requestBody);
				}
			}
		}

	}

}
