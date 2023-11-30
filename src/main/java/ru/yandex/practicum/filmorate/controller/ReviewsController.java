package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Service.ReviewService;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewsController {

    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review){
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review){
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable int id){
        reviewService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id){
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllReviewsByFilmId(@RequestParam(required = false) int filmId,
                                   @RequestParam(defaultValue = "10") int count){
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId){
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId){
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId){
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable int id, @PathVariable int userId){
        reviewService.deleteDislike(id, userId);
    }
}
