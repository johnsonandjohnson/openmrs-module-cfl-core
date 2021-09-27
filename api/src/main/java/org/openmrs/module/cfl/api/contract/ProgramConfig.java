package org.openmrs.module.cfl.api.contract;

public class ProgramConfig {

    private String name;

    private String programFormName;

    private String programFormEncounterTypeUuid;

    private String discontinuationFormName;

    private String discontinuationFormEncounterTypeUuid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgramFormName() {
        return programFormName;
    }

    public void setProgramFormName(String programFormName) {
        this.programFormName = programFormName;
    }

    public String getProgramFormEncounterTypeUuid() {
        return programFormEncounterTypeUuid;
    }

    public void setProgramFormEncounterTypeUuid(String programFormEncounterTypeUuid) {
        this.programFormEncounterTypeUuid = programFormEncounterTypeUuid;
    }

    public String getDiscontinuationFormName() {
        return discontinuationFormName;
    }

    public void setDiscontinuationFormName(String discontinuationFormName) {
        this.discontinuationFormName = discontinuationFormName;
    }

    public String getDiscontinuationFormEncounterTypeUuid() {
        return discontinuationFormEncounterTypeUuid;
    }

    public void setDiscontinuationFormEncounterTypeUuid(String discontinuationFormEncounterTypeUuid) {
        this.discontinuationFormEncounterTypeUuid = discontinuationFormEncounterTypeUuid;
    }
}
