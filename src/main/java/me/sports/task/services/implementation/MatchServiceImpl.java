package me.sports.task.services.implementation;

import me.sports.task.dto.MatchDTO;
import me.sports.task.entities.Match;
import me.sports.task.repositories.MatchRepo;
import me.sports.task.services.interfaces.IMatchService;
import me.sports.task.utils.MatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
 * Service which implements interface IMatchService and implement logic of summarizing and getting results
 * of Match by identifier
 *
 * @author Vadym
 */

@Service
public class MatchServiceImpl implements IMatchService {

    private MatchRepo matchRepo;

    @Autowired
    public MatchServiceImpl(final MatchRepo matchRepo) {
        this.matchRepo = matchRepo;
    }

    @Override
    public MatchDTO summarizingMatchResults(Long id) {
        Match match = getTournamentIfExists(id);

        //some business logic for calculate scores

        match.setFirstParticipantScore(new Random().nextDouble());
        match.setSecondParticipantScore(new Random().nextDouble());
        match.setFinishDate(new Date());
        match = matchRepo.save(match);
        return createTournamentDTO(match);
    }

    private Match getTournamentIfExists(Long id) {
        Match match = matchRepo.getById(id);
        if (match == null) {
            throw new RuntimeException("Match does not exist");
        }
        return match;
    }

    private MatchDTO createTournamentDTO(Match match) {
        return MatchDTO.builder()
                .id(match.getId())
                .startDate(match.getStartDate())
                .finishDate(match.getFinishDate())
                .firstParticipant(match.getFirstParticipant())
                .secondParticipant(match.getSecondParticipant())
                .firstParticipantScore(match.getFirstParticipantScore())
                .secondParticipantScore(match.getSecondParticipantScore())
                .status(getMatchStatus(match))
                .build();
    }

    private MatchStatus getMatchStatus(Match match) {
        if (match.getFinishDate() != null) {
            return MatchStatus.FINISHED;
        }

        if (match.getFirstParticipant() != null || match.getSecondParticipant() != null) {
            return MatchStatus.ACTIVE;
        }
        return MatchStatus.TBD;
    }
}
