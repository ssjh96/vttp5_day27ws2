package vttp5.paf.day27ws2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.json.JsonObject;
import vttp5.paf.day27ws2.service.ReviewService;


@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping(path = "/review", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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
    
    
}
