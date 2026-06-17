package com.bajaj.bfhl.controller;

import com.bajaj.bfhl.dto.BfhlRequest;
import com.bajaj.bfhl.dto.BfhlResponse;
import com.bajaj.bfhl.service.BfhlService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing the POST /bfhl endpoint.
 *
 * Accepts:
 *   - X-Request-Id header (optional, defaults to "UNKNOWN")
 *   - JSON body: { "data": [...] }
 *
 * Returns HTTP 200 with full BfhlResponse JSON.
 */
@RestController
@RequestMapping("/bfhl")
public class BfhlController {

    private static final Logger log = LoggerFactory.getLogger(BfhlController.class);

    private final BfhlService bfhlService;

    public BfhlController(BfhlService bfhlService) {
        this.bfhlService = bfhlService;
    }

    /**
     * POST /bfhl
     * Processes a mixed array of inputs and returns categorized data.
     *
     * @param requestId the X-Request-Id header (optional)
     * @param request   the request body with "data" array
     * @return ResponseEntity with BfhlResponse
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<BfhlResponse> process(
            @RequestHeader(value = "X-Request-Id", defaultValue = "UNKNOWN") String requestId,
            @Valid @RequestBody BfhlRequest request
    ) {
        log.info("Received POST /bfhl | X-Request-Id={}", requestId);
        BfhlResponse response = bfhlService.process(request, requestId);
        return ResponseEntity.ok(response);
    }
}
