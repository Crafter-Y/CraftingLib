package de.craftery.craftinglib.annotation;

import de.craftery.craftinglib.annotation.annotations.Entry;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("de.craftery.craftinglib.annotation.annotations.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CraftingLibAnnotationProcessor extends AbstractProcessor {
    private static final DateTimeFormatter dFormat = DateTimeFormatter.ofPattern( "yyyy/MM/dd HH:mm:ss", Locale.ENGLISH );

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Entry.class);
        if (elements.size() > 1) {
            error("There cannot be more than one entry point to your plugin/mod.");
            return false;
        }
        if (elements.isEmpty()) {
            return false;
        }

        Map<String, Object> yml = new HashMap<>();

        Element entryElement = elements.iterator().next();
        if (!(entryElement instanceof TypeElement)) {
            error("Entry element must be a class");
            return false;
        }

        TypeElement entryClass = (TypeElement) entryElement;
        String entryMain = entryClass.getQualifiedName().toString();

        if (entryClass.getAnnotation(Entry.class) == null) {
            error("Entry point is not annotated with @Entry annotation.");
            return false;
        }

        String entryName = entryClass.getAnnotation(Entry.class).name();
        String entryVersion = entryClass.getAnnotation(Entry.class).version();

        yml.put("name", entryName);
        yml.put("version", entryVersion);
        yml.put("main", entryMain);
        yml.put("api-version", "1.13");
        String[] depends = new String[1];
        depends[0] = "CraftingLib";
        yml.put("depend", depends);

        try {
            Yaml yaml = new Yaml();
            FileObject file = this.processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
            try (Writer w = file.openWriter()) {
                w.append( "# Auto-generated plugin.yml, generated at " )
                        .append( LocalDateTime.now().format( dFormat ) )
                        .append( " by " )
                        .append( this.getClass().getName() )
                        .append( "\n\n" );
                String raw = yaml.dumpAs(yml, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
                w.write(raw);
                w.flush();
                w.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private void error(String message) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }
}
