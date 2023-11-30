package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewsController {

    @PostMapping
    public Review addReview(@RequestBody Review review){
        return null;
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review){
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteReviewById(@PathVariable int id){

    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id){
        return null;
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) int filmId,
                                   @RequestParam(defaultValue = "10") int count){
        return null;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId){

    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId){

    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId){

    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable int id, @PathVariable int userId){

    }
}
