package nl.tudelft.sem.template.resourcepool.domain.dailyschedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;


@Repository
public interface ScheduleRepository extends JpaRepository<DailySchedule, DailyScheduleId> {
    /**
     * Find requests for given day and resource pool.
     */
    Optional<DailySchedule> findByDayAndResourcePool(Date day, long resourcePoolId);

}
