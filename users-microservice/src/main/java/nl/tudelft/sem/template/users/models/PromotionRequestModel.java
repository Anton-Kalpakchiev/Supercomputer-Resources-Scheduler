package nl.tudelft.sem.template.users.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for requesting a promotion.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRequestModel {
    private String netId;
}
