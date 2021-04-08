package org.openmrs.module.cfl.fragment.controller;

import org.openmrs.PatientProgram;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.cfl.api.util.DateUtil;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.fragment.action.FragmentActionResult;
import org.openmrs.ui.framework.fragment.action.SuccessResult;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Map;

public class DeactivateProgramFragmentController {

    private static final String DEACTIVATION_PROGRAM_DATE = "deactivateProgramDate";
    private static final String DEACTIVATION_PROGRAM_REASON = "deactivateProgramReason";
    private static final String RETURN_URL_PARAM_NAME = "returnUrl";

    public FragmentActionResult submit(@RequestParam("patientProgramId") Integer patientProgramId,
                                       @SpringBean("programWorkflowService") ProgramWorkflowService programService,
                                       HttpServletRequest request) throws ParseException {

        PatientProgram patientProgram = programService.getPatientProgram(patientProgramId);
        Map<String, String[]> parameterMap = request.getParameterMap();
        String deactivationProgramDate = getParamValues(parameterMap, DEACTIVATION_PROGRAM_DATE)[0];
        String deactivationProgramReason = getParamValues(parameterMap, DEACTIVATION_PROGRAM_REASON)[0];
        String returnUrl = getParamValues(parameterMap, RETURN_URL_PARAM_NAME)[0];

        patientProgram.setDateCompleted(DateUtil.parseStringToDate(deactivationProgramDate, "yyyy-MM-dd"));
        patientProgram.setVoided(true);
        patientProgram.setVoidReason(deactivationProgramReason);
        programService.savePatientProgram(patientProgram);

        return new SuccessResult(returnUrl);
    }

    private String[] getParamValues(Map<String, String[]> parameterMap, String paramName) {
        String[] paramValues = null;
        if (parameterMap.containsKey(paramName)) {
            paramValues = parameterMap.get(paramName);
        }
        return paramValues;
    }
}
