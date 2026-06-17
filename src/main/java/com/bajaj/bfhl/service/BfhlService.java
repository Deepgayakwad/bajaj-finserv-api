package com.bajaj.bfhl.service;

import com.bajaj.bfhl.dto.BfhlRequest;
import com.bajaj.bfhl.dto.BfhlResponse;

/**
 * Service interface for BFHL processing logic.
 * Follows SOLID Interface Segregation Principle.
 */
public interface BfhlService {

    /**
     * Process the incoming request and return the categorized response.
     *
     * @param request   the incoming request DTO
     * @param requestId the X-Request-Id header value
     * @return fully populated BfhlResponse
     */
    BfhlResponse process(BfhlRequest request, String requestId);
}
