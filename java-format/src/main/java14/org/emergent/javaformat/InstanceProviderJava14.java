package org.emergent.javaformat;

import org.emergent.javaformat.java.JavaInputAstVisitor;
import org.emergent.javaformat.java.java14.Java14InputAstVisitor;

public class InstanceProviderJava14 extends InstanceProviderBase implements InstanceProvider {

    @Override
    public JavaInputAstVisitor newJavaInputAstVisitor(OpsBuilder builder, int indentMultiplier) {
        return new Java14InputAstVisitor(builder, indentMultiplier);
    }
}
