package org.emergent.javaformat;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCImportBase;
import java.util.Set;
import org.emergent.javaformat.java.ImportUtilBase;

public class InstanceProviderJava23 extends InstanceProviderJava21 {

    @Override
    public ImportUtil imports() {
        return new ImportUtilJava23();
    }

    /**
     * Removes unused imports from a source file. Imports that are only used in Javadoc are also removed, and the
     * references in Javadoc are replaced with fully qualified names.
     */
    private static class ImportUtilJava23 extends ImportUtilBase implements ImportUtil {

        @Override
        protected RangeMap<Integer, String> buildReplacements(
                String contents,
                JCCompilationUnit unit,
                Set<String> usedNames,
                Multimap<String, Range<Integer>> usedInJavadoc) {
            RangeMap<Integer, String> replacements = TreeRangeMap.create();
            for (JCImportBase importTree : unit.getImports()) {
                String simpleName = getSimpleName(importTree);
                if (!isUnused(unit, usedNames, usedInJavadoc, importTree, simpleName)) {
                    continue;
                }
                // delete the import
                int endPosition = importTree.getEndPosition(unit.endPositions);
                endPosition = Math.max(CharMatcher.isNot(' ').indexIn(contents, endPosition), endPosition);
                String sep = Newlines.guessLineSeparator(contents);
                if (endPosition + sep.length() < contents.length()
                        && contents.subSequence(endPosition, endPosition + sep.length())
                                .toString()
                                .equals(sep)) {
                    endPosition += sep.length();
                }
                replacements.put(Range.closedOpen(importTree.getStartPosition(), endPosition), "");
            }
            return replacements;
        }
    }
}
