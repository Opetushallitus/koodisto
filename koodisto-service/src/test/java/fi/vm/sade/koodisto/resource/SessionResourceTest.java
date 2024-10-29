
package fi.vm.sade.koodisto.resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SessionResourceTest {

    @Autowired
    private SessionResource resource;

    @Test
    @WithMockUser(value = "1.2.3.4.5", authorities = {"ROLE_APP_KOODISTO_CRUD_1.2.246.562.10.00000000001", "ROLE_APP_KOODISTO_CRUD"})
    public void returnsSessionMaxInactiveInterval() throws Exception {
        Integer maxInactive = 30;
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);
        when(Integer.valueOf(session.getMaxInactiveInterval())).thenReturn(maxInactive);
        assertEquals(maxInactive, Integer.valueOf(resource.maxInactiveInterval(request)));
        verify(session).getMaxInactiveInterval();
    }


}