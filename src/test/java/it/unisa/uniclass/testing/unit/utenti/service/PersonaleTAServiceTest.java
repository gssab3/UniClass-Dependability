package it.unisa.uniclass.testing.unit.utenti.service;

import it.unisa.uniclass.utenti.model.PersonaleTA;
import it.unisa.uniclass.utenti.service.PersonaleTAService;
import it.unisa.uniclass.utenti.service.dao.PersonaleTARemote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations; //

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PersonaleTAServiceTest {

    @Mock
    private PersonaleTARemote personaleTADAO;

    private PersonaleTAService personaleTAService;

    @BeforeEach
    void setUp() {
        // Inizializzazione manuale dei Mock (sostituisce @ExtendWith)
        MockitoAnnotations.openMocks(this);

        // Iniezione manuale del mock nel service
        personaleTAService = new PersonaleTAService(personaleTADAO);
    }

    @Test
    void testTrovaEmail_Esistente() {
        String email = "staff@unisa.it";
        PersonaleTA expected = new PersonaleTA();
        expected.setEmail(email);

        when(personaleTADAO.trovaEmail(email)).thenReturn(expected);

        PersonaleTA result = personaleTAService.trovaEmail(email);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testTrovaEmailPass_Successo() {
        String email = "staff@unisa.it";
        String password = "password";
        PersonaleTA expected = new PersonaleTA();

        when(personaleTADAO.trovaEmailPassword(email, password)).thenReturn(expected);

        PersonaleTA result = personaleTAService.trovaEmailPass(email, password);
        assertNotNull(result);
    }

    @Test
    void testTrovaEmailPass_Fallimento_EccezioneGestita() {
        when(personaleTADAO.trovaEmailPassword(anyString(), anyString())).thenThrow(new RuntimeException("Errore DB"));

        PersonaleTA result = personaleTAService.trovaEmailPass("mail", "pass");
        assertNull(result, "Il service dovrebbe gestire l'eccezione ritornando null");
    }

    @Test
    void testAggiungiPersonaleTA() {
        PersonaleTA nuovo = new PersonaleTA();
        personaleTAService.aggiungiPersonaleTA(nuovo);
        verify(personaleTADAO).aggiungiPersonale(nuovo);
    }


    @Test
    void testMetodiMancanti() {
        // Test trova per ID
        personaleTAService.trovaPersonale(1L);
        verify(personaleTADAO).trovaPersonale(1L);

        // Test trova tutti
        personaleTAService.trovaTutti();
        verify(personaleTADAO).trovaTutti();

        // Test rimuovi
        PersonaleTA p = new PersonaleTA();
        personaleTAService.rimuoviPersonaleTA(p);
        verify(personaleTADAO).rimuoviPersonale(p);
    }
}