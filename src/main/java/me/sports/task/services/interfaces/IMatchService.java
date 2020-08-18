package me.sports.task.services.interfaces;

import me.sports.task.dto.MatchDTO;
import org.springframework.http.ResponseEntity;

/**
 * Interface which describes method of summarizing and getting results of Match by identifier
 *
 * @author Vadym
 */

public interface IMatchService {

    MatchDTO summarizingMatchResults(Long id);
}
