package me.sports.task.controllers;

import com.sun.istack.NotNull;
import me.sports.task.dto.MatchDTO;
import me.sports.task.services.interfaces.IMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller which provides summarizing and getting results of Match by identifier
 *
 * @author Vadym
 */

@RestController
@RequestMapping("/mesports/match")
@CrossOrigin
public class MatchController {

    private IMatchService matchService;

    @Autowired
    public MatchController(final IMatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<MatchDTO> summarizingMatchResults(@NotNull @PathVariable Long id) {
        return new ResponseEntity<>(matchService.summarizingMatchResults(id), HttpStatus.OK);
    }
}
