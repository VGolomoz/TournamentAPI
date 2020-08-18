package me.sports.task.repositories;

import me.sports.task.entities.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface extends JpaRepository for generic CRUD operations and create query to database for Participant Entity.
 *
 * @author Vadym
 */

@Repository
public interface ParticipantRepo extends JpaRepository<Participant, Long> {

    @Query("from Participant p join fetch p.tournaments t where p.name = :name")
    Participant getByName(String name);
}
