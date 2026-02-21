package schedulemanager.integration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import schedulemanager.domain.DailyStatistics;
import schedulemanager.service.StatsService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import schedulemanager.domain.PlanBlock;
import schedulemanager.domain.ActualSession;
import com.google.gson.JsonArray;
import java.util.List;

import okhttp3.*;

/**
 * Client for communicating with the external AI API to get insights and recommendations.
 * 
 * <p>This client sends daily statistics and task comparisons to an external HTTP API
 * and receives insights and recommendations. If the API is unavailable, it provides
 * a fallback response.
 * 
 */
public class AiApiClient {
    private static final String DEFAULT_API_URL = "http://localhost:8080/api/insights";
    private final String apiUrl;
    private final OkHttpClient httpClient;
    private final Gson gson;
    
    /**
     * Constructs an AiApiClient with the default API URL.
     */
    public AiApiClient() {
        this(DEFAULT_API_URL);
    }
    
    /**
     * Constructs an AiApiClient with a custom API URL.
     * 
     * @param apiUrl the API endpoint URL
     */
    public AiApiClient(String apiUrl) {
        this.apiUrl = apiUrl;
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }
    
    /**
     * Generates insights and recommendations for a specific day.
     * 
     * <p>Sends daily statistics and task comparisons to the AI API and returns
     * insights. If the API is unavailable, returns a fallback message.
     * 
     * @param date the date for which to generate insights
     * @param statsService the stats service to compute statistics
     * @return insights and recommendations as a string
     */
    public String generateInsights(LocalDate date, StatsService statsService) {
        try {
            DailyStatistics stats = statsService.computeDailyStats(date);
            Map<Long, StatsService.TaskStats> taskStats = statsService.computeTaskStats(date);
            var planBlocks = statsService.getPlanBlocks(date);
            var actualSessions = statsService.getActualSessions(date);

            JsonObject requestBody = buildRequestBody(date, stats, taskStats, planBlocks, actualSessions);            

            
            //JsonObject requestBody = buildRequestBody(date, stats, taskStats);
            return sendRequest(requestBody);
        } catch (Exception e) {
            return getFallbackInsights(date);
        }
    }
    
    /**
     * Builds the JSON request body to send to the API.
     * 
     * @param date the date
     * @param stats daily statistics
     * @param taskStats task-to-task statistics
     * @return JSON object representing the request
     */
    private JsonObject buildRequestBody(
        LocalDate date,
        DailyStatistics stats,
        Map<Long, StatsService.TaskStats> taskStats,
        java.util.List<schedulemanager.domain.PlanBlock> planBlocks,
        java.util.List<schedulemanager.domain.ActualSession> actualSessions
    ) {
        JsonObject body = new JsonObject();
        body.addProperty("date", date.toString());
        body.addProperty("plannedMinutes", stats.getPlannedMinutes());
        body.addProperty("actualMinutes", stats.getActualMinutes());
        body.addProperty("overlapMinutes", stats.getOverlapMinutes());
        body.addProperty("quantitativeAccuracy", stats.getQuantitativeAccuracy());
        body.addProperty("temporalAccuracy", stats.getTemporalAccuracy());
        
        // Add plan calendar lines
        JsonArray planArr = new JsonArray();
        if (planBlocks != null) {
            for (PlanBlock b : planBlocks) {
                String line = String.format("%s-%s: %s",
                        b.getStartTime(), b.getEndTime(), b.getTitle());
                planArr.add(line);
            }
        }
        body.add("planCalendar", planArr);

        // Add actual calendar lines
        JsonArray actualArr = new JsonArray();
        if (actualSessions != null) {
            for (ActualSession s : actualSessions) {
                String line = String.format("%s-%s: %s",
                        s.getStartTime(), s.getEndTime(), s.getTitle());
                actualArr.add(line);
            }
        }
        body.add("actualCalendar", actualArr);

        // Add task statistics
        JsonObject tasksJson = new JsonObject();
        for (Map.Entry<Long, StatsService.TaskStats> entry : taskStats.entrySet()) {
            JsonObject taskJson = new JsonObject();
            taskJson.addProperty("plannedMinutes", entry.getValue().plannedMinutes);
            taskJson.addProperty("actualMinutes", entry.getValue().actualMinutes);
            taskJson.addProperty("overlapMinutes", entry.getValue().overlapMinutes);
            tasksJson.add(entry.getKey().toString(), taskJson);
        }
        body.add("tasks", tasksJson);
        
        return body;
    }
    
    /**
     * Sends the request to the AI API.
     * 
     * @param requestBody the JSON request body
     * @return the response from the API
     * @throws IOException if the request fails
     */
    private String sendRequest(JsonObject requestBody) throws IOException {
        RequestBody body = RequestBody.create(
            gson.toJson(requestBody),
            MediaType.parse("application/json"));
        
        Request request = new Request.Builder()
            .url(apiUrl)
            .post(body)
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject responseJson = gson.fromJson(response.body().string(), JsonObject.class);
                if (responseJson.has("insights")) {
                    return responseJson.get("insights").getAsString();
                }
                if (responseJson.has("recommendations")) {
                    return responseJson.get("recommendations").getAsString();
                }
                return responseJson.toString();
            } else {
                return getFallbackInsights(null);
            }
        }
    }
    
    /**
     * Returns fallback insights when the API is unavailable.
     * 
     * @param date the date (can be null)
     * @return fallback message
     */
    private String getFallbackInsights(LocalDate date) {
        return "AI API is currently unavailable. " +
               "Please check your connection or configure the API endpoint. " +
               (date != null ? "Date: " + date : "");
    }
}

