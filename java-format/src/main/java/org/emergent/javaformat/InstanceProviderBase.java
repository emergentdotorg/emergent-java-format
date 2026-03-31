package org.emergent.javaformat;

import java.util.List;
import java.util.stream.Collectors;
import org.emergent.javaformat.java.ImportUtilBase;
import org.emergent.javaformat.java.JavaBaseInputAstVisitor;
import org.emergent.javaformat.java.JavaInputAstVisitor;

public class InstanceProviderBase implements InstanceProvider {

    @Override
    public StringUtil strings() {
        return new StringUtilImpl();
    }

    @Override
    public ImportUtil imports() {
        return new ImportUtilBase();
    }

    @Override
    public JavaInputAstVisitor newJavaInputAstVisitor(OpsBuilder builder, int indentMultiplier) {
        return new JavaBaseInputAstVisitor(builder, indentMultiplier);
    }

    protected static class StringUtilImpl implements StringUtil {

        public String stripIndent(String s) {
            int length = s.length();
            if (length == 0) {
                return "";
            }
            char lastChar = s.charAt(length - 1);
            boolean optOut = lastChar == '\n' || lastChar == '\r';
            List<String> lines = s.lines().collect(Collectors.toList());
            final int outdent = optOut ? 0 : outdent(lines);
            return lines.stream()
                    .map(line -> {
                        int firstNonWhitespace = indexOfNonWhitespace(line);
                        int lastNonWhitespace = lastIndexOfNonWhitespace(line);
                        int incidentalWhitespace = Math.min(outdent, firstNonWhitespace);
                        return firstNonWhitespace > lastNonWhitespace
                                ? "" : line.substring(incidentalWhitespace, lastNonWhitespace);
                    })
                    .collect(Collectors.joining("\n", "", optOut ? "\n" : ""));
        }

        private static int outdent(List<String> lines) {
            // Note: outdent is guaranteed to be zero or positive number.
            // If there isn't a non-blank line then the last must be blank
            int outdent = Integer.MAX_VALUE;
            for (String line : lines) {
                int leadingWhitespace = indexOfNonWhitespace(line);
                if (leadingWhitespace != line.length()) {
                    outdent = Integer.min(outdent, leadingWhitespace);
                }
            }
            String lastLine = lines.get(lines.size() - 1);
            if (lastLine.isBlank()) {
                outdent = Integer.min(outdent, lastLine.length());
            }
            return outdent;
        }

        private static int indexOfNonWhitespace(String value) {
            int length = value.length();
            int left = 0;
            while (left < length) {
                char ch = value.charAt(left);
                if (!Character.isWhitespace(ch)) {
                    break;
                }
                left++;
            }
            return left;
        }

        private static int lastIndexOfNonWhitespace(String value) {
            int length = value.length();
            int right = length;
            while (0 < right) {
                char ch = value.charAt(right - 1);
                if (!Character.isWhitespace(ch)) {
                    break;
                }
                right--;
            }
            return right;
        }
    }
}
