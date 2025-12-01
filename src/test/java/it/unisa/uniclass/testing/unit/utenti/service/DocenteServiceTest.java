package it.unisa.uniclass.testing.unit.utenti.service;

import it.unisa.uniclass.common.exceptions.IncorrectUserSpecification;
import it.unisa.uniclass.utenti.model.Docente;
import it.unisa.uniclass.utenti.service.DocenteService;
import it.unisa.uniclass.utenti.service.dao.DocenteRemote;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DocenteServiceTest {

    @Mock
    private DocenteRemote docenteDao;

    private DocenteService docenteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        docenteService = new DocenteService(docenteDao);
    }

    @Test
    void testTrovaDocente_Esistente() {
        String matricola = "0512100001";
        Docente expected = new Docente();
        expected.setMatricola(matricola);

        when(docenteDao.trovaDocenteUniClass(matricola)).thenReturn(expected);

        Docente result = docenteService.trovaDocenteUniClass(matricola);
        assertNotNull(result);
        assertEquals(matricola, result.getMatricola());
    }

    @Test
    void testTrovaDocente_NonTrovato() {
        when(docenteDao.trovaDocenteUniClass("999")).thenThrow(new NoResultException());

        Docente result = docenteService.trovaDocenteUniClass("999");
        assertNull(result);
    }

    @Test
    void testAggiungiDocente_Successo() throws Exception {
        Docente nuovo = new Docente();
        nuovo.setEmail("prof.nuovo@unisa.it");
        nuovo.setMatricola("0512199999");

        // Simula che non esista nessuno con questa email o matricola
        when(docenteDao.trovaEmailUniClass(nuovo.getEmail())).thenReturn(null);
        when(docenteDao.trovaDocenteUniClass(nuovo.getMatricola())).thenReturn(null);

        docenteService.aggiungiDocente(nuovo);

        verify(docenteDao).aggiungiDocente(nuovo);
    }

    @Test
    void testAggiungiDocente_ConflittoDati() {
        // Scenario: Cerco di aggiungere un docente, ma la sua email appartiene già a Tizio
        // e la sua matricola appartiene a Caio. Il sistema deve bloccarlo.
        Docente input = new Docente();
        input.setEmail("prof@unisa.it");
        input.setMatricola("0512100001");

        Docente tizio = new Docente(); // Ha la mail
        Docente caio = new Docente();  // Ha la matricola

        when(docenteDao.trovaEmailUniClass(input.getEmail())).thenReturn(tizio);
        when(docenteDao.trovaDocenteUniClass(input.getMatricola())).thenReturn(caio);

        // Verifica che lanci l'eccezione specifica
        assertThrows(IncorrectUserSpecification.class, () -> {
            docenteService.aggiungiDocente(input);
        });

        verify(docenteDao, never()).aggiungiDocente(any());
    }
}