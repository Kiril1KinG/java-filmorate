package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewDbStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public Review addReview(Review review) {
        if (!userStorage.containsUserById(review.getUserId())) {
            throw new DataNotFoundException("Add review failed: Incorrect user id");
        }
        if (!filmStorage.containsFilmById(review.getFilmId())) {
            throw new DataNotFoundException("Add review failed: Incorrect film id");
        }
        Review result = reviewStorage.addReview(review);
        userStorage.addFeed(result.getUserId(), result.getReviewId(), EventType.REVIEW, Operation.ADD);
        log.info("Review added: {}", result);
        return result;
    }

    public Review updateReview(Review review) {
        if (!reviewStorage.containsReviewById(review.getReviewId())) {
            throw new DataNotFoundException("Update review failed: review not exists");
        }
        Review result = reviewStorage.updateReview(review);
        userStorage.addFeed(result.getUserId(), result.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        log.info("Review updated: {}", result);
        return result;
    }

    public void deleteReviewById(int id) {
        if (!reviewStorage.containsReviewById(id)) {
            throw new DataNotFoundException("Delete review failed: review not exists");
        }
        Review review = reviewStorage.getReviewById(id);
        userStorage.addFeed(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.REMOVE);
        reviewStorage.deleteReviewById(id);
        log.info("Review with id={} deleted", id);
    }

    public Review getReviewById(int id) {
        Review review = reviewStorage.getReviewById(id);
        log.info("Review by id received: {}", review);
        return review;
    }

    public List<Review> getAllReviewsByFilmId(int filmId, int count) {
        List<Review> reviews = reviewStorage.getAllReviewsByFilmId(filmId, count);
        log.info("Reviews received: {}", reviews);
        return reviews;
    }

    public void addLike(int id, int userId) {
        if (!reviewStorage.containsReviewById(id)) {
            throw new DataNotFoundException("Add like failed: Incorrect review id");
        }
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Add like failed: Incorrect user id");
        }
        if (reviewStorage.isReviewContainsLikeOrDislikeFromUser(id, userId, true)) {
            throw new DataAlreadyExistsException("Add like failed: Like already exists");
        }
        reviewStorage.addLike(id, userId);
        log.info("Like for review(id={}) from user(id={}) added", id, userId);
    }

    public void addDislike(int id, int userId) {
        if (!reviewStorage.containsReviewById(id)) {
            throw new DataNotFoundException("Add dislike failed: Incorrect review id");
        }
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Add dislike failed: Incorrect user id");
        }
        if (reviewStorage.isReviewContainsLikeOrDislikeFromUser(id, userId, false)) {
            throw new DataAlreadyExistsException("Add dislike failed: Dislike already exists");
        }
        reviewStorage.addDislike(id, userId);
        log.info("Dislike for review(id={}) from user(id={}) added", id, userId);
    }

    public void deleteLike(int id, int userId) {
        if (!reviewStorage.containsReviewById(id)) {
            throw new DataNotFoundException("Delete like failed: Incorrect review id");
        }
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Delete like failed: Incorrect user id");
        }
        if (!reviewStorage.isReviewContainsLikeOrDislikeFromUser(id, userId, true)) {
            throw new DataAlreadyExistsException("Delete like failed: Like not exists");
        }
        reviewStorage.deleteLike(id, userId);
        log.info("Like for review(id={}) from user(id={}) deleted", id, userId);
    }

    public void deleteDislike(int id, int userId) {
        if (!reviewStorage.containsReviewById(id)) {
            throw new DataNotFoundException("Delete dislike failed: Incorrect review id");
        }
        if (!userStorage.containsUserById(userId)) {
            throw new DataNotFoundException("Delete dislike failed: Incorrect user id");
        }
        if (!reviewStorage.isReviewContainsLikeOrDislikeFromUser(id, userId, false)) {
            throw new DataAlreadyExistsException("Delete dislike failed: Dislike not exists");
        }
        reviewStorage.deleteDislike(id, userId);
        log.info("Dislike for review(id={}) from user(id={}) deleted", id, userId);
    }


}
