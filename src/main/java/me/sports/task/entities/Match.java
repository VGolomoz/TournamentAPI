package me.sports.task.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * POJO Class to save Match instance in the database
 *
 * @author Vadym
 */

@Entity
@Table(name = "`match`")
@Data
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "`id`")
    private Long id;

    @CreationTimestamp
    @Column(name = "`start_time`")
    private Date startDate;

    @Column(name = "`finish_time`")
    private Date finishDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "`first_participant`")
    private Participant firstParticipant;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "`second_participant`")
    private Participant secondParticipant;

    @Column(name = "`first_participant_score`")
    private Double firstParticipantScore = 0.0;

    @Column(name = "`second_participant_score`")
    private Double secondParticipantScore = 0.0;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "`tournament_id`")
    private Tournament tournament;
}
