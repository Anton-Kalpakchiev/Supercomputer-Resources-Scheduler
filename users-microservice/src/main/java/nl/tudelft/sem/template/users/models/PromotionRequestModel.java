package nl.tudelft.sem.template.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model for requesting a promotion.
 */
@Data
@AllArgsConstructor
public class PromotionRequestModel {
    private String netId;
}
