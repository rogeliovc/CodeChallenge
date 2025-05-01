package com.example.codechallenge;

public class TestResult {
    public final String title;
    public final String input;
    public final String expected;
    public final String output;
    public final String error;
    public final String compileOutput;
    public final String message;
    public final boolean passed;

    public TestResult(String title, String input, String expected, String output, String error, String compileOutput, String message, boolean passed) {
        this.title = title;
        this.input = input;
        this.expected = expected;
        this.output = output;
        this.error = error;
        this.compileOutput = compileOutput;
        this.message = message;
        this.passed = passed;
    }
}
