package ru.vtb.code.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import wiremock.org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Slf4j
@UtilityClass
public class UtilsHelper {

    public void startWireMockServer(String url,
                                    int port,
                                    String method,
                                    int status,
                                    int fixedDelay,
                                    String fileResponse,
                                    String description,
                                    Map<String, ?> queryParams,
                                    Map<String, Map<String, String>> httpStatusResponse,
                                    Map<String, String> requestHeaders,
                                    String requestBody) throws IOException {

        var responseMap = httpStatusResponse.get(String.valueOf(status));
        var payload = String.valueOf(responseMap.get("body"));

        log.info("Start wiremock service: {}, url={}, port={}", description, url, port);
        final var config = wireMockConfig().port(port);
        WireMockServer wireMockServer = new WireMockServer(config);

        var body = StringUtils.isEmpty(payload)
            ? UtilsHelper.jsonFileToString(fileResponse)
            : payload;

        var stubRequestMatcher = request(method, urlPathMatching(url))
            .willReturn(aResponse().withBody(body));

        queryParams.forEach((key, value) -> stubRequestMatcher.withQueryParam(key, equalTo(String.valueOf(value))));
        requestHeaders.forEach((key, value) -> stubRequestMatcher.withHeader(key, equalTo(String.valueOf(value))));

        if (StringUtils.isNotEmpty(requestBody)) {
            stubRequestMatcher.withRequestBody(equalToJson(requestBody));
        }

        wireMockServer.stubFor(stubRequestMatcher
            .willReturn(aResponse()
                .withHeader("Content-Type", responseMap.get("media-type"))
                .withStatus(status)
                .withBody(body)
                .withFixedDelay(fixedDelay)
            ));

        wireMockServer.start();
    }

    String jsonFileToString(String source) throws IOException {
        try {
            File file = ResourceUtils.getFile(String.format("classpath:%s", source));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IOException("file not found");
        }
    }

}