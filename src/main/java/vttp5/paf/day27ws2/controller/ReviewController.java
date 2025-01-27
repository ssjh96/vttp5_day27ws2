package vttp5.paf.day27ws2.controller;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.internal.bulk.UpdateRequest;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp5.paf.day27ws2.service.ReviewService;


@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping(path = "/review", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postReview(@RequestParam String gameId,
                                            @RequestParam String user,
                                            @RequestParam Double rating,
                                            @RequestParam (required = false) String comment) 
    {
        JsonObject result = reviewService.insertReview(gameId, user, rating, comment); // Result contains success or failure
        
        if (result.containsKey("error"))
        {
            return ResponseEntity.badRequest().body(result.toString());
        }

        return ResponseEntity.ok(result.toString());
    }



    @PutMapping(path = "/review/{reviewId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postReview(@PathVariable String reviewId, 
                                            @RequestBody String payload) 
    {
        JsonReader jReader = Json.createReader(new StringReader(payload));
        JsonObject jPayload = jReader.readObject();

        Double newRating = jPayload.getJsonNumber("newRating").doubleValue();
        String newComment = jPayload.getString("newComment");

        JsonObject result = reviewService.updateReview(reviewId, newRating, newComment); // Result contains success or failure
        
        if (result.containsKey("error"))
        {
            return ResponseEntity.badRequest().body(result.toString());
        }

        return ResponseEntity.ok(result.toString());
    }
    
}
