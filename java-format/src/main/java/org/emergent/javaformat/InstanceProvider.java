package org.emergent.javaformat;

import org.emergent.javaformat.java.FormatterException;
import org.emergent.javaformat.java.JavaInputAstVisitor;

public interface InstanceProvider {

    static InstanceProvider getInstance() {
        return new InstanceProviderImpl();
    }

    StringUtil strings();

    ImportUtil imports();

    JavaInputAstVisitor newJavaInputAstVisitor(OpsBuilder builder, int indentMultiplier);

    static interface StringUtil {
        String stripIndent(String s);
    }

    static interface ImportUtil {

        /**
         * Removes unused imports from a source file. Imports that are only used in Javadoc are also removed, and the
         * references in Javadoc are replaced with fully qualified names.
         */
        String removeUnusedImports(final String contents) throws FormatterException;
    }
}
