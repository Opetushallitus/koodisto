package fi.vm.sade.koodisto.service.koodisto.rest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = "classpath:spring/test-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SessionResourceIT {

    @Autowired
    private SessionResource resource;

    @Test
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
