package org.getaviz.generator.abap.city.steps;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.OutputFormatHelper;
import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.ACityElement;
import org.getaviz.generator.abap.city.repository.ACityRepository;

import java.util.Collection;

public class ACityAFrameExporter {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;

    private ACityRepository repository;

    public ACityAFrameExporter(ACityRepository aCityRepository,SettingsConfiguration config) {
        this.config = config;

        repository = aCityRepository;
    }

    public String createAFrameExportFile(){

        StringBuilder aFrameExport = new StringBuilder();

        aFrameExport.append(OutputFormatHelper.AFrameHead());

        aFrameExport.append(createAFrameRepositoryExport());

        aFrameExport.append(OutputFormatHelper.AFrameTail());

        return aFrameExport.toString();
    }

    private String createAFrameRepositoryExport() {
        StringBuilder builder = new StringBuilder();

        Collection<ACityElement> floors = repository.getElementsByType(ACityElement.ACityType.Floor);
        builder.append(createElementsExport(floors));

        Collection<ACityElement> chimneys = repository.getElementsByType(ACityElement.ACityType.Chimney);
        builder.append(createElementsExport(chimneys));

        Collection<ACityElement> buildings = repository.getElementsByType(ACityElement.ACityType.Building);
        builder.append(createElementsExport(buildings));

        Collection<ACityElement> districts = repository.getElementsByType(ACityElement.ACityType.District);
        builder.append(createElementsExport(districts));

        return builder.toString();
    }

    private String createElementsExport(Collection<ACityElement> elements) {
        StringBuilder builder = new StringBuilder();
        for (ACityElement element: elements) {
            builder.append(createACityElementExport(element));
        }
        return builder.toString();
    }


    private String createACityElementExport(ACityElement element){
        StringBuilder builder = new StringBuilder();

        builder.append("<" + getShapeExport(element.getShape()) + " id=\"" + element.getHash() + "\"");
        builder.append("\n");
        builder.append("\t position=\"" + element.getXPosition() + " " + element.getYPosition() + " " + element.getZPosition() + "\"");
        builder.append("\n");
        builder.append("\t height=\"" + element.getHeight() + "\"");
        builder.append("\n");

        if(element.getShape() == ACityElement.ACityShape.box){
            builder.append("\t width=\"" + element.getWidth() + "\"");
            builder.append("\n");
            builder.append("\t depth=\"" + element.getLength() + "\"");
            builder.append("\n");
        } else {
            builder.append("\t radius=\"" + (element.getWidth() / 2) + "\"");
            builder.append("\n");
        }



        //builder.append("\t shader=\"flat\"");
        //builder.append("\n");
        //builder.append("\t flat-shading=\"true\"");
        //builder.append("\n");

        builder.append("\t color=\"" + element.getColor() + "\"");
        builder.append("\n");
        builder.append("\t shadow");
        builder.append(">");

        builder.append("\n");

        builder.append("</" + getShapeExport(element.getShape()) + ">");
        builder.append("\n");
        return builder.toString();
    }

    private String getShapeExport(ACityElement.ACityShape shape) {
        switch (shape){
            case box: return "a-box";
            case cylinder: return "a-cylinder";
            case cone: return "a-cone";
        }
        return "a-sphere";
    }


}
