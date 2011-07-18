/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.

Oracle and Java are registered trademarks of Oracle and/or its affiliates.
Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package jsf2.demo.scrum.web.controller;

import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.domain.task.Task;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import jsf2.demo.scrum.domain.task.TaskRepository;
import jsf2.demo.scrum.infra.manager.BaseCrudManager;

/**
 * @author Dr. Spock (spock at dev.java.net)
 */
@Named
@ConversationScoped
public class TaskManager extends BaseCrudManager<Task> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private TaskRepository taskRepository;
    
    @Inject
    private StoryManager storyManager;

    public Task getCurrentTask() {
        return getCurrentEntity();
    }

    public Story getStory() {
        return storyManager.getCurrentStory();
    }

    @Override
    protected Task doCreate() {
        Task task = new Task();
        task.setStory(storyManager.getCurrentStory());
        
        return task;
    }

    @Override
    protected void doSave(Task task) {
        Task merged = taskRepository.save(task);
        storyManager.getCurrentStory().addTask(merged);
    }

    @Override
    protected void doRemove(Task task) {
        taskRepository.remove(task);
        storyManager.getCurrentStory().removeTask(task);
    }

    public void checkUniqueTaskName(FacesContext context, UIComponent component, Object newValue) {
        final String newName = (String) newValue;

        long count = taskRepository.countOtherTasksWithName(storyManager.getCurrentStory(), getCurrentEntity(), newName);
        if (count > 0) {
            throw new ValidatorException(getFacesMessageForKey("task.form.label.name.unique"));
        }
    }

    public String showStories() {
        endConversation();
        
        return "/story/show";
    }
 
}