package nus.iss.trainify.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class YoutubeService {

    private String search;
    
    RestTemplate restTemplate = new RestTemplate();

    @Value("${youtube.apikey}")
    private String apikey;

    private final String youtubeUrl = "https://youtube.googleapis.com/youtube/v3/search?part=snippet&maxResults=4&";

    public List<String> generateYoutubeVideosId(@RequestParam String location, @RequestParam String duration, @RequestParam String focus) {
        search = "q=workout%20" + location + "%20" + focus  + "&key=" + apikey;
        String url_youtubeSearch = youtubeUrl + search;
        
        // System.out.println(url_youtubeSearch);
        ResponseEntity<String> response = restTemplate.getForEntity(url_youtubeSearch, String.class);

        System.out.println("----------------");
        System.out.println(response);
        System.out.println("----------------");

        String responseBody = response.getBody().toString();
        JsonReader jReader = Json.createReader(new StringReader(responseBody));
        JsonObject jsonObject = jReader.readObject();
        List<String> videoIds = new ArrayList<>();

        // System.out.println(jsonObject);

        // Parse the JSON response and extract video IDs
        JsonArray items = jsonObject.getJsonArray("items");
        for (JsonValue item : items) {
            JsonObject videoIdObject = ((JsonObject) item).getJsonObject("id");
            if (videoIdObject.containsKey("videoId")) {
                String videoId = videoIdObject.getJsonString("videoId").getString();
                videoIds.add(videoId);
            }
        }
        // System.out.println(videoIds);
        return videoIds;
    }

}



