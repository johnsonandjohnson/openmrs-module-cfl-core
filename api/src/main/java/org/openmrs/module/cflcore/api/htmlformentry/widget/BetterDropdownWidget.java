/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.cflcore.api.htmlformentry.widget;

import org.openmrs.module.htmlformentry.FormEntryContext;
import org.openmrs.module.htmlformentry.widget.Option;
import org.openmrs.module.htmlformentry.widget.SingleOptionWidget;
import org.openmrs.module.htmlformentry.widget.WidgetFactory;

public class BetterDropdownWidget extends SingleOptionWidget {
  public String generateHtml(FormEntryContext context) {
    if (context.getMode() != FormEntryContext.Mode.VIEW) {
      return renderEditMode(context);
    } else {
      return renderViewMode();
    }
  }

  private String renderEditMode(FormEntryContext context) {
    final StringBuilder sb = new StringBuilder();
    final String fieldName = context.getFieldName(this);
    sb.append("<select id=\"")
        .append(fieldName)
        .append("\" name=\"")
        .append(fieldName)
        .append("\">");

    for (Option option : this.getOptions()) {
      boolean selected = option.isSelected();
      if (!selected) {
        selected =
            this.getInitialValue() == null
                ? option.getValue().equals("")
                : this.getInitialValue().equals(option.getValue());
      }

      sb.append("<option value=\"").append(option.getValue()).append("\"");
      if (selected) {
        sb.append(" selected=\"true\"");
      }
      if (option.isRetired()) {
        sb.append(" style=\"display: none;\"");
      }
      sb.append(">").append(option.getLabel()).append("</option>");
    }

    sb.append("</select>");
    return sb.toString();
  }

  private String renderViewMode() {
    if (this.getInitialValue() == null) {
      return WidgetFactory.displayDefaultEmptyValue();
    } else {
      String displayValue = "";

      boolean found = false;
      for (Option option : this.getOptions()) {
        if (this.getInitialValue().equals(option.getValue())) {
          displayValue = option.getLabel();
          found = true;
          break;
        }
      }

      if (!found) {
        displayValue = this.getInitialValue();
      }

      return WidgetFactory.displayValue(displayValue);
    }
  }
}
