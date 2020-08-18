package me.sports.task.services.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.sports.task.TaskApplication;
import me.sports.task.dto.TournamentDTO;
import me.sports.task.services.interfaces.ITournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = TaskApplication.class)
@TestPropertySource(locations = "/application-test.properties")
public class TournamentBundleTest {

    @Autowired
    private ITournamentService tournamentService;
    private Long tournamentTestId;
    private static final Integer DEFAULT_MAX_PARTICIPANT_NUMBER = 8;
    private static final Integer WRONG_PARTICIPANT_NUMBER = 3;
    private static final Long WRONG_TOURNAMENT_ID = -1l;
    private static final String GOOD_PARTICIPANT_NAME = "GoodParticipant";
    private static final String SAME_NAME_TEST = "SameName";
    private static final String NOT_ON_HOLD_TEST = "NotOnHoldName";
    private static final String ON_HOLD_TEST = "OnHoldName";
    private static final String FIRST_PARTICIPANT_NAME = "FirstParticipant";
    private static final String SECOND_PARTICIPANT_NAME = "SecondParticipant";
    private static final String THIRD_PARTICIPANT_NAME = "ThirdParticipant";
    private static final String FOURTH_PARTICIPANT_NAME = "FourthParticipant";

    @BeforeEach
    public void setup() {
        TournamentDTO dto = tournamentService.createTournament(DEFAULT_MAX_PARTICIPANT_NUMBER);
        tournamentTestId = dto.getId();
    }


    @Test
    public void createTournamentWithGoodNumberOfParticipants_thenGoodResult() throws JsonProcessingException {
        TournamentDTO tournamentDTO = tournamentService.createTournament(DEFAULT_MAX_PARTICIPANT_NUMBER);
        assertNotNull(tournamentDTO);
        assertNotNull(tournamentDTO.getId());
    }

    @Test
    public void createTournamentWithWrongNumberOfParticipants_thenBadResult() {
        Exception thrown = assertThrows(Exception.class, () -> tournamentService.createTournament(WRONG_PARTICIPANT_NUMBER));
        assertTrue(thrown.getMessage().contains("Max number of participants should be positive and multiple of 8"));
    }

    @Test
    public void getTournamentWithGoodId_thenGoodResult() throws InterruptedException {
        TournamentDTO tournamentDTO = tournamentService.getTournament(tournamentTestId);
        assertNotNull(tournamentDTO);
        assertNotNull(tournamentDTO.getId());
        assertEquals(tournamentTestId, tournamentDTO.getId());
    }

    @Test
    public void getTournamentWithWrongId_thenBadResult() {
        Exception thrown = assertThrows(Exception.class, () -> tournamentService.getTournament(WRONG_TOURNAMENT_ID));
        assertTrue(thrown.getMessage().contains("Tournament does not exist"));
    }

    @Test
    public void startTournamentWithGoodId_thenGoodResult() {
        TournamentDTO tournamentDTO = tournamentService.getTournament(tournamentTestId);
        assertNotNull(tournamentDTO);
        assertNotNull(tournamentDTO.getId());
        assertEquals(tournamentTestId, tournamentDTO.getId());
    }

    @Test
    public void startTournamentWithWrongId_thenBadResult() {
        Exception thrown = assertThrows(Exception.class, () -> tournamentService.getTournament(WRONG_TOURNAMENT_ID));
        assertTrue(thrown.getMessage().contains("Tournament does not exist"));
    }

    @Test
    public void addSameParticipantToSameTournament_thenBadResult() {
        TournamentDTO tournamentDTO = tournamentService.addParticipant(tournamentTestId, SAME_NAME_TEST);
        assertNotNull(tournamentDTO);
        assertFalse(tournamentDTO.getTournamentParticipants().isEmpty());
        tournamentDTO.getTournamentParticipants().forEach(participant ->
                assertEquals(SAME_NAME_TEST, participant.getName()));

        Exception thrown = assertThrows(Exception.class, () -> tournamentService.addParticipant(tournamentTestId, SAME_NAME_TEST));
        assertTrue(thrown.getMessage().contains("The participant is already taking part in this tournament"));
    }

    @Test
    public void addParticipantWithGoodNameAndStartTournament_thenGoodResult() {
        TournamentDTO tournamentDTO = tournamentService.addParticipant(tournamentTestId, GOOD_PARTICIPANT_NAME);
        assertNotNull(tournamentDTO);
        assertTrue(tournamentDTO.getMatches().isEmpty());
        assertFalse(tournamentDTO.getTournamentParticipants().isEmpty());
        tournamentDTO.getTournamentParticipants().forEach(participant ->
                assertEquals(GOOD_PARTICIPANT_NAME, participant.getName()));

        tournamentDTO = tournamentService.startTournament(tournamentTestId);
        assertNotNull(tournamentDTO);
        assertEquals(tournamentTestId, tournamentDTO.getId());
        assertFalse(tournamentDTO.getMatches().isEmpty());
    }

    @Test
    public void addParticipantWithWrongName_thenBadResult(){
        Exception thrown = assertThrows(Exception.class, () -> tournamentService.addParticipant(tournamentTestId, null));
        assertTrue(thrown.getMessage().contains("Wrong value for participant name. Name should contain only letters or digits"));
    }

