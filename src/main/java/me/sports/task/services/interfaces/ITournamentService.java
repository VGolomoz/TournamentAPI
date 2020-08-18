package me.sports.task.services.interfaces;

import me.sports.task.dto.TournamentDTO;
import org.springframework.http.ResponseEntity;

/**
 * Interface which describes methods of create, start, hold and get Tournament by identifier and also
 * methods of add or remove Participants by name to/from Tournament
 * @author Vadym
 */

public interface ITournamentService {

    TournamentDTO createTournament(Integer maxNumberOfParticipants);

    TournamentDTO getTournament(Long id);

    TournamentDTO startTournament(Long id);

    TournamentDTO holdTournament(Long id);

    TournamentDTO addParticipant(Long id, String name);

    TournamentDTO removeParticipant(Long id, String name);
}
