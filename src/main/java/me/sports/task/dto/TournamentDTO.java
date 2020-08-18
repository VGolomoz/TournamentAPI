package me.sports.task.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import me.sports.task.entities.Participant;

import java.util.Set;

/**
 * POJO class to construct and transfer new instances of Tournament entity
 *
 * @author Vadym
 */

@Data
@Builder
public class TournamentDTO {

    private Long id;
    private Integer maxNumberOfParticipants;
    private Integer numberOfMatches;
    @Getter(AccessLevel.NONE)
    private Boolean onHold;
    private Set<Participant> tournamentParticipants;
    private Set<MatchDTO> matches;

    public Boolean isOnHold(){
        return onHold;
    }
}
