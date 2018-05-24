package codeu.controller;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.Instant;
import codeu.model.data.User;
import codeu.model.data.Message;
import codeu.model.store.basic.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DeleteDataServletTest {

 private DeleteDataServlet deleteDataServlet;
 private HttpServletRequest mockRequest;
 private HttpServletResponse mockResponse;
 private RequestDispatcher mockRequestDispatcher, mockRequestDispatcher2;
 private UserStore mockUserStore;
 private ConversationStore mockConversationStore;
 private MessageStore mockMessageStore;
 private User mockUser;

 @Before
 public void setup() {
   deleteDataServlet = new DeleteDataServlet();
   mockRequest = Mockito.mock(HttpServletRequest.class);
   mockResponse = Mockito.mock(HttpServletResponse.class);

   HttpSession mockSession = Mockito.mock(HttpSession.class);
   Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

   mockUserStore = Mockito.mock(UserStore.class);
   deleteDataServlet.setUserStore(mockUserStore);

   mockUser = Mockito.mock(User.class);
   Mockito.when(mockUser.getId()).thenReturn(UUID.randomUUID());

   mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
   Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/delete.jsp"))
       .thenReturn(mockRequestDispatcher);

    mockMessageStore = Mockito.mock(MessageStore.class);
    deleteDataServlet.setMessageStore(mockMessageStore);

    mockConversationStore = Mockito.mock(ConversationStore.class);
    deleteDataServlet.setConversationStore(mockConversationStore);

 }

 @Test
 public void testDoGet() throws IOException, ServletException {
   deleteDataServlet.doGet(mockRequest, mockResponse);

   Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
 }

 @Test
 public void testDoPost_confirmed() throws IOException, ServletException {
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test username");
    Mockito.when(mockUserStore.getUser("test username")).thenReturn(mockUser);

    Mockito.when(mockRequest.getParameter("confirm")).thenReturn("confirmTesting");
    Mockito.when(mockRequest.getParameter("confirm1")).thenReturn("confirm1");
    Mockito.when(mockRequest.getParameter("confirm2")).thenReturn("confirm2");
    Mockito.when(mockRequest.getParameter("confirm3")).thenReturn("confirm3");
    Mockito.when(mockRequest.getParameter("confirmUsername")).thenReturn("test username");

    deleteDataServlet.doPost(mockRequest, mockResponse);

    User deletedUser = mockUserStore.getUser("test username");
    Mockito.verify(mockMessageStore).removeUserMessages(deletedUser);
    Mockito.verify(mockConversationStore).removeUserConversations(deletedUser);
    Mockito.verify(mockUserStore).removeUser(deletedUser);
    Mockito.verify(mockRequest.getSession()).setAttribute(("user"), null);
    Mockito.verify(mockResponse).sendRedirect("/");

 }

 @Test
 public void testDoPost_badButton() throws IOException, ServletException {
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test username");
    Mockito.when(mockUserStore.getUser("test username")).thenReturn(mockUser);

    Mockito.when(mockRequest.getParameter("confirm")).thenReturn("cancel");
    Mockito.when(mockRequest.getParameter("confirm1")).thenReturn("confirm1");
    Mockito.when(mockRequest.getParameter("confirm2")).thenReturn("confirm2");
    Mockito.when(mockRequest.getParameter("confirm3")).thenReturn("confirm3");
    Mockito.when(mockRequest.getParameter("confirmUsername")).thenReturn("test username");

    deleteDataServlet.doPost(mockRequest, mockResponse);

    User deletedUser = mockUserStore.getUser("test username");
    Mockito.verify(mockMessageStore, Mockito.never()).removeUserMessages(deletedUser);
    Mockito.verify(mockConversationStore, Mockito.never()).removeUserConversations(deletedUser);
    Mockito.verify(mockUserStore, Mockito.never()).removeUser(deletedUser);
    Mockito.verify(mockRequest.getSession(), Mockito.never()).setAttribute(("user"), null);
    Mockito.verify(mockResponse).sendRedirect("/");

 }

 @Test
 public void testDoPost_badUsername() throws IOException, ServletException {
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test username");
    Mockito.when(mockUserStore.getUser("test username")).thenReturn(mockUser);

    Mockito.when(mockRequest.getParameter("confirm")).thenReturn("confirmTesting");
    Mockito.when(mockRequest.getParameter("confirm1")).thenReturn("confirm1");
    Mockito.when(mockRequest.getParameter("confirm2")).thenReturn("confirm2");
    Mockito.when(mockRequest.getParameter("confirm3")).thenReturn("confirm3");
    Mockito.when(mockRequest.getParameter("confirmUsername")).thenReturn("bad username");

    deleteDataServlet.doPost(mockRequest, mockResponse);

    User deletedUser = mockUserStore.getUser("test username");
    Mockito.verify(mockMessageStore, Mockito.never()).removeUserMessages(deletedUser);
    Mockito.verify(mockConversationStore, Mockito.never()).removeUserConversations(deletedUser);
    Mockito.verify(mockUserStore, Mockito.never()).removeUser(deletedUser);
    Mockito.verify(mockRequest.getSession(), Mockito.never()).setAttribute(("user"), null);
    Mockito.verify(mockResponse).sendRedirect("/");

 }
}
