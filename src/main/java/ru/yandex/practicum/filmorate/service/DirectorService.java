package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getDirectors() {
        List<Director> directors = directorStorage.getDirectors();
        log.info("Directors received: {}", directors);
        return directors;
    }

    public Director getDirectorById(int id) {
        checkExistDirector(id);
        Director director = directorStorage.getDirectorById(id);
        log.info("Director by id received: {}", director);
        return director;
    }

    public Director addDirector(Director director) {
        log.info("Director added: {}", director);
        return directorStorage.addDirector(director);

    }

    public Director updateDirector(Director director) {
        checkExistDirector(director.getId());
        log.info("Director updated: {}", director);
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        checkExistDirector(id);
        log.info("Director with id={} deleted", id);
        directorStorage.deleteDirector(id);
    }

    private void checkExistDirector(int id) {
        if (!directorStorage.containsDirectorById(id)) {
            log.warn("Director with id={} doesn't exist", id);
            throw new DataNotFoundException("Director not found");
        }
    }
}
