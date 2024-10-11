package App.util;

import com.google.api.client.util.DateTime;

import java.util.List;

public class DateUtils {

    public static String dayOfWeek(DateTime date) {
        String[] tokens = date.toString().substring(0, date.toString().indexOf("T")).split("-");
        int year = Integer.parseInt(tokens[0]);
        int month = Integer.parseInt(tokens[1]);
        int day = Integer.parseInt(tokens[2]);
        if (month < 3) {
            month += 12;
            year -= 1;
        }

        int k = year % 100;
        int j = year / 100;

        int dayOfWeek = (day + 13*(month+1)/5 + k + k/4 + j/4 + 5*j) % 7;

        switch (dayOfWeek) {
            case 0: return "Saturday";
            case 1: return "Sunday";
            case 2: return "Monday";
            case 3: return "Tuesday";
            case 4: return "Wednesday";
            case 5: return "Thursday";
            case 6: return "Friday";
            default: return "Invalid day";
        }
    }

}
