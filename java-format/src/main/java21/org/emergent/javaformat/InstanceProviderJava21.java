package org.emergent.javaformat;

import org.emergent.javaformat.java.JavaInputAstVisitor;
import org.emergent.javaformat.java.java21.Java21InputAstVisitor;

public class InstanceProviderJava21 extends InstanceProviderJava14 implements InstanceProvider {

    @Override
    public StringUtil strings() {
        return String::stripIndent;
    }

    @Override
    public JavaInputAstVisitor newJavaInputAstVisitor(OpsBuilder builder, int indentMultiplier) {
        return new Java21InputAstVisitor(builder, indentMultiplier);
    }
}
