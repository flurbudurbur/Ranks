package dev.flur.ranks.processor;

import dev.flur.ranks.command.CommandInfo;
import org.jetbrains.annotations.NotNull;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("dev.flur.ranks.command.CommandInfo")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class CommandAnnotationProcessor extends AbstractProcessor {

    private final Map<String, CommandData> commands = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnv) {
        // Collect all commands
        for (Element element : roundEnv.getElementsAnnotatedWith(CommandInfo.class)) {
            if (element instanceof TypeElement typeElement) {
                CommandInfo commandInfo = element.getAnnotation(CommandInfo.class);

                CommandData data = new CommandData(
                        commandInfo.name(),
                        commandInfo.description(),
                        commandInfo.permission()
                );

                commands.put(commandInfo.name(), data);

                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        "Found command: " + commandInfo.name() + " in class " + typeElement.getSimpleName()
                );
            }
        }

        // Generate plugin.yml fragment in the last round
        if (roundEnv.processingOver() && !commands.isEmpty()) {
            generatePluginYmlFragment();
        }

        return true;
    }

    private void generatePluginYmlFragment() {
        try {
            FileObject resource = processingEnv.getFiler().createResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    "commands.yml"
            );

            try (Writer writer = resource.openWriter()) {
                writer.write("# Auto-generated command definitions\n");
                writer.write("commands:\n");

                for (CommandData command : commands.values()) {
                    writer.write("  " + command.name() + ":\n");
                    writer.write("    description: \"" + command.description() + "\"\n");
                    writer.write("    usage: \"/" + command.name() + "\"\n");

                    if (!command.permission().isEmpty()) {
                        writer.write("    permission: \"" + command.permission() + "\"\n");
                    }

                    writer.write("\n");
                }
            }

            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Generated commands.yml with " + commands.size() + " commands"
            );

        } catch (IOException e) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "Failed to generate commands.yml: " + e.getMessage()
            );
        }
    }

    private record CommandData(String name, String description, String permission) {
    }
}