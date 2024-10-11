package App.model;

import com.google.api.client.util.DateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
//
@Entity
@Table(name="events")
public class AppEvent {

    //private String calendarId;
    @Id
    private String eventId;
    private String summary;
    private DateTime startTime;
    private DateTime endTime;

    private int priority;
    private boolean isDynamic;

    public AppEvent() {}

    public AppEvent(String eventId, String summary, DateTime startTime, DateTime endTime, boolean isDynamic, int priority) {
        this.eventId = eventId;
        this.summary = summary;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDynamic = isDynamic;
        this.priority = priority;
    }


    public String getEventId() {
        return eventId;
    }

    public String getSummary() {
        return summary;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
    }


    @Override
    public String toString() {
        return "Event{" +
                " eventId='" + eventId + '\'' +
                ", summary='" + summary + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}