    @Test
    public void removeParticipantFromTournamentNotOnHold_thenBadResult(){
        TournamentDTO tournamentDTO = tournamentService.getTournament(tournamentTestId);
        assertNotNull(tournamentDTO);
        assertEquals(tournamentTestId, tournamentDTO.getId());
        assertTrue(tournamentDTO.getTournamentParticipants().isEmpty());
        assertTrue(tournamentDTO.getMatches().isEmpty());

        tournamentDTO = tournamentService.addParticipant(tournamentTestId, NOT_ON_HOLD_TEST);
        assertNotNull(tournamentDTO);
        assertFalse(tournamentDTO.getTournamentParticipants().isEmpty());

        tournamentDTO = tournamentService.startTournament(tournamentTestId);
        assertNotNull(tournamentDTO);
        assertFalse(tournamentDTO.getMatches().isEmpty());

        assertFalse(tournamentDTO.isOnHold());
        Exception thrown = assertThrows(Exception.class,
                () -> tournamentService.removeParticipant(tournamentTestId, NOT_ON_HOLD_TEST));
        assertTrue(thrown.getMessage().contains("Tournament is not on hold"));
    }

    @Test
    public void removeParticipantFromTournamentOnHold_thenGoodResult(){
        TournamentDTO tournamentDTO = tournamentService.getTournament(tournamentTestId);
        assertNotNull(tournamentDTO);
        assertEquals(tournamentTestId, tournamentDTO.getId());
        assertTrue(tournamentDTO.getTournamentParticipants().isEmpty());
        assertTrue(tournamentDTO.getMatches().isEmpty());

        tournamentDTO = tournamentService.addParticipant(tournamentTestId, ON_HOLD_TEST);
        assertNotNull(tournamentDTO);
        assertFalse(tournamentDTO.getTournamentParticipants().isEmpty());

        tournamentDTO = tournamentService.startTournament(tournamentTestId);
        assertNotNull(tournamentDTO);
        assertFalse(tournamentDTO.getMatches().isEmpty());

        assertFalse(tournamentDTO.isOnHold());
        tournamentDTO = tournamentService.holdTournament(tournamentTestId);

        assertTrue(tournamentDTO.isOnHold());
        tournamentDTO = tournamentService.removeParticipant(tournamentTestId, ON_HOLD_TEST);
        assertTrue(tournamentDTO.getTournamentParticipants().isEmpty());
        assertTrue(tournamentDTO.getMatches().isEmpty());
    }

    @Test
    public void addParticipantAndStartTournamentAndCheckMatchNumbersChanges_thenGoodResult(){
        TournamentDTO tournamentDTO = tournamentService.addParticipant(tournamentTestId, FIRST_PARTICIPANT_NAME);
        assertNotNull(tournamentDTO);
        assertTrue(tournamentDTO.getMatches().isEmpty());
        assertEquals(1, tournamentDTO.getNumberOfMatches());

        tournamentDTO = tournamentService.addParticipant(tournamentTestId, SECOND_PARTICIPANT_NAME);
        assertNotNull(tournamentDTO);
        assertTrue(tournamentDTO.getMatches().isEmpty());
        assertEquals(1, tournamentDTO.getNumberOfMatches());

        tournamentDTO = tournamentService.startTournament(tournamentTestId);
        assertNotNull(tournamentDTO);
        assertEquals(tournamentTestId, tournamentDTO.getId());
        assertFalse(tournamentDTO.getTournamentParticipants().isEmpty());
        assertFalse(tournamentDTO.getMatches().isEmpty());
        assertEquals(1, tournamentDTO.getMatches().size());
        assertEquals(1, tournamentDTO.getNumberOfMatches());

        tournamentDTO = tournamentService.addParticipant(tournamentTestId, THIRD_PARTICIPANT_NAME);
        assertNotNull(tournamentDTO);
        assertFalse(tournamentDTO.getMatches().isEmpty());
        assertEquals(3, tournamentDTO.getMatches().size());
        assertEquals(3, tournamentDTO.getNumberOfMatches());
    }

    @Test
    public void removeParticipantFromStartedTournamentAndCheckMatchNumbersChanges_thenGoodResult(){
        tournamentService.addParticipant(tournamentTestId, FIRST_PARTICIPANT_NAME);
        tournamentService.addParticipant(tournamentTestId, SECOND_PARTICIPANT_NAME);
        tournamentService.addParticipant(tournamentTestId, THIRD_PARTICIPANT_NAME);
        TournamentDTO tournamentDTO = tournamentService.addParticipant(tournamentTestId, FOURTH_PARTICIPANT_NAME);
        assertNotNull(tournamentDTO);
        assertTrue(tournamentDTO.getMatches().isEmpty());
        assertEquals(3, tournamentDTO.getNumberOfMatches());

        tournamentService.startTournament(tournamentTestId);
        tournamentDTO = tournamentService.holdTournament(tournamentTestId);
        assertNotNull(tournamentDTO);
        assertEquals(tournamentTestId, tournamentDTO.getId());
        assertFalse(tournamentDTO.getTournamentParticipants().isEmpty());
        assertFalse(tournamentDTO.getMatches().isEmpty());
        assertEquals(3, tournamentDTO.getMatches().size());
        assertEquals(3, tournamentDTO.getNumberOfMatches());

        tournamentDTO = tournamentService.removeParticipant(tournamentTestId, FOURTH_PARTICIPANT_NAME);
        assertNotNull(tournamentDTO);
        assertFalse(tournamentDTO.getMatches().isEmpty());
        assertEquals(3, tournamentDTO.getMatches().size());
    }
}
