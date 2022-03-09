package ru.vtb.code;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "settings")
public class Settings {

    private Map<String, ?> getAtmDetailPath;
    private Map<String, ?> getAtmNearbyDetailPath;
    private Map<String, ?> getAtmNearbyFilteredPath;
    private Map<String, ?> getAtmNearbyShortPath;

    private Map<String, ?> getCategoriesPath;
    private Map<String, ?> getOperatorInfoPath;
    private Map<String, ?> getOperatorPath;

    private Map<String, ?> getRequestMobilePaymentRequestPath;
    private Map<String, ?> getConfirmMobilePaymentRequestPath;
    private Map<String, ?> getPaymentInfoBasePath;
    private Map<String, ?> getStartMobilePaymentRequestPath;

    private final List<String> envs = new ArrayList<>();

    @Autowired
    public void setEnvs(@Value("${run.envs}") List<String> values) {
        this.envs.addAll(values);
    }

}
