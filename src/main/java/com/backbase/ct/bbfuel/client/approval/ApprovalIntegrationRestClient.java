package com.backbase.ct.bbfuel.client.approval;

import static com.backbase.ct.bbfuel.data.ApprovalsDataGenerator.createDeletePolicyAssignmentRequest;
import static com.backbase.ct.bbfuel.data.ApprovalsDataGenerator.createPostApprovalTypeRequest;
import static com.backbase.ct.bbfuel.data.ApprovalsDataGenerator.createPostBulkApprovalTypesAssignmentRequest;
import static com.backbase.ct.bbfuel.data.ApprovalsDataGenerator.createPostPolicyAssignmentBulkRequest;
import static com.backbase.ct.bbfuel.data.ApprovalsDataGenerator.createPostPolicyRequest;
import static com.backbase.ct.bbfuel.data.ApprovalsDataGenerator.createPostPolicyRequestWithZeroPolicyItems;
import static com.backbase.ct.bbfuel.data.CommonConstants.PROPERTY_APPROVALS_BASE_URI;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

import com.backbase.ct.bbfuel.client.common.AbstractRestClient;
import com.backbase.dbs.approval.integration.spec.IntegrationApprovalTypeAssignmentDto;
import com.backbase.dbs.approval.integration.spec.IntegrationDeletePolicyAssignmentRequest;
import com.backbase.dbs.approval.integration.spec.IntegrationDeletePolicyAssignmentResponse;
import com.backbase.dbs.approval.integration.spec.IntegrationPolicyAssignmentRequest;
import com.backbase.dbs.approval.integration.spec.IntegrationPolicyItemDto;
import com.backbase.dbs.approval.integration.spec.IntegrationPostApprovalTypeRequest;
import com.backbase.dbs.approval.integration.spec.IntegrationPostApprovalTypeResponse;
import com.backbase.dbs.approval.integration.spec.IntegrationPostBulkApprovalTypeAssignmentRequest;
import com.backbase.dbs.approval.integration.spec.IntegrationPostBulkApprovalTypeAssignmentResponse;
import com.backbase.dbs.approval.integration.spec.IntegrationPostPolicyAssignmentBulkRequest;
import com.backbase.dbs.approval.integration.spec.IntegrationPostPolicyRequest;
import com.backbase.dbs.approval.integration.spec.IntegrationPostPolicyResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ApprovalIntegrationRestClient extends AbstractRestClient {

    private static final String APPROVALS_BASE_URI = globalProperties.getString(PROPERTY_APPROVALS_BASE_URI);
    private static final String SERVICE_VERSION = "v2";

    private static final String APPROVAL_INTEGRATION_SERVICE = "approval-integration-service";
    private static final String APPROVAL_TYPES = "/approval-types";
    private static final String APPROVAL_TYPE_ASSIGNMENTS = "/approval-type-assignments";
    private static final String APPROVAL_TYPE_ASSIGNMENTS_BULK = APPROVAL_TYPE_ASSIGNMENTS + "/bulk";
    private static final String POLICIES = "/policies";
    private static final String POLICY_ASSIGNMENTS = "/policy-assignments";
    private static final String POLICY_ASSIGNMENTS_BULK = POLICY_ASSIGNMENTS + "/bulk";

    public ApprovalIntegrationRestClient() {
        super(APPROVALS_BASE_URI, SERVICE_VERSION);
        setInitialPath(composeInitialPath());
    }

    public Response createApprovalType(IntegrationPostApprovalTypeRequest body) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .body(body)
            .post(getPath(APPROVAL_TYPES));
    }

    public Response deleteApprovalType(String approvalTypeId) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .delete(getPath(APPROVAL_TYPES + "/" + approvalTypeId));
    }

    public Response assignApprovalTypes(IntegrationPostBulkApprovalTypeAssignmentRequest body) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .body(body)
            .post(getPath(APPROVAL_TYPE_ASSIGNMENTS_BULK));
    }

    public Response deleteApprovalTypeAssignment(String jobProfileId) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .delete(getPath(APPROVAL_TYPE_ASSIGNMENTS + "/" + jobProfileId));
    }

    public Response createPolicy(IntegrationPostPolicyRequest body) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .body(body)
            .post(getPath(POLICIES));
    }

    public Response assignPolicies(IntegrationPostPolicyAssignmentBulkRequest body) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .body(body)
            .post(getPath(POLICY_ASSIGNMENTS_BULK));
    }

    public Response deletePolicyAssignment(IntegrationDeletePolicyAssignmentRequest body) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .body(body)
            .delete(getPath(POLICY_ASSIGNMENTS));
    }

    public Response deletePolicy(String policyId) {
        return requestSpec()
            .contentType(ContentType.JSON)
            .delete(getPath(POLICIES + "/" + policyId));
    }

    public String createApprovalType(String name, Integer rank) {
        return createApprovalType(createPostApprovalTypeRequest(name, rank))
            .then()
            .statusCode(SC_CREATED)
            .extract()
            .as(IntegrationPostApprovalTypeResponse.class)
            .getApprovalType()
            .getId();
    }

    public IntegrationPostBulkApprovalTypeAssignmentResponse assignApprovalTypes(
        List<IntegrationApprovalTypeAssignmentDto> approvalTypeAssignmentDtos) {
        return assignApprovalTypes(createPostBulkApprovalTypesAssignmentRequest(approvalTypeAssignmentDtos))
            .then()
            .statusCode(SC_CREATED)
            .extract()
            .as(IntegrationPostBulkApprovalTypeAssignmentResponse.class);
    }

    public String createPolicy(List<IntegrationPolicyItemDto> policyItems) {
        return createPolicy(createPostPolicyRequest(policyItems))
            .then()
            .statusCode(SC_CREATED)
            .extract()
            .as(IntegrationPostPolicyResponse.class)
            .getPolicy()
            .getId();
    }

    public String createZeroApprovalPolicy() {
        return createPolicy(createPostPolicyRequestWithZeroPolicyItems())
            .then()
            .statusCode(SC_CREATED)
            .extract()
            .as(IntegrationPostPolicyResponse.class)
            .getPolicy()
            .getId();
    }

    public void assignPolicies(List<IntegrationPolicyAssignmentRequest> policyAssignmentRequests) {
        assignPolicies(createPostPolicyAssignmentBulkRequest(policyAssignmentRequests))
            .then()
            .statusCode(SC_NO_CONTENT);
    }

    public IntegrationDeletePolicyAssignmentResponse deletePolicyAssignment(String externalServiceAgreementId,
        String externalLegalEntityId,
        String resource,
        String function) {
        return deletePolicyAssignment(
            createDeletePolicyAssignmentRequest(externalServiceAgreementId, externalLegalEntityId, resource, function))
            .then()
            .statusCode(SC_OK)
            .extract()
            .as(IntegrationDeletePolicyAssignmentResponse.class);
    }

    @Override
    protected String composeInitialPath() {
        return APPROVAL_INTEGRATION_SERVICE;
    }

}
