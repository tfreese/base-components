/**
 * Created: 21.04.2020
 */

package de.freese.base.core.codegen;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.Externalizable;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import de.freese.base.core.codegen.model.ClassModel;
import de.freese.base.core.codegen.model.FieldModel;
import de.freese.base.core.codegen.writer.CodeWriter;
import de.freese.base.core.codegen.writer.JavaCodeWriter;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class TestCodeGen
{
    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    void testJavaCodeGen() throws Exception
    {
        ClassModel classModel = new ClassModel("MyTest");
        classModel.setAddFullConstructor(true);
        classModel.setSerializeable(false);
        classModel.setPackageName("de.freese.test");
        classModel.addComment("Test-JavaCodeGenerator");
        classModel.addComment("@author Thomas Freese");

        classModel.addAnnotation("@Resource");
        classModel.addImport(Resource.class);

        classModel.addInterface(Externalizable.class);

        // Attribute
        FieldModel fieldModel = classModel.addField("myInt", int.class);
        fieldModel.setDefaultValueAsString("0");
        fieldModel.addComment("int-Field");

        fieldModel.addAnnotation("@Autowired");
        classModel.addImport(Autowired.class);

        fieldModel = classModel.addField("myBoolean", Boolean.class);
        fieldModel.setDefaultValueAsString("null");
        fieldModel.addComment("Boolean-Field");

        fieldModel = classModel.addField("myList", List.class);
        fieldModel.addComment("List-Field");

        CodeWriter codeWriter = new JavaCodeWriter();
        codeWriter.write(classModel, System.out);

        assertTrue(true);
    }
}
