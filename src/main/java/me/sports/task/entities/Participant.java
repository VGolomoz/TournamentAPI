package me.sports.task.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO Class to save Participant instance in the database
 *
 * @author Vadym
 */

@Entity
@Table(name = "`participant`")
@Data
public class Participant {

    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    @Column(name = "`id`")
    private Long id;

    @Column(name = "`name`", unique = true, nullable = false)
    private String name;

    @LazyCollection(LazyCollectionOption.TRUE)
    @JsonIgnore
    @ManyToMany(mappedBy = "tournamentParticipants")
    private List<Tournament> tournaments = new ArrayList<>();

}
