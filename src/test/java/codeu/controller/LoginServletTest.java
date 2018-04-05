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

import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mindrot.jbcrypt.*;

public class LoginServletTest {

  private LoginServlet loginServlet;
  private HttpServletRequest mockRequest;
  private HttpServletResponse mockResponse;
  private RequestDispatcher mockRequestDispatcher;

  @Before
  public void setup() {
    loginServlet = new LoginServlet();
    mockRequest = Mockito.mock(HttpServletRequest.class);
    mockResponse = Mockito.mock(HttpServletResponse.class);
    mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/login.jsp"))
        .thenReturn(mockRequestDispatcher);
  }

  @Test
  public void testDoGet() throws IOException, ServletException {
    loginServlet.doGet(mockRequest, mockResponse);

    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_NoUserFound() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");

    UserStore mockUserStore = Mockito.mock(UserStore.class);
    Mockito.when(mockUserStore.isUserRegistered("test_username")).thenReturn(false);
    loginServlet.setUserStore(mockUserStore);

    HttpSession mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    loginServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockUserStore, Mockito.never()).addUser(Mockito.any(User.class));

    Mockito.verify(mockRequest)
        .setAttribute("error", "The username test_username was not found.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_WrongPassword() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");
    Mockito.when(mockRequest.getParameter("password")).thenReturn("wrong _password");

    UserStore mockUserStore = Mockito.mock(UserStore.class);
    Mockito.when(mockUserStore.isUserRegistered("test_username")).thenReturn(true);
    loginServlet.setUserStore(mockUserStore);

    HttpSession mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    User mockUser = Mockito.mock(User.class);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(mockUser);
    Mockito.when(mockUser.getPassword()).thenReturn(BCrypt.hashpw("valid password", BCrypt.gensalt()));


    loginServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockUserStore, Mockito.never()).addUser(Mockito.any(User.class));

    Mockito.verify(mockRequest)
        .setAttribute("error", "Invalid password.");
    Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
  }

  @Test
  public void testDoPost_RightPassword() throws IOException, ServletException {
    Mockito.when(mockRequest.getParameter("username")).thenReturn("test_username");
    Mockito.when(mockRequest.getParameter("password")).thenReturn("correct_password");

    UserStore mockUserStore = Mockito.mock(UserStore.class);
    Mockito.when(mockUserStore.isUserRegistered("test_username")).thenReturn(true);
    loginServlet.setUserStore(mockUserStore);

    HttpSession mockSession = Mockito.mock(HttpSession.class);
    Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

    User mockUser = Mockito.mock(User.class);
    Mockito.when(mockUserStore.getUser("test_username")).thenReturn(mockUser);
    Mockito.when(mockUser.getPassword()).thenReturn(BCrypt.hashpw("correct_password", BCrypt.gensalt()));

    loginServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockSession).setAttribute("user", "test_username");
    Mockito.verify(mockResponse).sendRedirect("/conversations");
  }
}
