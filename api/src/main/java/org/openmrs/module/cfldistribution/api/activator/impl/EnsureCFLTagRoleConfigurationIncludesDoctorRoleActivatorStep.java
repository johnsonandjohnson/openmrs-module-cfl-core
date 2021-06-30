package org.openmrs.module.cfldistribution.api.activator.impl;

import org.apache.commons.logging.Log;
import org.openmrs.Role;
import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CFLConstants;
import org.openmrs.module.cfldistribution.api.activator.ModuleActivatorStep;
import org.openmrs.module.cfldistribution.api.metadata.RolePrivilegeProfilesMetadata;
import org.openmrs.module.patientflags.Tag;
import org.openmrs.module.patientflags.api.FlagService;

/**
 * The bean defined in moduleApplicationContext.xml because OpenMRS performance issues with annotated beans.
 */
public class EnsureCFLTagRoleConfigurationIncludesDoctorRoleActivatorStep implements ModuleActivatorStep {
    @Override
    public int getOrder() {
        return 60;
    }

    @Override
    public void startup(Log log) throws Exception {
        final FlagService flagService = Context.getService(FlagService.class);

        final Tag cflTag = flagService.getTagByUuid(CFLConstants.CFL_TAG_UUID);
        final Role doctorRole = Context.getUserService().getRole(RolePrivilegeProfilesMetadata.DOCTOR_PRIVILEGE_LEVEL);

        for (final Role cflTagRole : cflTag.getRoles()) {
            if (cflTagRole.equals(doctorRole)) {
                // The configuration is ok
                return;
            }
        }

        // The configuration is not ok
        cflTag.getRoles().add(doctorRole);
        flagService.saveTag(cflTag);
    }
}
