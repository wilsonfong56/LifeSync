package App.model;

import com.google.api.client.util.DateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Events")
public class AppEvent {

    //private String calendarId;
    @Id
    private String eventId;
    private String summary;
    private DateTime startTime;
    private DateTime endTime;

    private boolean isDynamic;

    public AppEvent() {}

    public AppEvent(String eventId, String summary, DateTime startTime, DateTime endTime, boolean isDynamic) {
        this.eventId = eventId;
        this.summary = summary;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDynamic = isDynamic;
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

    public boolean isDynamic() {
        return isDynamic;
    }

    public void setDynamic(boolean dynamic) {
        isDynamic = dynamic;
    }


    @java.lang.Override
    public java.lang.String toString() {
        return "Event{" +
                " eventId='" + eventId + '\'' +
                ", summary='" + summary + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}