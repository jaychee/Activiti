/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.rest.api.process;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.rest.api.ActivitiUtil;
import org.activiti.rest.api.SecuredResource;
import org.activiti.rest.application.ActivitiRestServicesApplication;
import org.restlet.data.Status;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;


/**
 * @author Frederik Heremans
 */
public class ProcessInstanceResource extends SecuredResource {

  @Get
  public ProcessInstanceResponse getProcessInstance() {
    if(!authenticate()) {
      return null;
    }
    return getApplication(ActivitiRestServicesApplication.class).getRestResponseFactory()
            .createProcessInstanceResponse(this, getProcessInstanceFromRequest());
  }
  
  @Delete
  public void deleteProcessInstance() {
    if(!authenticate()) {
      return;
    }
    ProcessInstance processInstance = getProcessInstanceFromRequest();
    String deleteReason = getQueryParameter("deleteReason", getQuery());
    
    ActivitiUtil.getRuntimeService().deleteProcessInstance(processInstance.getId(), deleteReason);
    setStatus(Status.SUCCESS_NO_CONTENT);
  }
  
  protected ProcessInstance getProcessInstanceFromRequest() {
    String processInstanceId = getAttribute("processInstanceId");
    if (processInstanceId == null) {
      throw new ActivitiIllegalArgumentException("The processInstanceId cannot be null");
    }
    
   ProcessInstance processInstance = ActivitiUtil.getRuntimeService().createProcessInstanceQuery()
           .processInstanceId(processInstanceId).singleResult();
    if (processInstance == null) {
      throw new ActivitiObjectNotFoundException("Could not find a process instance with id '" + processInstanceId + "'.", ProcessInstance.class);
    }
    return processInstance;
  }
}
