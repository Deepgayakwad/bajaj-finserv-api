package com.bajaj.bfhl.service;

import com.bajaj.bfhl.dto.BfhlRequest;
import com.bajaj.bfhl.dto.BfhlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Core service implementation for all BFHL business logic.
 *
 * Processing Rules:
 * - Null, empty, whitespace-only values are ignored (counted as invalid).
 * - Duplicates are detected before removal; processing happens on unique values.
 * - Alphanumeric strings (e.g., "A1B2") are split: letters → alphabets, digits → numbers.
 * - Negative numbers (e.g., "-10") and decimals (e.g., "25.5") are handled correctly.
 * - Pure alphabetic strings (e.g., "ABC") are kept as a whole in the alphabets list.
 * - Special characters are single non-alphanumeric chars.
 */
@Service
public class BfhlServiceImpl implements BfhlService {

    private static final Logger log = LoggerFactory.getLogger(BfhlServiceImpl.class);

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern PURE_ALPHA_PATTERN =
            Pattern.compile("^[a-zA-Z]+$");
    private static final Pattern ALPHANUMERIC_MIXED_PATTERN =
            Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]+$");
    private static final Pattern SPECIAL_CHAR_PATTERN =
            Pattern.compile("^[^a-zA-Z0-9\\s]+$");
    private static final Set<Character> VOWELS =
            Set.of('A', 'E', 'I', 'O', 'U');

    @Override
    public BfhlResponse process(BfhlRequest request, String requestId) {
        long startTime = System.currentTimeMillis();
        log.info("Processing request with requestId={}", requestId);

        List<String> rawData = request.getData();
        int totalReceived = (rawData == null) ? 0 : rawData.size();

        // ---- 1. Filter invalid values (null, empty, blank) ----
        List<String> validRaw = new ArrayList<>();
        int invalidCount = 0;

        if (rawData != null) {
            for (String item : rawData) {
                if (item == null || item.trim().isEmpty()) {
                    invalidCount++;
                } else {
                    validRaw.add(item.trim());
                }
            }
        }

        // ---- 2. Detect duplicates before dedup ----
        boolean containsDuplicates = hasDuplicates(validRaw);

        // ---- 3. Remove duplicates (preserve first occurrence order) ----
        List<String> deduped = deduplicatePreserveOrder(validRaw);
        int validProcessed = deduped.size();
        int uniqueElementCount = deduped.size();

        // ---- 4. Categorise each element ----
        List<BigDecimal> numbers = new ArrayList<>();
        List<String> alphabets = new ArrayList<>();       // individual letters or pure-alpha strings
        List<String> originalAlphaStrings = new ArrayList<>(); // original pure-alpha strings for longest/shortest
        List<String> specialChars = new ArrayList<>();

        for (String token : deduped) {
            if (NUMBER_PATTERN.matcher(token).matches()) {
                // Pure number (including negatives and decimals)
                numbers.add(new BigDecimal(token));

            } else if (PURE_ALPHA_PATTERN.matcher(token).matches()) {
                // Pure alphabetic string — keep as group AND register each letter
                originalAlphaStrings.add(token.toUpperCase());
                for (char c : token.toUpperCase().toCharArray()) {
                    alphabets.add(String.valueOf(c));
                }

            } else if (ALPHANUMERIC_MIXED_PATTERN.matcher(token).matches()) {
                // Alphanumeric — extract digits as number, letters as alphabets
                StringBuilder numPart = new StringBuilder();
                StringBuilder alphaPart = new StringBuilder();
                for (char c : token.toCharArray()) {
                    if (Character.isDigit(c)) numPart.append(c);
                    else if (Character.isLetter(c)) alphaPart.append(c);
                }
                if (numPart.length() > 0) {
                    numbers.add(new BigDecimal(numPart.toString()));
                }
                if (alphaPart.length() > 0) {
                    // Each extracted letter goes individually into alphabets
                    for (char c : alphaPart.toString().toUpperCase().toCharArray()) {
                        alphabets.add(String.valueOf(c));
                    }
                }

            } else if (SPECIAL_CHAR_PATTERN.matcher(token).matches()) {
                specialChars.add(token);
            }
            // anything else (mixed special+alpha etc.) is skipped
        }

        // ---- 5. Odd / Even split ----
        List<String> oddNumbers = new ArrayList<>();
        List<String> evenNumbers = new ArrayList<>();
        for (BigDecimal num : numbers) {
            try {
                long longVal = num.longValueExact();
                if (longVal % 2 == 0) evenNumbers.add(formatNumber(num));
                else oddNumbers.add(formatNumber(num));
            } catch (ArithmeticException e) {
                // Decimal — not integer, treat as even (can't be odd/even by standard definition)
                evenNumbers.add(formatNumber(num));
            }
        }

        // ---- 6. Sum ----
        BigDecimal sum = numbers.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        String sumStr = formatNumber(sum);

        // ---- 7. Largest / Smallest number ----
        String largestNumber = null;
        String smallestNumber = null;
        if (!numbers.isEmpty()) {
            largestNumber = formatNumber(Collections.max(numbers));
            smallestNumber = formatNumber(Collections.min(numbers));
        }

        // ---- 8. Sorted numbers ----
        List<String> sortedNumbers = numbers.stream()
                .sorted()
                .map(this::formatNumber)
                .collect(Collectors.toList());

        // ---- 9. Vowel / Consonant count ----
        int vowelCount = 0;
        int consonantCount = 0;
        for (String letter : alphabets) {
            char c = letter.toUpperCase().charAt(0);
            if (VOWELS.contains(c)) vowelCount++;
            else consonantCount++;
        }

        // ---- 10. Alphabet frequency ----
        Map<String, Integer> alphabetFrequency = new TreeMap<>();
        for (String letter : alphabets) {
            alphabetFrequency.merge(letter.toUpperCase(), 1, Integer::sum);
        }

        // ---- 11. Longest / Shortest alphabetic value ----
        String longestAlpha = null;
        String shortestAlpha = null;
        if (!originalAlphaStrings.isEmpty()) {
            longestAlpha = originalAlphaStrings.stream()
                    .max(Comparator.comparingInt(String::length))
                    .orElse(null);
            shortestAlpha = originalAlphaStrings.stream()
                    .min(Comparator.comparingInt(String::length))
                    .orElse(null);
        }

        // ---- 12. Build alphabets list for response ----
        // Include original pure-alpha strings AND individual extracted letters from alphanumeric
        List<String> responseAlphabets = buildResponseAlphabets(deduped);

        // ---- 13. Build response ----
        long processingTime = System.currentTimeMillis() - startTime;
        BfhlResponse response = new BfhlResponse();
        response.setSuccess(true);
        response.setRequestId(requestId);
        response.setOddNumbers(oddNumbers);
        response.setEvenNumbers(evenNumbers);
        response.setAlphabets(responseAlphabets);
        response.setSpecialCharacters(specialChars);
        response.setSum(sumStr);
        response.setLargestNumber(largestNumber);
        response.setSmallestNumber(smallestNumber);
        response.setAlphabetCount(alphabets.size());
        response.setNumberCount(numbers.size());
        response.setSpecialCharacterCount(specialChars.size());
        response.setContainsDuplicates(containsDuplicates);
        response.setUniqueElementCount(uniqueElementCount);
        response.setSortedNumbers(sortedNumbers);
        response.setVowelCount(vowelCount);
        response.setConsonantCount(consonantCount);
        response.setAlphabetFrequency(alphabetFrequency.isEmpty() ? null : alphabetFrequency);
        response.setLongestAlphabeticValue(longestAlpha);
        response.setShortestAlphabeticValue(shortestAlpha);
        response.setProcessingTimeMs(processingTime);
        response.setSummary(new BfhlResponse.Summary(totalReceived, validProcessed, invalidCount));

        log.info("Completed processing requestId={} in {}ms", requestId, processingTime);
        return response;
    }

    // ---------- Helper methods ----------

    /**
     * Builds the alphabets list for the response as per sample outputs:
     * - Pure alpha strings are kept as-is (uppercased).
     * - From alphanumeric strings, extracted letter groups are added.
     */
    private List<String> buildResponseAlphabets(List<String> deduped) {
        List<String> result = new ArrayList<>();
        for (String token : deduped) {
            if (PURE_ALPHA_PATTERN.matcher(token).matches()) {
                result.add(token.toUpperCase());
            } else if (ALPHANUMERIC_MIXED_PATTERN.matcher(token).matches()) {
                // Extract just letter portions as individual characters
                StringBuilder alphaPart = new StringBuilder();
                for (char c : token.toCharArray()) {
                    if (Character.isLetter(c)) alphaPart.append(c);
                }
                if (alphaPart.length() > 0) {
                    for (char c : alphaPart.toString().toUpperCase().toCharArray()) {
                        result.add(String.valueOf(c));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Detects duplicate values (case-insensitive for strings) in the valid list.
     */
    private boolean hasDuplicates(List<String> list) {
        Set<String> seen = new HashSet<>();
        for (String item : list) {
            if (!seen.add(item.toLowerCase())) return true;
        }
        return false;
    }

    /**
     * Removes duplicates while preserving first-occurrence order.
     * Case-insensitive comparison.
     */
    private List<String> deduplicatePreserveOrder(List<String> list) {
        Set<String> seen = new LinkedHashSet<>();
        List<String> result = new ArrayList<>();
        for (String item : list) {
            if (seen.add(item.toLowerCase())) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Formats a BigDecimal: removes trailing zeros after decimal point.
     * E.g., 10.00 → "10", 25.5 → "25.5", -100.75 → "-100.75"
     */
    private String formatNumber(BigDecimal num) {
        if (num == null) return null;
        BigDecimal stripped = num.stripTrailingZeros();
        // If scale is negative or zero, it's a whole number
        if (stripped.scale() <= 0) {
            return stripped.toPlainString();
        }
        return stripped.toPlainString();
    }
}
