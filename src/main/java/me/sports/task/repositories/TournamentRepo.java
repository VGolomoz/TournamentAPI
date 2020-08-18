package me.sports.task.repositories;

import me.sports.task.entities.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface extends JpaRepository for generic CRUD operations and create query to database for Tournament Entity.
 *
 * @author Vadym
 */

@Repository
public interface TournamentRepo extends JpaRepository<Tournament, Long> {

    Tournament getById(Long id);
}
