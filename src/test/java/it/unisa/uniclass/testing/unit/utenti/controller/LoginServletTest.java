package it.unisa.uniclass.utenti.controller;

import it.unisa.uniclass.utenti.model.Accademico;
import it.unisa.uniclass.utenti.model.PersonaleTA;
import it.unisa.uniclass.utenti.service.AccademicoService;
import it.unisa.uniclass.utenti.service.PersonaleTAService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LoginServletTest {

    // sottoclasse che overridea i metodi incriminati e rende pubblico doGet
    static class TestableLoginServlet extends LoginServlet {
        @Override
        protected AccademicoService getAccademicoService() {
            return Mockito.mock(AccademicoService.class);
        }

        @Override
        protected PersonaleTAService getPersonaleTAService() {
            return Mockito.mock(PersonaleTAService.class);
        }

        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            super.doGet(req, resp);
        }
    }

    private LoginServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private AccademicoService accademicoService;
    private PersonaleTAService personaleTAService;

    @BeforeEach
    void setUp() {
        servlet = new LoginServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        accademicoService = mock(AccademicoService.class);
        personaleTAService = mock(PersonaleTAService.class);

        servlet.setAccademicoService(accademicoService);
        servlet.setPersonaleTAService(personaleTAService);

        when(request.getContextPath()).thenReturn("/ctx");
        when(request.getSession(true)).thenReturn(session);
        when(request.getParameter("email")).thenReturn("test@unisa.it");
        when(request.getParameter("password")).thenReturn("pwd");
    }

    @Test
    void testNoUserFound() throws IOException {
        when(accademicoService.trovaEmailPassUniclass(anyString(), anyString())).thenReturn(null);
        when(personaleTAService.trovaEmailPass(anyString(), anyString())).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/ctx/Login.jsp?action=error");
    }

    @Test
    void testAccademicoAttivato() throws IOException {
        Accademico acc = mock(Accademico.class);
        when(acc.isAttivato()).thenReturn(true);
        when(accademicoService.trovaEmailPassUniclass(anyString(), anyString())).thenReturn(acc);
        when(personaleTAService.trovaEmailPass(anyString(), anyString())).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("currentSessionUser"), eq(acc));
        verify(response).sendRedirect("/ctx/Home");
    }

    @Test
    void testAccademicoNonAttivatoPasswordNull() throws IOException {
        Accademico acc = mock(Accademico.class);
        when(acc.isAttivato()).thenReturn(false);
        when(acc.getPassword()).thenReturn(null);
        when(accademicoService.trovaEmailPassUniclass(anyString(), anyString())).thenReturn(acc);
        when(personaleTAService.trovaEmailPass(anyString(), anyString())).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/ctx/Login.jsp?action=notactivated");
    }

    @Test
    void testPersonaleTAFound() throws IOException {
        PersonaleTA ta = mock(PersonaleTA.class);
        when(accademicoService.trovaEmailPassUniclass(anyString(), anyString())).thenReturn(null);
        when(personaleTAService.trovaEmailPass(anyString(), anyString())).thenReturn(ta);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("currentSessionUser"), eq(ta));
        verify(response).sendRedirect("/ctx/Home");
    }

    @Test
    void testDoGetDelegatesToDoPost() throws Exception {
        TestableLoginServlet servlet = new TestableLoginServlet();
        servlet.setAccademicoService(accademicoService);
        servlet.setPersonaleTAService(personaleTAService);

        servlet.doGet(request, response);

        verify(response, atLeastOnce()).sendRedirect(anyString());
    }

    @Test
    void testGetAccademicoService() {
        TestableLoginServlet servlet = new TestableLoginServlet();
        assertNotNull(servlet.getAccademicoService());
    }

    @Test
    void testGetPersonaleTAService() {
        TestableLoginServlet servlet = new TestableLoginServlet();
        assertNotNull(servlet.getPersonaleTAService());
    }

    @Test
    void testGetAccademicoServiceOriginal() {
        try (MockedConstruction<AccademicoService> mocked =
                     Mockito.mockConstruction(AccademicoService.class)) {
            LoginServlet s = new LoginServlet();
            AccademicoService svc = s.getAccademicoService(); // chiama il metodo originale
            assertNotNull(svc);
            assertEquals(1, mocked.constructed().size());
        }
    }

    @Test
    void testGetPersonaleTAServiceOriginal() {
        try (MockedConstruction<PersonaleTAService> mocked =
                     Mockito.mockConstruction(PersonaleTAService.class)) {
            LoginServlet s = new LoginServlet();
            PersonaleTAService svc = s.getPersonaleTAService(); // chiama il metodo originale
            assertNotNull(svc);
            assertEquals(1, mocked.constructed().size());
        }
    }

    @Test
    void testAccademicoNonAttivatoPasswordNonNulla() throws IOException {
        Accademico acc = mock(Accademico.class);
        when(acc.isAttivato()).thenReturn(false);
        when(acc.getPassword()).thenReturn("hashedpwd");
        when(accademicoService.trovaEmailPassUniclass(anyString(), anyString())).thenReturn(acc);
        when(personaleTAService.trovaEmailPass(anyString(), anyString())).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/ctx/Login.jsp?action=error");
    }


    @Test
    void testIstanziaServiziSeNull() throws IOException {
        when(request.getContextPath()).thenReturn("/ctx");
        when(request.getSession(true)).thenReturn(session);
        when(request.getParameter("email")).thenReturn("test@unisa.it");
        when(request.getParameter("password")).thenReturn("pwd");

        try (MockedConstruction<AccademicoService> mockedAcc = mockConstruction(AccademicoService.class,
                (mock, context) -> when(mock.trovaEmailPassUniclass(anyString(), anyString())).thenReturn(null));
             MockedConstruction<PersonaleTAService> mockedTA = mockConstruction(PersonaleTAService.class,
                     (mock, context) -> when(mock.trovaEmailPass(anyString(), anyString())).thenReturn(null))) {

            LoginServlet s = new LoginServlet(); // non settiamo i service
            s.doPost(request, response);

            verify(response).sendRedirect("/ctx/Login.jsp?action=error");
            assertEquals(1, mockedAcc.constructed().size());
            assertEquals(1, mockedTA.constructed().size());
        }
    }
    @Test
    void testCatchIOException() {
        when(request.getContextPath()).thenReturn("/ctx");
        when(request.getParameter("email")).thenReturn("test@unisa.it");
        when(request.getParameter("password")).thenReturn("pwd");

        // mock dei service che restituiscono null
        try (MockedConstruction<AccademicoService> mockedAcc = mockConstruction(AccademicoService.class,
                (mock, context) -> when(mock.trovaEmailPassUniclass(anyString(), anyString())).thenReturn(null));
             MockedConstruction<PersonaleTAService> mockedTA = mockConstruction(PersonaleTAService.class,
                     (mock, context) -> when(mock.trovaEmailPass(anyString(), anyString())).thenReturn(null))) {

            LoginServlet s = new LoginServlet();

            // forza IOException
            try {
                doThrow(new IOException("Errore simulato")).when(response).sendRedirect(anyString());
                assertThrows(RuntimeException.class, () -> s.doPost(request, response));
            } catch (IOException e) {
                fail("Non doveva propagare IOException");
            }
        }
    }


}
