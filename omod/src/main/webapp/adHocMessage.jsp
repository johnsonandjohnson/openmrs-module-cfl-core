<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>

<%--
  ~ This Source Code Form is subject to the terms of the Mozilla Public License,
  ~ v. 2.0. If a copy of the MPL was not distributed with this file, You can
  ~ obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
  ~ the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
  ~ <p>
  ~ Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
  ~ graphic logo is a trademark of OpenMRS Inc.
  --%>

<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js"/>
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/cflcore/scripts/adHocMessage.js"/>

<script type="text/javascript">
    let $sendConfirmationMessage = '<openmrs:message code="cfl.adHocMessage.send.confirm.msg"/>';

    /**
     * Load Patient Overview data.
     */
    const $patients = [];
    <c:forEach var="recipient" items="${model.recipients}">
    $patients.push([
        "${recipient.identifier}",
        "${recipient.givenName}",
        "${recipient.middleName}",
        "${recipient.familyName}",
        "${recipient.age}",
        "${recipient.gender}"
    ]);
    </c:forEach>
</script>

<h2><spring:message code="cfl.adHocMessage.title"/></h2>

<form method="post">
    <b class="boxHeader"><openmrs:message code="cfl.adHocMessage.parameters.title"/></b>
    <div class="box">
        <table>
            <tr>
                <th><openmrs:message code="cfl.adHocMessage.deliveryDateTime"/></th>
                <td>
                    <spring:bind path="model.messageRequest.deliveryDateTime">
                        <input required type="text" name="${status.expression}" size="20"
                               value="${status.value}" onfocus="showDateTimePicker(this)"/>
                        (<openmrs:message code="general.format"/>: <openmrs:dateTimePattern/>)
                        <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <th><openmrs:message code="cfl.adHocMessage.channel"/></th>
                <td>
                    <spring:bind path="model.messageRequest.callChannel">
                        <input type="hidden" name="_${status.expression}"/>
                        <span>
                            <input required type="checkbox" name="${status.expression}" id="callChannel"
                                   onClick="channelCheckboxClicked(this, 'callConfig')"
                                   <c:if test="${model.messageRequest.callChannel}">checked</c:if> />
                            <openmrs:message code="cfl.channelName.Call"/>
                        </span>
                        <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
                    </spring:bind>
                    <spring:bind path="model.messageRequest.smsChannel">
                        <input type="hidden" name="_${status.expression}"/>
                        <span>
                            <input required type="checkbox" name="${status.expression}" id="smsChannel"
                                   onClick="channelCheckboxClicked(this, 'messageConfig')"
                                   <c:if test="${model.messageRequest.smsChannel}">checked</c:if> />
                            <openmrs:message code="cfl.channelName.SMS"/>
                        </span>
                        <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
                    </spring:bind>
                    <spring:bind path="model.messageRequest.whatsAppChannel">
                        <input type="hidden" name="_${status.expression}"/>
                        <span>
                            <input required type="checkbox" name="${status.expression}" id="whatsAppChannel"
                                   onClick="channelCheckboxClicked(this, 'messageConfig')"
                                   <c:if test="${model.messageRequest.whatsAppChannel}">checked</c:if> />
                            <openmrs:message code="cfl.channelName.WhatsApp"/>
                        </span>
                        <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr id="callConfig">
                <th><openmrs:message code="cfl.adHocMessage.Call.config"/></th>
                <td>
                    <spring:bind path="model.messageRequest.callFlowName">
                        <input type="text" name="${status.expression}" value="${status.value}" size="35"/>
                        <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr id="messageConfig">
                <th><openmrs:message code="cfl.adHocMessage.SMS.config"/></th>
                <td>
                    <spring:bind path="model.messageRequest.message">
                        <textarea name="${status.expression}" rows="10" cols="124">${status.value}</textarea>
                        <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
                    </spring:bind>
                </td>
            </tr>
        </table>
    </div>

    <b class="boxHeader"><openmrs:message code="cfl.adHocMessage.patientFilters.title"/></b>
    <div class="box">
        <table>
            <c:forEach items="${model.messageRequest.filters}" var="filter" varStatus="filterStatus">
                <tr>
                    <th><openmrs:message code="${filter.label}"/></th>
                    <td>
                        <spring:nestedPath path="model.messageRequest.filters[${filterStatus.index}]">
                            <!-- Bind the value - editor depends on the filter type -->
                            <c:choose>
                                <c:when test="${filter.filterTypeName == 'STRING'}">
                                    <!-- Type text -->
                                    <spring:bind path="value">
                                        <input type="text" name="${status.expression}" value="${status.value}"/>
                                        <c:if test="${status.errorMessage != ''}">
                                            <span class="error">${status.errorMessage}</span>
                                        </c:if>
                                    </spring:bind>
                                </c:when>
                                <c:when test="${filter.filterTypeName == 'SELECT_ENTITY_UUID'}">
                                    <!-- Select from selectbox -->
                                    <form:select path="value">
                                        <form:option value="${null}"></form:option>
                                        <form:options
                                                items="${model.messageRequest.filters[filterStatus.index].options}"
                                                itemLabel="label" itemValue="value"/>
                                    </form:select>
                                </c:when>
                                <c:when test="${filter.filterTypeName == 'MULTI_SELECT_STRING'}">
                                    <c:if test="${not empty model.messageRequest.filters[filterStatus.index].options}">
                                        <form:checkboxes items="${model.messageRequest.filters[filterStatus.index].options}"
                                                         itemLabel="label" itemValue="value"
                                                         path="manyValues"/>
                                    </c:if>
                                </c:when>
                                <c:when test="${filter.filterTypeName == 'INTEGER'}">
                                    <!-- Select operator -->
                                    <form:select path="secondValue">
                                        <form:options items="${model.messageRequest.filters[filterStatus.index].options}"
                                                      itemLabel="label" itemValue="value"/>
                                    </form:select>
                                    <!-- Type number -->
                                    <spring:bind path="value">
                                        <input type="number" name="${status.expression}" value="${status.value}"/>
                                        <c:if test="${status.errorMessage != ''}">
                                            <span class="error">${status.errorMessage}</span>
                                        </c:if>
                                    </spring:bind>
                                </c:when>
                                <c:when test="${filter.filterTypeName == 'AGE_RANGE'}">
                                    <!-- Select from -->
                                    <spring:bind path="value">
                                        <label for="from-${filterStatus.index}"><openmrs:message code="common.from.label"/>:</label>
                                        <input type="number"
                                               id="from-${filterStatus.index}"
                                               name="${status.expression}"
                                               size="20"
                                               value="${status.value}">
                                        <c:if test="${status.errorMessage != ''}">
                                            <span class="error">${status.errorMessage}</span>
                                        </c:if>
                                    </spring:bind>
                                    <!-- Select to -->
                                    <spring:bind path="secondValue">
                                        <label for="to-${filterStatus.index}"><openmrs:message code="common.to.label"/>:</label>
                                        <input type="number"
                                               id="to-${filterStatus.index}"
                                               name="${status.expression}"
                                               size="20"
                                               value="${status.value}">
                                        <c:if test="${status.errorMessage != ''}">
                                            <span class="error">${status.errorMessage}</span>
                                        </c:if>
                                    </spring:bind>
                                </c:when>
                                <c:otherwise>
                                <span>
                                    <openmrs:message code="cfl.adHocMessage.unknownFilter.msg"/>${filter.filterTypeName}
                                </span>
                                </c:otherwise>
                            </c:choose>
                        </spring:nestedPath>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>

    <br/>

    <input type="submit" value="<openmrs:message code="cfl.adHocMessage.filter"/>" name="filter">

    <br/>
    <br/>

    <c:if test="${model.recipients != null}">
        <b class="boxHeader"><openmrs:message code="cfl.adHocMessage.patientOverview.title"/></b>
        <div>
            <!-- See: adHocMessage.js -->
            <table id="patientOverview">
                <thead>
                <tr>
                    <th class="identifierColumn"><spring:message code="cfl.patient.identifier"/></th>
                    <th class="givenNameColumn"><spring:message code="cfl.patient.givenName"/></th>
                    <th class="middleNameColumn"><spring:message code="cfl.patient.middleName"/></th>
                    <th class="familyNameColumn"><spring:message code="cfl.patient.familyName"/></th>
                    <th class="ageColumn"><spring:message code="cfl.patient.age"/></th>
                    <th class="genderColumn"><spring:message code="cfl.patient.gender"/></th>
                </tr>
                </thead>
            </table>
        </div>
    </c:if>

    <br/>

    <input id="send-btn" type="submit" value="<openmrs:message code="cfl.adHocMessage.send"/>" name="send"
           <c:if test="${model.recipients == null}">hidden</c:if> >
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>
