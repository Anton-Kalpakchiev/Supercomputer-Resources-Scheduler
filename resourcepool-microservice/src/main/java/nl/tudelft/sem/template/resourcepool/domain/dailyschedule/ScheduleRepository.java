package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import java.util.Date;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ScheduleRepository extends JpaRepository<DailySchedule, DailyScheduleId> {
    /**
     * Find requests for given day and resource pool.
     */
    Optional<DailySchedule> findByDayAndResourcePoolId(Date day, long resourcePoolId);

}
