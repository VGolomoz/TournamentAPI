package me.sports.task.repositories;

import me.sports.task.entities.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface extends JpaRepository for generic CRUD operations and create query to database for Match Entity.
 *
 * @author Vadym
 */
@Repository
public interface MatchRepo extends JpaRepository<Match, Long> {

    Match getById(Long id);
}
