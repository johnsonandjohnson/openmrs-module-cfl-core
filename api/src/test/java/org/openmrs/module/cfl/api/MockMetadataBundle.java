/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cfl.api;

import org.openmrs.module.metadatadeploy.bundle.MetadataBundle;

/**
 * This is mock class for MetadataBundle which are implemented inside this module. The Activator
 * checks for the class's package name, whether it's 'org.openmrs.module.cfl.api' package.
 */
public class MockMetadataBundle implements MetadataBundle {
  private int installCallCounter = 0;

  @Override
  public void install() throws Exception {
    ++installCallCounter;
  }

  public int getInstallCallCounter() {
    return installCallCounter;
  }
}
