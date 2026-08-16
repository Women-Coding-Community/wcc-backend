package com.wcc.platform.repository;

import com.wcc.platform.domain.platform.feedback.Feedback;
import com.wcc.platform.domain.platform.feedback.FeedbackSearchCriteria;
import java.util.List;

/**
 * Repository interface for managing feedback entities. Provides methods to perform CRUD operations
 * and additional feedback-related queries on the data source.
 */
public interface FeedbackRepository extends CrudRepository<Feedback, Long> {

  /**
   * Retrieve all feedbacks matching the specified search criteria.
   *
   * @param criteria the search criteria to filter feedbacks
   * @return List of feedbacks
   */
  List<Feedback> getAll(FeedbackSearchCriteria criteria);

  /**
   * Approve feedback by ID.
   *
   * @param feedbackId the ID of the feedback to approve
   */
  void approveFeedback(Long feedbackId);

  /**
   * Set anonymous status of feedback.
   *
   * @param feedbackId the ID of the feedback
   * @param isAnonymous true to hide reviewer name, false to show reviewer name
   */
  void updateAnonymousStatus(Long feedbackId, Boolean isAnonymous);
}
