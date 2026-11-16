/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.unit.DataSize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormPostSizeFilterTest {

    static final int MAX = 1000;
    final FormPostSizeFilter filter = new FormPostSizeFilter(DataSize.ofBytes(MAX));

    @Test
    void tooLargeConfirmRedirectsWithSize() throws Exception {
        MockHttpServletResponse response = run("POST", FormPostSizeFilter.CONFIRM_PATH, MAX + 1, false);
        assertEquals("/tak-web/bestallning?tooLarge=1001", response.getRedirectedUrl());
    }

    @Test
    void tooLargeRestCreateReturns413() throws Exception {
        MockHttpServletResponse response = run("POST", FormPostSizeFilter.REST_CREATE_PATH, MAX + 1, false);
        assertEquals(413, response.getStatus());
        assertTrue(response.getContentAsString().startsWith("JsonBestallning ERROR::: Beställningen är för stor"));
    }

    @Test
    void postWithinLimitPassesThrough() throws Exception {
        run("POST", FormPostSizeFilter.CONFIRM_PATH, MAX, true);
    }

    @Test
    void otherPathsAndMethodsAreNotChecked() throws Exception {
        run("POST", "/vagval/create", MAX + 1, true);
        run("GET", FormPostSizeFilter.CONFIRM_PATH, MAX + 1, true);
    }

    @Test
    void messageUsesSwedishMegabytes() {
        assertEquals("Beställningen är för stor (6,2 MB). Maximal storlek är 5,0 MB.",
                FormPostSizeFilter.tooLargeMessage(6_500_000, DataSize.ofMegabytes(5).toBytes()));
    }

    private MockHttpServletResponse run(String method, String path, int size, boolean expectPassed) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, "/tak-web" + path);
        request.setContextPath("/tak-web");
        request.setServletPath(path);
        request.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        request.setContent(new byte[size]);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        if (expectPassed) {
            assertNotNull(chain.getRequest(), "request should pass the filter");
        } else {
            assertNull(chain.getRequest(), "request should be stopped by the filter");
        }
        return response;
    }
}
