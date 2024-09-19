package App.repository;


import App.model.AppEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//persistent memory
@Repository
public interface EventRepository extends JpaRepository<AppEvent, String> {
    AppEvent findBySummary(String summary);
}
