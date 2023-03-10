package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ScheduleRepository extends JpaRepository<DailySchedule, DailyScheduleId> {
    /**
     * Find requests for given day and resource pool.
     */
    Optional<DailySchedule> findByDayAndResourcePoolId(Calendar day, long resourcePoolId);

    boolean existsByDayAndResourcePoolId(Calendar day, long resourcePoolId);

    List<DailySchedule> findAllByResourcePoolId(long resourcePoolId);
}
