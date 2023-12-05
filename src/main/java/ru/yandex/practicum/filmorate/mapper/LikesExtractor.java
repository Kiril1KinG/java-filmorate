package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikesExtractor implements ResultSetExtractor<Map<Integer, Set<Integer>>> {

    @Override
    public Map<Integer, Set<Integer>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        int currentUserId = 0;
        Set<Integer> likedFilms = new HashSet<>();
        Map<Integer, Set<Integer>> userLikes = new HashMap<>();
        while (rs.next()) {
            int userId = rs.getInt("user_id");
            if (currentUserId != userId) {
                if (currentUserId != 0) {
                    userLikes.put(currentUserId, likedFilms);
                }
                currentUserId = userId;
                likedFilms = new HashSet<>();
            }
            likedFilms.add(rs.getInt("film_id"));
        }
        if (currentUserId != 0) {
            userLikes.put(currentUserId, likedFilms);
        }
        return userLikes;
    }
}
