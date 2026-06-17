package com.bajaj.bfhl.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * DTO representing the full API response for POST /bfhl
 * Fields with null values are excluded from the JSON output.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BfhlResponse {

    @JsonProperty("is_success")
    private boolean isSuccess;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("odd_numbers")
    private List<String> oddNumbers;

    @JsonProperty("even_numbers")
    private List<String> evenNumbers;

    @JsonProperty("alphabets")
    private List<String> alphabets;

    @JsonProperty("special_characters")
    private List<String> specialCharacters;

    @JsonProperty("sum")
    private String sum;

    @JsonProperty("largest_number")
    private String largestNumber;

    @JsonProperty("smallest_number")
    private String smallestNumber;

    @JsonProperty("alphabet_count")
    private Integer alphabetCount;

    @JsonProperty("number_count")
    private Integer numberCount;

    @JsonProperty("special_character_count")
    private Integer specialCharacterCount;

    @JsonProperty("contains_duplicates")
    private Boolean containsDuplicates;

    @JsonProperty("unique_element_count")
    private Integer uniqueElementCount;

    @JsonProperty("sorted_numbers")
    private List<String> sortedNumbers;

    @JsonProperty("vowel_count")
    private Integer vowelCount;

    @JsonProperty("consonant_count")
    private Integer consonantCount;

    @JsonProperty("alphabet_frequency")
    private Map<String, Integer> alphabetFrequency;

    @JsonProperty("longest_alphabetic_value")
    private String longestAlphabeticValue;

    @JsonProperty("shortest_alphabetic_value")
    private String shortestAlphabeticValue;

    @JsonProperty("processing_time_ms")
    private Long processingTimeMs;

    @JsonProperty("summary")
    private Summary summary;

    // ---- Nested Summary DTO ----
    public static class Summary {

        @JsonProperty("total_elements_received")
        private int totalElementsReceived;

        @JsonProperty("valid_elements_processed")
        private int validElementsProcessed;

        @JsonProperty("invalid_elements_ignored")
        private int invalidElementsIgnored;

        public Summary() {}

        public Summary(int totalElementsReceived, int validElementsProcessed, int invalidElementsIgnored) {
            this.totalElementsReceived = totalElementsReceived;
            this.validElementsProcessed = validElementsProcessed;
            this.invalidElementsIgnored = invalidElementsIgnored;
        }

        public int getTotalElementsReceived() { return totalElementsReceived; }
        public int getValidElementsProcessed() { return validElementsProcessed; }
        public int getInvalidElementsIgnored() { return invalidElementsIgnored; }
        public void setTotalElementsReceived(int v) { this.totalElementsReceived = v; }
        public void setValidElementsProcessed(int v) { this.validElementsProcessed = v; }
        public void setInvalidElementsIgnored(int v) { this.invalidElementsIgnored = v; }
    }

    // ---- Getters & Setters ----
    @JsonIgnore
    public boolean isSuccess() { return isSuccess; }
    public void setSuccess(boolean success) { isSuccess = success; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public List<String> getOddNumbers() { return oddNumbers; }
    public void setOddNumbers(List<String> oddNumbers) { this.oddNumbers = oddNumbers; }

    public List<String> getEvenNumbers() { return evenNumbers; }
    public void setEvenNumbers(List<String> evenNumbers) { this.evenNumbers = evenNumbers; }

    public List<String> getAlphabets() { return alphabets; }
    public void setAlphabets(List<String> alphabets) { this.alphabets = alphabets; }

    public List<String> getSpecialCharacters() { return specialCharacters; }
    public void setSpecialCharacters(List<String> specialCharacters) { this.specialCharacters = specialCharacters; }

    public String getSum() { return sum; }
    public void setSum(String sum) { this.sum = sum; }

    public String getLargestNumber() { return largestNumber; }
    public void setLargestNumber(String largestNumber) { this.largestNumber = largestNumber; }

    public String getSmallestNumber() { return smallestNumber; }
    public void setSmallestNumber(String smallestNumber) { this.smallestNumber = smallestNumber; }

    public Integer getAlphabetCount() { return alphabetCount; }
    public void setAlphabetCount(Integer alphabetCount) { this.alphabetCount = alphabetCount; }

    public Integer getNumberCount() { return numberCount; }
    public void setNumberCount(Integer numberCount) { this.numberCount = numberCount; }

    public Integer getSpecialCharacterCount() { return specialCharacterCount; }
    public void setSpecialCharacterCount(Integer specialCharacterCount) { this.specialCharacterCount = specialCharacterCount; }

    public Boolean getContainsDuplicates() { return containsDuplicates; }
    public void setContainsDuplicates(Boolean containsDuplicates) { this.containsDuplicates = containsDuplicates; }

    public Integer getUniqueElementCount() { return uniqueElementCount; }
    public void setUniqueElementCount(Integer uniqueElementCount) { this.uniqueElementCount = uniqueElementCount; }

    public List<String> getSortedNumbers() { return sortedNumbers; }
    public void setSortedNumbers(List<String> sortedNumbers) { this.sortedNumbers = sortedNumbers; }

    public Integer getVowelCount() { return vowelCount; }
    public void setVowelCount(Integer vowelCount) { this.vowelCount = vowelCount; }

    public Integer getConsonantCount() { return consonantCount; }
    public void setConsonantCount(Integer consonantCount) { this.consonantCount = consonantCount; }

    public Map<String, Integer> getAlphabetFrequency() { return alphabetFrequency; }
    public void setAlphabetFrequency(Map<String, Integer> alphabetFrequency) { this.alphabetFrequency = alphabetFrequency; }

    public String getLongestAlphabeticValue() { return longestAlphabeticValue; }
    public void setLongestAlphabeticValue(String longestAlphabeticValue) { this.longestAlphabeticValue = longestAlphabeticValue; }

    public String getShortestAlphabeticValue() { return shortestAlphabeticValue; }
    public void setShortestAlphabeticValue(String shortestAlphabeticValue) { this.shortestAlphabeticValue = shortestAlphabeticValue; }

    public Long getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(Long processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }
}
