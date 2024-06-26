package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.RecommendationsStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationsService {
    private final RecommendationsStorage recommendationsStorage;
    private final FilmStorage filmStorage;

    public List<Film> getUserRecommendations(int userId) {
        var allLikes = recommendationsStorage.getCommonLikes(userId);
        if (allLikes.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        var userLikes = allLikes.get(userId);


        Map<Integer, Map<Integer, Integer>> freq = new HashMap<>();
        for (var likes : allLikes.values()) {
            for (Integer film : likes) {
                for (var film2 : likes) {
                    freq.computeIfAbsent(film, HashMap::new);
                    freq.get(film).put(film2, freq.get(film).getOrDefault(film2, 0) + 1);
                }
            }
        }

        Map<Integer, Integer> uFreq = new HashMap<>();
        for (Integer j : userLikes) {
            for (Integer k : freq.keySet()) {
                uFreq.put(k, uFreq.getOrDefault(k, 0) + freq.get(k).getOrDefault(j, 0));
            }
        }

        var clean = uFreq.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (var film : filmStorage.getFilms()) {
            if (userLikes.contains(film.getId())) {
                clean.put(film.getId(), 1);
            } else if (!clean.containsKey(film.getId())) {
                clean.put(film.getId(), -1);
            }
        }

        return clean.entrySet().stream()
                .filter(e -> e.getValue() >= 1)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .filter(filmId -> !userLikes.contains(filmId))
                .map(filmStorage::getFilmById)
                .collect(Collectors.toList());
    }
}
