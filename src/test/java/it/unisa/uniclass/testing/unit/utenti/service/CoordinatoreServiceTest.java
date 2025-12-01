package it.unisa.uniclass.testing.unit.utenti.service;

import it.unisa.uniclass.common.exceptions.AlreadyExistentUserException;
import it.unisa.uniclass.utenti.model.Coordinatore;
import it.unisa.uniclass.utenti.service.CoordinatoreService;
import it.unisa.uniclass.utenti.service.dao.CoordinatoreRemote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CoordinatoreServiceTest {

    @Mock
    private CoordinatoreRemote coordinatoreDao;

    private CoordinatoreService coordinatoreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        coordinatoreService = new CoordinatoreService(coordinatoreDao);
    }

    @Test
    void testTrovaCoordinatore_Esistente() {
        String email = "coord@unisa.it";
        Coordinatore expected = new Coordinatore();
        expected.setEmail(email);

        when(coordinatoreDao.trovaCoordinatoreEmailUniclass(email)).thenReturn(expected);

        Coordinatore result = coordinatoreService.trovaCoordinatoreEmailUniclass(email);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testAggiungiCoordinatore_Successo() throws Exception {
        Coordinatore nuovo = new Coordinatore();
        nuovo.setEmail("nuovo@unisa.it");
        nuovo.setMatricola("M123");

        when(coordinatoreDao.trovaCoordinatoreEmailUniclass(nuovo.getEmail())).thenReturn(null);
        when(coordinatoreDao.trovaCoordinatoreUniClass(nuovo.getMatricola())).thenReturn(null);

        coordinatoreService.aggiungiCoordinatore(nuovo);

        verify(coordinatoreDao).aggiungiCoordinatore(nuovo);
    }

    @Test
    void testAggiungiCoordinatore_GiaEsistente() {
        Coordinatore esistente = new Coordinatore();
        esistente.setEmail("vecchio@unisa.it");

        // Simula che l'email sia già presente
        when(coordinatoreDao.trovaCoordinatoreEmailUniclass(esistente.getEmail())).thenReturn(new Coordinatore());

        assertThrows(AlreadyExistentUserException.class, () -> {
            coordinatoreService.aggiungiCoordinatore(esistente);
        });

        verify(coordinatoreDao, never()).aggiungiCoordinatore(any());
    }
}