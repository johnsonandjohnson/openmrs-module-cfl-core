package org.openmrs.module.cfl.api.metadata;

import org.openmrs.module.cfl.api.constant.RolePrivilegeConstants;
import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.idSet;
import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.role;

/**
 * Metadata bundle which is used to install based CFL role profiles
 */
public class RolePrivilegeProfilesMetadata extends AbstractMetadataBundle {

    /**
     * Performs the installation of the metadata items
     * @throws Exception if an error occurs
     */
    @Override
    public void install() throws Exception {
        installAnalystRole();
        installDoctorRole();
    }

    private void installAnalystRole() {
        install(role(RolePrivilegeConstants.ANALYST_PRIVILEGE_LEVEL,
                RolePrivilegeConstants.ANALYST_PRIVILEGE_LEVEL_DESCRIPTION,
                idSet(), RolePrivilegeConstants.ANALYST_PRIVILEGES));
    }

    private void installDoctorRole() {
        install(role(RolePrivilegeConstants.DOCTOR_PRIVILEGE_LEVEL,
                RolePrivilegeConstants.DOCTOR_PRIVILEGE_LEVEL_DESCRIPTION,
                idSet(), RolePrivilegeConstants.DOCTOR_PRIVILEGES));
    }
}
