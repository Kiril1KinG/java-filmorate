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
public class RecommendationsDbStorage implements RecommendationsStorage {

    private final JdbcTemplate jdbcTemplate;
    private final LikesExtractor likesExtractor;

    @Override
    public Map<Integer, Set<Integer>> getCommonLikes(int userId) {
        String sql = "select user_id, film_id from \"like\" " +
                " where user_id in ( " +
                "       select l2.user_id " +
                "         from \"like\" l1 " +
                "         join \"like\" l2 on l2.film_id = l1.film_id " +
                "        where l1.user_id = ?" +
                ")" +
                "order by user_id";
        return jdbcTemplate.query(sql, likesExtractor, userId);
    }
}
