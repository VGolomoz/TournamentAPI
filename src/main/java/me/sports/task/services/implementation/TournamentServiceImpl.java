package me.sports.task.services.implementation;

import me.sports.task.dto.MatchDTO;
import me.sports.task.dto.TournamentDTO;
import me.sports.task.entities.Match;
import me.sports.task.entities.Participant;
import me.sports.task.entities.Tournament;
import me.sports.task.repositories.MatchRepo;
import me.sports.task.repositories.ParticipantRepo;
import me.sports.task.repositories.TournamentRepo;
import me.sports.task.services.interfaces.ITournamentService;
import me.sports.task.utils.MatchStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service which implements interface ITournamentService and implement logic of create, start, hold and get
 * Tournament by identifier and also logic of add or remove Participants by name to/from Tournament
 *
 * @author Vadym
 */

@Service
public class TournamentServiceImpl implements ITournamentService {

    private TournamentRepo tournamentRepo;
    private ParticipantRepo participantRepo;
    private MatchRepo matchRepo;

    private final int defaultMaxNumberOfParticipantsInTournament = 8;

    @Autowired
    public TournamentServiceImpl(final TournamentRepo tournamentRepo, final ParticipantRepo participantRepo,
                                 final MatchRepo matchRepo) {
        this.tournamentRepo = tournamentRepo;
        this.participantRepo = participantRepo;
        this.matchRepo = matchRepo;
    }

    @Override
    public TournamentDTO createTournament(Integer maxNumberOfParticipants) {
        if (maxNumberOfParticipants <= 0 || maxNumberOfParticipants % defaultMaxNumberOfParticipantsInTournament != 0) {
            throw new RuntimeException("Max number of participants should be positive and multiple of 8");
        }
        Tournament tournament = new Tournament();
        tournament.setMaxNumberOfParticipants(maxNumberOfParticipants);
        tournament = tournamentRepo.save(tournament);
        return createTournamentDTO(tournament);
    }

    @Override
    public TournamentDTO getTournament(Long id) {
        Tournament tournament = getTournamentIfExists(id);
        return createTournamentDTO(tournament);
    }

    @Override
    @Transactional
    public TournamentDTO startTournament(Long id) {
        Tournament tournament = getTournamentIfExists(id);
        tournament.setNumberOfMatches(calculateNumberOfMatches(tournament.getTournamentParticipants()));
        tournament.setOnHold(false);
        appointOpponents(tournament);
        tournament = tournamentRepo.save(tournament);

        return createTournamentDTO(tournament);
    }

    @Override
    @Transactional
    public TournamentDTO holdTournament(Long id) {
        Tournament tournament = getTournamentIfExists(id);
        tournament.setOnHold(true);
        tournament = tournamentRepo.save(tournament);
        return createTournamentDTO(tournament);
    }

    @Override
    @Transactional
    public TournamentDTO addParticipant(Long id, String name) {
        participantNameValidate(name);

        Tournament tournament = getTournamentIfExists(id);
        if (tournament.getTournamentParticipants().size() == tournament.getMaxNumberOfParticipants()) {
            throw new RuntimeException("Tournament already includes the maximum number of participants");
        } else if (isFinished(tournament.getMatches())) {
            throw new RuntimeException("Tournament was finished");
        }

        Participant participant = participantRepo.getByName(name);
        if (participant == null) {
            participant = new Participant();
            participant.setName(name);
            participant = participantRepo.save(participant);
        } else if (tournament.getTournamentParticipants().contains(participant)) {
            throw new RuntimeException("The participant is already taking part in this tournament");
        }

//        if tournament was created get incompletely match if exists or create new match with new participant
        if (!tournament.getMatches().isEmpty()) {
            Match match = getIncompletelyTournamentMatchIfExists(tournament.getMatches());
            if (match == null) {
                match = new Match();
                match.setFirstParticipant(participant);
//              incompletely match does not exists, so add additional TBD match for additional match winner.
                tournament.addMatch(createAdditionalMatchWithoutParticipations(tournament));
            } else {
                if (match.getFirstParticipant() == null) {
                    match.setFirstParticipant(participant);
                } else {
                    match.setSecondParticipant(participant);
                }
            }
            match.setTournament(tournament);
            match = matchRepo.save(match);
            tournament.addMatch(match);
        }
        tournament.addParticipant(participant);
        tournament = tournamentRepo.save(tournament);

        return createTournamentDTO(tournament);
    }

