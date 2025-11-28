package it.unisa.uniclass.testing.unit;

import it.unisa.uniclass.common.exceptions.AuthenticationException;
import it.unisa.uniclass.utenti.model.Accademico;
import it.unisa.uniclass.utenti.model.PersonaleTA;
import it.unisa.uniclass.utenti.model.Utente;
import it.unisa.uniclass.utenti.service.AccademicoService;
import it.unisa.uniclass.utenti.service.PersonaleTAService;
import it.unisa.uniclass.utenti.service.UtenteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtenteServiceTest {

    // Oggetto REALE da testare
    private UtenteService utenteService;

    // Oggetti MOCK (finti) che simuleranno il comportamento del DB
    @Mock private PersonaleTAService personaleTAServiceMock;
    @Mock private AccademicoService accademicoServiceMock;

    @BeforeEach
    void setUp() {
        // 1. Inizializza i mock definiti con @Mock
        MockitoAnnotations.openMocks(this);

        // 2. Istanzia il service reale
        utenteService = new UtenteService();

        // 3. INIEZIONE DEI DIPENDENZE 
        // Qui usiamo i setterì per inserire i nostri mock.
        // Grazie all'IF che abbiamo messo nel codice (Lazy Loading), 
        // la classe userà questi invece di provare a connettersi al DB vero.
        utenteService.setPersonaleTAService(personaleTAServiceMock);
        utenteService.setAccademicoService(accademicoServiceMock);
    }

    /**
     * SCENARIO 1: Login corretto come Personale Tecnico Amministrativo.
     * Verifica che se l'email corrisponde a un TA, venga restituito quell'oggetto.
     */
    @Test
    void testRetrieveByUserAndPassword_Success_PersonaleTA() {
        System.out.println("Test: Login Personale TA Successo");

        // Arrange (Preparazione)
        String email = "admin@unisa.it";
        String password = "passwordSicura";
        
        PersonaleTA taUser = new PersonaleTA();
        taUser.setEmail(email);
        taUser.setPassword(password);

        // Istruiamo i Mock:
        // "Se ti chiedono questa email, restituisci l'utente TA"
        when(personaleTAServiceMock.trovaEmail(email)).thenReturn(taUser);
        // "Il service accademico non deve trovare nulla per questa email"
        when(accademicoServiceMock.trovaEmailUniClass(email)).thenReturn(null);

        // Act (Esecuzione)
        Utente result = utenteService.retrieveByUserAndPassword(email, password);

        // Assert (Verifica)
        assertNotNull(result, "L'utente dovrebbe essere stato trovato");
        assertEquals(email, result.getEmail());
        assertTrue(result instanceof PersonaleTA, "L'utente restituito deve essere di tipo PersonaleTA");
    }

    /**
     * SCENARIO 2: Login corretto come Accademico (es. Docente o Studente).
     * Verifica la logica 'else if': se non è un TA, cerca tra gli accademici.
     */
    @Test
    void testRetrieveByUserAndPassword_Success_Accademico() {
        System.out.println("Test: Login Accademico Successo");

        // Arrange
        String email = "prof@unisa.it";
        String password = "passwordProf";

        Accademico accUser = new Accademico();
        accUser.setEmail(email);
        accUser.setPassword(password);

        // Istruiamo i Mock:
        // PRIMA cerca nei TA -> Restituisce NULL (non trovato)
        when(personaleTAServiceMock.trovaEmail(email)).thenReturn(null);
        // POI cerca negli Accademici -> Restituisce l'utente
        when(accademicoServiceMock.trovaEmailUniClass(email)).thenReturn(accUser);

        // Act
        Utente result = utenteService.retrieveByUserAndPassword(email, password);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertTrue(result instanceof Accademico, "L'utente restituito deve essere di tipo Accademico");
    }

    /**
     * SCENARIO 3: Password errata.
     * Questo è fondamentale per la sicurezza (Dependability).
     * Deve lanciare un'eccezione, NON ritornare null o l'utente.
     */
    @Test
    void testRetrieveByUserAndPassword_WrongPassword() {
        System.out.println("Test: Login Password Errata");

        String email = "user@unisa.it";
        String passwordVera = "giusta";
        String passwordErrata = "sbagliata";

        PersonaleTA user = new PersonaleTA();
        user.setEmail(email);
        user.setPassword(passwordVera);

        // Simuliamo che l'utente esista
        when(personaleTAServiceMock.trovaEmail(email)).thenReturn(user);

        // Act & Assert
        // Ci aspettiamo che chiamando il metodo con la password sbagliata venga lanciata l'eccezione
        assertThrows(AuthenticationException.class, () -> {
            utenteService.retrieveByUserAndPassword(email, passwordErrata);
        });
    }

    /**
     * SCENARIO 4: Utente non esistente.
     * Entrambi i service ritornano null. Il risultato finale deve essere null.
     */
    @Test
    void testRetrieveByUserAndPassword_UserNotFound() {
        System.out.println("Test: Utente Non Trovato");

        String email = "fantasma@unisa.it";
        String password = "any";

        when(personaleTAServiceMock.trovaEmail(email)).thenReturn(null);
        when(accademicoServiceMock.trovaEmailUniClass(email)).thenReturn(null);

        Utente result = utenteService.retrieveByUserAndPassword(email, password);

        assertNull(result, "Se l'utente non esiste in nessun DB, deve tornare null");
    }
}
