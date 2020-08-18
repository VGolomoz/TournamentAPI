package me.sports.task.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * POJO Class to save Tournament instance in the database
 *
 * @author Vadym
 */

@Entity
@Table(name = "`tournament`")
@Data
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "`id`")
    private Long id;

    @Column(name = "`max_number_of_participants`")
    private Integer maxNumberOfParticipants;

    @Column(name = "`number_of_matches`")
    private Integer numberOfMatches;

    @Getter(AccessLevel.NONE)
    @Column(name = "`on_hold`")
    private Boolean onHold = true;

    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "`tournament_participant`",
                joinColumns = { @JoinColumn(name = "`tournament_id`") },
                inverseJoinColumns = { @JoinColumn(name = "`participant_id`")})
    private Set<Participant> tournamentParticipants = new HashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    @OneToMany(mappedBy = "tournament")
    private List<Match> matches = new ArrayList<>();

    public void addParticipant(Participant participant){
        tournamentParticipants.add(participant);
    }

    public void removeParticipant(Participant participant){
        tournamentParticipants.remove(participant);
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public void removeMatch(Match match){
        matches.remove(match);
    }

    public Boolean isOnHold(){
        return onHold;
    }
}
