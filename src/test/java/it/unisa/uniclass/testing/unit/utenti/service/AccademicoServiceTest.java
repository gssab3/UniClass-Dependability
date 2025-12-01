package it.unisa.uniclass.testing.unit.utenti.service;

import it.unisa.uniclass.utenti.model.Accademico;
import it.unisa.uniclass.utenti.service.AccademicoService;
import it.unisa.uniclass.utenti.service.dao.AccademicoRemote;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AccademicoServiceTest {

    @Mock
    private AccademicoRemote accademicoDao;

    private AccademicoService accademicoService;

    @BeforeEach
    void setUp() {
        // 1. Inizializza i Mock manualmente (Niente @ExtendWith)
        MockitoAnnotations.openMocks(this);

        // 2. Usa il costruttore per i test che abbiamo aggiunto nel refactoring
        accademicoService = new AccademicoService(accademicoDao);
    }

    @Test
    void testTrovaAccademico_Esistente() {
        String matricola = "0512100001";
        Accademico expected = new Accademico();
        expected.setMatricola(matricola);

        when(accademicoDao.trovaAccademicoUniClass(matricola)).thenReturn(expected);

        Accademico result = accademicoService.trovaAccademicoUniClass(matricola);

        assertNotNull(result);
        assertEquals(matricola, result.getMatricola());
    }

    @Test
    void testTrovaAccademico_NonTrovato_GestioneEccezione() {
        // Il service cattura NoResultException e ritorna null
        when(accademicoDao.trovaAccademicoUniClass("999")).thenThrow(new NoResultException());

        Accademico result = accademicoService.trovaAccademicoUniClass("999");

        assertNull(result, "Se il DAO lancia eccezione, il service deve ritornare null");
    }

    @Test
    void testTrovaEmailPass_Successo() {
        String email = "prof@unisa.it";
        String pass = "password123";
        Accademico acc = new Accademico();
        acc.setEmail(email);
        acc.setPassword(pass); // Simuliamo che nel DB la password sia questa

        when(accademicoDao.trovaEmailUniClass(email)).thenReturn(acc);

        Accademico result = accademicoService.trovaEmailPassUniclass(email, pass);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testTrovaEmailPass_PasswordErrata() {
        String email = "prof@unisa.it";
        Accademico acc = new Accademico();
        acc.setEmail(email);
        acc.setPassword("giusta"); // Password nel DB

        when(accademicoDao.trovaEmailUniClass(email)).thenReturn(acc);

        // Proviamo con una password diversa
        Accademico result = accademicoService.trovaEmailPassUniclass(email, "sbagliata");

        assertNull(result, "Deve ritornare null se la password non coincide");
    }

    @Test
    void testAggiungiAccademico() {
        Accademico nuovo = new Accademico();
        nuovo.setMatricola("0512109999");

        accademicoService.aggiungiAccademico(nuovo);

        // Verifica che il metodo del DAO sia stato chiamato 1 volta
        verify(accademicoDao, times(1)).aggiungiAccademico(nuovo);
    }


    @Test
    void testMetodiCRUD_Passacarte() {
        // Test trovaTutti
        accademicoService.trovaTuttiUniClass();
        verify(accademicoDao).trovaTuttiUniClass();

        // Test trovaAttivati
        accademicoService.trovaAttivati(true);
        verify(accademicoDao).trovaAttivati(true);

        // Test retrieveEmail
        accademicoService.retrieveEmail();
        verify(accademicoDao).retrieveEmail();

        // Test rimuovi
        Accademico a = new Accademico();
        accademicoService.rimuoviAccademico(a);
        verify(accademicoDao).rimuoviAccademico(a);

        // Test cambia attivazione
        accademicoService.cambiaAttivazione(a, true);
        verify(accademicoDao).cambiaAttivazione(a, true);
    }
}