package nl.tudelft.sem.template.resourcepool.application;

import nl.tudelft.sem.template.resourcepool.domain.resourcepool.FacultyWasCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This event listener is automatically called when a domain entity is saved
 * which has stored events of type: FacultyWasCreated.
 */
@Component
public class FacultyWasCreatedListener {
    /**
     * The name of the function indicated which event is listened to.
     * The format is onEVENTNAME.
     *
     * @param event The event to react to
     */
    @EventListener
    public void onFacultyWasCreated(FacultyWasCreatedEvent event) {
        System.out.println("Faculty (" + event.getName() + ") was created.");
    }
}
