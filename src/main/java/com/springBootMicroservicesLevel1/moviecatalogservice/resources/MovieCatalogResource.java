package com.springBootMicroservicesLevel1.moviecatalogservice.resources;

import com.springBootMicroservicesLevel1.moviecatalogservice.models.CatalogItem;
import com.springBootMicroservicesLevel1.moviecatalogservice.models.Movie;
import com.springBootMicroservicesLevel1.moviecatalogservice.models.Rating;
import com.springBootMicroservicesLevel1.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;
    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId)
    {
        //Step 1 get all related movie id
        UserRating ratings= restTemplate.getForObject("http://localhost:8081/ratingdata/users/" + userId, UserRating.class);
        return ratings.getUserRating().stream().map(rating->
                {
                    //for each movie id, call movie infor service and get details
                   Movie movie= restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
                   //Put them all together
                    return new CatalogItem(movie.getName(),"Test",rating.getRating());
                })
                .collect(Collectors.toList());
    }
    /*
    calling from WEB CLIENT
    Movie movie = webClientBuilder.build()
                            .get()
                            .uri("http://localhost:8082/movies/" + rating.getMovieId())
                            .retrieve()
                            .bodyToMono(Movie.class)  //mono means in this (It's a reactive way of saying we are getting an object back according to the call in future)
                            .block();

     */

    @GetMapping("/test")
    public String checkRunStatus()
    {
        return "Api is Running";
    }
}