    @Override
    @Transactional
    public TournamentDTO removeParticipant(Long id, String name) {
        participantNameValidate(name);

        Tournament tournament = getTournamentIfExists(id);
        if (isFinished(tournament.getMatches())) {
            throw new RuntimeException("Tournament was finished");
        } else if (!tournament.isOnHold()) {
            throw new RuntimeException("Tournament is not on hold");
        }

        Participant participant = participantRepo.getByName(name);
        if (participant == null) {
            throw new RuntimeException("Participant '" + name + "' does not exist");
        } else if (!tournament.getTournamentParticipants().contains(participant)) {
            throw new RuntimeException("Participant '" + name + "'  is not taking part in this tournament");
        } else if (tournament.getMatches().isEmpty()) {
            tournament.removeParticipant(participant);
            tournament = tournamentRepo.save(tournament);
            return createTournamentDTO(tournament);
        }

        Match match = getTournamentActiveMatchWithCurrentParticipant(tournament.getMatches(), participant);
        if (match == null) {
            throw new RuntimeException("Participant cannot be removed - all matches with participant '"
                    + name + "' have been completed");
        }
        tournament.removeParticipant(participant);
//        if  match was incompletely and only with one participant - remove match, else - remove only current participant
        if (getMatchStatus(match).equals(MatchStatus.TBD)) {
            tournament.removeMatch(match);
            matchRepo.delete(match);
        } else {
            if (match.getFirstParticipant() != null && match.getFirstParticipant().equals(participant)) {
                match.setFirstParticipant(null);
            } else {
                match.setSecondParticipant(null);
            }
            matchRepo.save(match);
        }

//        fix number of matches in current tournament after all changes with participant
        if (calculateNumberOfMatches(tournament.getTournamentParticipants()) != tournament.getMatches().size()) {
            Match currentExtraMatch = null;
            for (Match extraMatch : tournament.getMatches()) {
                if (getMatchStatus(extraMatch).equals(MatchStatus.TBD)) {
                    currentExtraMatch = extraMatch;
                    matchRepo.delete(extraMatch);
                }
            }
            tournament.removeMatch(currentExtraMatch);
        }
        tournament = tournamentRepo.save(tournament);

        return createTournamentDTO(tournament);
    }

    private Match getTournamentActiveMatchWithCurrentParticipant(List<Match> matches, Participant participant) {
        Match newMatch = null;
        for (Match match : matches) {
            if (getMatchStatus(match).equals(MatchStatus.ACTIVE)) {
                if ((match.getFirstParticipant() != null && match.getFirstParticipant().equals(participant)) ||
                        (match.getSecondParticipant() != null && match.getSecondParticipant().equals(participant))) {
                    newMatch = match;
                }
            }

        }
        return newMatch;
    }

    private boolean isFinished(List<Match> matches) {
        if (matches.isEmpty()) {
            return false;
        } else {
            boolean isFinished = true;
            for (Match match : matches) {
                if (match.getFinishDate() == null)
                    isFinished = false;
            }
            return isFinished;
        }
    }

    private Match getIncompletelyTournamentMatchIfExists(List<Match> matches) {
        if (matches.isEmpty()) {
            return null;
        }
        Match newMatch = null;
        for (Match match : matches) {
            if ((match.getFirstParticipant() == null || match.getSecondParticipant() == null)
                    && getMatchStatus(match).equals(MatchStatus.ACTIVE)) {
                newMatch = match;
            }
        }
        return newMatch;
    }

    private void appointOpponents(Tournament tournament) {
        List<Participant> currentParticipants = new ArrayList<>(tournament.getTournamentParticipants().size());
        currentParticipants.addAll(tournament.getTournamentParticipants());
        for (int i = 0; i < tournament.getNumberOfMatches(); i++) {
            Match match = new Match();
            match.setTournament(tournament);
            match.setFirstParticipant(getRandomOpponent(currentParticipants));
            match.setSecondParticipant(getRandomOpponent(currentParticipants));
            match = matchRepo.save(match);
            tournament.addMatch(match);
        }
    }

    private Participant getRandomOpponent(List<Participant> currentParticipants) {
        Participant randomOpponent = null;
        if (currentParticipants.size() > 0) {
            int randomNumber = new Random().nextInt(currentParticipants.size());
            randomOpponent = currentParticipants.get(randomNumber);
            currentParticipants.remove(randomNumber);
        }
        return randomOpponent;
    }

    private Integer calculateNumberOfMatches(Set<Participant> participants) {
        if (participants.isEmpty()) {
            return 0;
        } else if (participants.size() % 2 == 0) {
            return participants.size() - 1;
        } else {
            return participants.size();
        }
    }

    private Set<MatchDTO> getCurrentMatches(Tournament tournament) {
        Set<MatchDTO> currentMatches = new HashSet<>();
        for (Match match : tournament.getMatches()) {
            MatchDTO matchDTOBuilder = MatchDTO.builder()
                    .id(match.getId())
                    .startDate(match.getStartDate())
                    .finishDate(match.getFinishDate())
                    .firstParticipant(match.getFirstParticipant())
                    .secondParticipant(match.getSecondParticipant())
                    .firstParticipantScore(match.getFirstParticipantScore())
                    .secondParticipantScore(match.getSecondParticipantScore())
                    .status(getMatchStatus(match))
                    .build();
            currentMatches.add(matchDTOBuilder);
        }
        return currentMatches;
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

    private Match createAdditionalMatchWithoutParticipations(Tournament tournament) {
        Match additionalMatch = new Match();
        additionalMatch.setTournament(tournament);
        matchRepo.save(additionalMatch);
        return additionalMatch;
    }

    private TournamentDTO createTournamentDTO(Tournament tournament) {
        return TournamentDTO.builder()
                .id(tournament.getId())
                .maxNumberOfParticipants(tournament.getMaxNumberOfParticipants())
                .numberOfMatches(calculateNumberOfMatches(tournament.getTournamentParticipants()))
                .onHold(tournament.isOnHold())
                .tournamentParticipants(tournament.getTournamentParticipants())
                .matches(getCurrentMatches(tournament))
                .build();
    }

    private void participantNameValidate(String name) {
        if (StringUtils.isAnyBlank(name) || name.equals("null") || !StringUtils.isAlphanumeric(name)) {
            throw new RuntimeException("Wrong value for participant name. Name should contain only letters or digits");
        }
    }

    private Tournament getTournamentIfExists(Long id) {
        Tournament tournament = tournamentRepo.getById(id);
        if (tournament == null) {
            throw new RuntimeException("Tournament does not exist");
        }
        return tournament;
    }
}
