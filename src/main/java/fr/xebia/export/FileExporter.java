package fr.xebia.export;

import java.nio.file.Path;

@FunctionalInterface
public interface FileExporter<T> {

    void apply(T image, Path pdfFilePath, Path resultPath, int page);

}
