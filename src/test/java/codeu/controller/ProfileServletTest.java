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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProfileServletTest {

 private ProfileServlet profileServlet;
 private HttpServletRequest mockRequest;
 private HttpServletResponse mockResponse;
 private RequestDispatcher mockRequestDispatcher, mockRequestDispatcher2;
 private UserStore mockUserStore;
 private MessageStore mockMessageStore;
 private User mockUser;

 @Before
 public void setup() {
   profileServlet = new ProfileServlet();
   mockRequest = Mockito.mock(HttpServletRequest.class);
   mockResponse = Mockito.mock(HttpServletResponse.class);

   HttpSession mockSession = Mockito.mock(HttpSession.class);
   Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

   mockUserStore = Mockito.mock(UserStore.class);
   profileServlet.setUserStore(mockUserStore);

   mockUser = Mockito.mock(User.class);
   Mockito.when(mockUser.getAboutMe()).thenReturn("oldAboutMe");
   Mockito.when(mockUser.getId()).thenReturn(UUID.randomUUID());

   mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
   Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profile.jsp"))
       .thenReturn(mockRequestDispatcher);
   mockRequestDispatcher2 = Mockito.mock(RequestDispatcher.class);
   Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/login.jsp"))
       .thenReturn(mockRequestDispatcher2);

    mockMessageStore = Mockito.mock(MessageStore.class);
    profileServlet.setMessageStore(mockMessageStore);

    List<Message> fakeMessageList = new ArrayList<>();
    fakeMessageList.add(
        new Message(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "test message",
            Instant.now()));
    Mockito.when(mockMessageStore.getMessages()).thenReturn(fakeMessageList);
 }

 @Test
 public void testDoGet_Correct() throws IOException, ServletException {
   Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test username");
   Mockito.when(mockUserStore.getUser("test username")).thenReturn(mockUser);

   profileServlet.doGet(mockRequest, mockResponse);

   Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
 }

 @Test
 public void testDoGet_nullUser() throws IOException, ServletException {
   Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn(null);

   profileServlet.doGet(mockRequest, mockResponse);

   Mockito.verify(mockResponse).sendRedirect("/register");
 }

@Test
 public void testDoGet_unregisteredUser() throws IOException, ServletException {
   Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test username");
   Mockito.when(mockUserStore.getUser("test username")).thenReturn(null);

   profileServlet.doGet(mockRequest, mockResponse);

   Mockito.verify(mockResponse).sendError(HttpServletResponse.SC_NOT_FOUND);
 }

 @Test
 public void testDoPost_validCurrentUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("test username");
    Mockito.when(mockUserStore.getUser("test username")).thenReturn(mockUser);

    Mockito.when(mockRequest.getParameter("aboutMe")).thenReturn("test aboutMe");

    profileServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockUser).setAboutMe("test aboutMe");

    Mockito.verify(mockResponse).sendRedirect("/profile");

 }

 @Test
 public void testDoPost_invalidCurrentUser() throws IOException, ServletException {
    Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn(null);

    Mockito.when(mockRequest.getParameter("aboutMe")).thenReturn("test aboutMe");

    profileServlet.doPost(mockRequest, mockResponse);

    Mockito.verify(mockRequest).setAttribute("Error", "Please login correctly.");
    Mockito.verify(mockRequestDispatcher2).forward(mockRequest, mockResponse);

 }
}
