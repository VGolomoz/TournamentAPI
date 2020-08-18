package me.sports.task.dto;

import lombok.Builder;
import lombok.Data;
import me.sports.task.entities.Participant;
import me.sports.task.utils.MatchStatus;

import java.util.Date;

/**
 * POJO class to construct and transfer new instances of Match entity
 *
 * @author Vadym
 */

@Data
@Builder
public class MatchDTO {

    private Long id;
    private Date startDate;
    private Date finishDate;
    private Participant firstParticipant;
    private Participant secondParticipant;
    private Double firstParticipantScore;
    private Double secondParticipantScore;
    private MatchStatus status;
}
