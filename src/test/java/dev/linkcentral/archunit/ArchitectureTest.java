package dev.linkcentral.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;

public class ArchitectureTest {

    @Test
    public void validateLayeredArchitecture() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("dev.linkcentral");

        Architectures.LayeredArchitecture architectureRule = Architectures.layeredArchitecture()
                .layer("Presentation").definedBy("dev.linkcentral.presentation..")
                .layer("Service").definedBy("dev.linkcentral.service..")
                .layer("Database").definedBy("dev.linkcentral.database..")
                .layer("Infrastructure").definedBy("dev.linkcentral.infrastructure..")
                .layer("Common").definedBy("dev.linkcentral.common..")

                .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Presentation")
                .whereLayer("Database").mayOnlyBeAccessedByLayers("Service")
                .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Service", "Presentation");

        architectureRule.check(importedClasses);
    }

}
