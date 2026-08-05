/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the AAA integration is only enabled when {@code aaa.url} has a non-blank value.
 */
class AaaClientTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FeignAutoConfiguration.class))
            .withUserConfiguration(TestFeignConfiguration.class);

    @Test
    void testClientNotCreatedWhenUrlPropertyIsMissing() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(AaaClient.class);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void testClientNotCreatedWhenUrlPropertyIsBlank(String blankUrl) {
        contextRunner
                .withPropertyValues("aaa.url=" + blankUrl)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(AaaClient.class);
                });
    }

    @Test
    void testClientCreatedWhenUrlPropertyIsSet() {
        contextRunner
                .withPropertyValues("aaa.url=https://aaa.example.com")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(AaaClient.class);
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableFeignClients(basePackageClasses = AaaClient.class)
    static class TestFeignConfiguration {
    }
}


