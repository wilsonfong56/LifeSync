package App.util;

import App.model.AppEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class JsonUtils {

    public static String eventToJson(AppEvent event) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(event);
        return jsonString;
    }

    public static String eventsToJson(List<AppEvent> events) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonArrayString = gson.toJson(events);
        return jsonArrayString;
    }
}

