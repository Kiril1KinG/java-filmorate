package ru.yandex.practicum.filmorate.storage;

import java.util.Map;
import java.util.Set;

public interface RecommendationsStorage {

    Map<Integer, Set<Integer>> getCommonLikes(int userId);
}
