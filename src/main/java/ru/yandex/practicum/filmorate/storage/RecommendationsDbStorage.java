package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.LikesExtractor;

import java.util.Map;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
public class RecommendationsDbStorage {

    private final JdbcTemplate jdbcTemplate;
    private final LikesExtractor likesExtractor;

    public Map<Integer, Set<Integer>> getAllLikes() {
        String sql = "select user_id, film_id from \"like\" order by user_id";
        return jdbcTemplate.query(sql, likesExtractor);
    }
}
