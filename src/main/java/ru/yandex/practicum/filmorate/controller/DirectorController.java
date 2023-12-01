package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.Service.DirectorService;
import ru.yandex.practicum.filmorate.model.Director;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public List<Director> getDirectors() {
        log.info("GET: /directors");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("GET: /directors/{}", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director addDirector(@RequestBody @Valid Director director) {
        log.info("POST: /directors");
        return directorService.addDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        log.info("PUT: /directors");
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        log.info("DELETE: /directors/{}", id);
        directorService.deleteDirector(id);
    }
}
