
// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.controller;

import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import codeu.model.data.User;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import java.util.List;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet class responsible for loading test data. */
public class DeleteDataServlet extends HttpServlet {

  /** Store class that gives access to Conversations. */
  private ConversationStore conversationStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Set up state for handling the load test data request. */
  @Override
  public void init() throws ServletException {
    super.init();
    setConversationStore(ConversationStore.getInstance());
    setMessageStore(MessageStore.getInstance());
    setUserStore(UserStore.getInstance());
  }

  /**
   * Sets the ConversationStore used by this servlet. This function provides a common setup method
   * for use by the test framework or the servlet's init() function.
   */
  void setConversationStore(ConversationStore conversationStore) {
    this.conversationStore = conversationStore;
  }

  /**
   * Sets the MessageStore used by this servlet. This function provides a common setup method for
   * use by the test framework or the servlet's init() function.
   */
  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * This function fires when a user requests the /testdata URL. It simply forwards the request to
   * testdata.jsp.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    request.getRequestDispatcher("/WEB-INF/view/delete.jsp").forward(request, response);
  }

  /**
   * This function fires when a user submits the testdata form. It loads test data if the user
   * clicked the confirm button.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String [] confirms = new String[4];
	confirms[0] = request.getParameter("confirm");
	confirms[1] = request.getParameter("confirm1");
	confirms[2] = request.getParameter("confirm2");
	confirms[3] = request.getParameter("confirm3");
    boolean confirmed = true;
    for(int i = 0; i < 4; i++){
	if(!confirms[i].contains("confirm")){
    	    confirmed = false;
	}
    }
        String username = (String) request.getSession().getAttribute("user");
    if(!request.getParameter("confirmUsername").equals(username)){
	confirmed = false;
    }
    if(confirmed){
        User user = userStore.getUser(username);
        messageStore.removeUserMessages(user);
        conversationStore.removeUserConversations(user);
        userStore.removeUser(user);
        request.getSession().setAttribute("user", null);
    }
    response.sendRedirect("/");
  }
}

