package org.jboss.wolf.validator.impl;

import static org.jboss.wolf.validator.impl.ValidatorSupport.listPomFiles;

import java.io.File;
import java.util.Collection;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.jboss.wolf.validator.Validator;
import org.jboss.wolf.validator.ValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(ModelValidator.class);

    @Resource(name = "modelValidatorFilter")
    private IOFileFilter fileFilter;
    @Inject
    private ModelBuilder modelBuilder;
    @Inject
    private ModelBuildingRequest modelBuildingRequestTemplate;

    @Override
    public void validate(ValidatorContext ctx) {
        logger.debug("start...");
        Collection<File> pomFiles = listPomFiles(ctx.getValidatedRepository(), fileFilter);
        for (File pomFile : pomFiles) {
            if (!ctx.getExceptions(pomFile).isEmpty()) {
                logger.debug("skipping `{}`, because already contains exceptions", pomFile);
                continue;
            }
            logger.debug("validate `{}`", pomFile);
            validate(ctx, pomFile);
        }
    }

    private void validate(ValidatorContext ctx, File pomFile) {
        DefaultModelBuildingRequest request = new DefaultModelBuildingRequest(modelBuildingRequestTemplate);
        request.setPomFile(pomFile);
        request.setModelSource(new FileModelSource(pomFile));
        request.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MAVEN_3_0);
        try {
            modelBuilder.build(request);
        } catch (ModelBuildingException e) {
            ctx.addException(pomFile, e);
        }
    }

}