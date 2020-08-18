package me.sports.task.controllers;

import com.sun.istack.NotNull;
import me.sports.task.dto.TournamentDTO;
import me.sports.task.services.interfaces.ITournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller which provides functions for create, start, hold and get Tournament by identifier and also
 * add or remove Participants by name to/from Tournament
 *
 * @author Vadym
 */

@RestController
@RequestMapping("/mesports/tournament")
@CrossOrigin
public class TournamentController {

    private ITournamentService tournamentService;

    @Autowired
    public TournamentController(final ITournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTournament(@NotNull @RequestParam Integer maxNumberOfParticipants) {
        return new ResponseEntity<>(tournamentService.createTournament(maxNumberOfParticipants), HttpStatus.OK);
    }

    @GetMapping("/{id}/get")
    public ResponseEntity<TournamentDTO> getTournament(@NotNull @PathVariable Long id)  {
        return new ResponseEntity<>(tournamentService.getTournament(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<TournamentDTO> startTournament(@NotNull @PathVariable Long id) {
        return new ResponseEntity<>(tournamentService.startTournament(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/hold")
    public ResponseEntity<TournamentDTO> holdTournament(@NotNull @PathVariable Long id) {
        return new ResponseEntity<>(tournamentService.holdTournament(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/participant/add")
    public ResponseEntity<TournamentDTO> addParticipant(@NotNull @PathVariable Long id, @NotNull @RequestParam String name) {
        return new ResponseEntity<>(tournamentService.addParticipant(id, name), HttpStatus.OK);
    }

    @PostMapping("/{id}/participant/remove")
    public ResponseEntity<TournamentDTO> removeParticipant(@NotNull @PathVariable Long id, @NotNull @RequestParam String name)  {
        return new ResponseEntity<>(tournamentService.removeParticipant(id, name), HttpStatus.OK);
    }
}
