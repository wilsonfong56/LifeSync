package App.repository;


import App.model.AppEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//persistent memory
@Repository
public interface EventRepository extends JpaRepository<AppEvent, String> {
    List<AppEvent> findBySummary(String summary);
}
