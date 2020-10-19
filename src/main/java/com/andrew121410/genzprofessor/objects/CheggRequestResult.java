package com.andrew121410.genzprofessor.objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CheggRequestResult {
    private final List<File> files;
    private final Result result;

    public enum Result {
        SUCCESS,
        FAILED_UNKNOWN_REASON,
        FAILED_TEXTBOOK_SOLUTION;

        public boolean hasFailed() {
            switch (this) {
                case FAILED_UNKNOWN_REASON:
                case FAILED_TEXTBOOK_SOLUTION:
                    return true;
            }
            return false;
        }
    }
}

