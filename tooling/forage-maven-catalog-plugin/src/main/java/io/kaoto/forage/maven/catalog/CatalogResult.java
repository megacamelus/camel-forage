package io.kaoto.forage.maven.catalog;

import java.io.File;
import java.util.List;

/**
 * Result object containing information about the catalog generation process.
 */
public class CatalogResult {

    private final int componentCount;
    private final List<File> generatedFiles;

    public CatalogResult(int componentCount, List<File> generatedFiles) {
        this.componentCount = componentCount;
        this.generatedFiles = generatedFiles;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public List<File> getGeneratedFiles() {
        return generatedFiles;
    }

    public int getGeneratedFileCount() {
        return generatedFiles.size();
    }
}
