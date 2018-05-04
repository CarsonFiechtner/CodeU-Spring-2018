package codeu.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.until.UUID;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.store.basic.MessageStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;

public class ProfileServletTest {

   private ProfileServlet profileServlet;
   private HttpServletRequest mockRequest;
   private HttpSession mockSession;
   private HttpServletResponse mockResponse;
   private RequestDispatcher mockRequestDispatcher;
   private MessageStore mockMessageStore;
   private UserStore mockUserStore;

   @Before
   public void setup() {
      profileServlet = new ProfileServlet();
      mockRequest = Mockito.mock(HttpServletRequest.class);
      mockSession = Mockito.mock(HttpSession.class);
      Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
      mockResponse = Mockito.mock(HttpServletResponse.class);
      mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
      Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profile.jsp"))
          .thenReturn(mockRequestDispatcher);
      mockMessageStore = Mockito.mock(MessageStore.class);
      profileServlet.setMessageStore(mockMessageStore);
      mockUserStore = Mockito.mock(UserStore.class);
      ProfileServlet.setUserStore(mockUserStore);
   }

   @Test
   public void testDoGet() throws IOException, ServletException {
      List<Message> fakeMessages = new ArrayList<>();
      fakeMessages.add(new Message(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID, "test message", Instant.now()));
      Mockito.when(ProfileServlet.getAuthorMessages()).thenReturn(fakeMessages);

      Mockito.when(mockRequest.getName()).thenReturn("test username");
      User mockUser = Mockito.mock(User.class);
      Mockito.when(mockUserStore.getUser("test username")).thenReturn(mockUser);

      Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

      Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test username");
      profileServlet.doGet(mockRequest, mockResponse);

      Mockito.verify(mockRequest).setAttribute("user", "messages", mockUser, fakeMessages);
      Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
   }

   @Test
   public void testDoPost_UserNotLogin() throws IOException ServletException{
      Mockito.when(mockSession.getAttribute("user")).thenReturn(null);
      profileServlet.doPost(mockRequest, mockResponse);
      Mockito.verify(mockResponse).sendRedirect("/profile");
   }


} 