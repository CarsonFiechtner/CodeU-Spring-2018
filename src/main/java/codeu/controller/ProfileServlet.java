package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.store.basic.MessageStore;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Servlet class responsible for the profile page. */
public class ProfileServlet extends HttpServlet {

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

  /** Set up state for handling profile requests. */
  @Override
  public void init() throws ServletException {
    super.init();
    setUserStore(UserStore.getInstance());
    setMessageStore(MessageStore.getInstance());
  }

  /**
   * Sets the UserStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setUserStore(UserStore userStore) {
    this.userStore = userStore;
  }

  /**
   * Sets the MessageStore used by this servlet. This function provides a common setup method for use
   * by the test framework or the servlet's init() function.
   */
  void setMessageStore(MessageStore messageStore) {
    this.messageStore = messageStore;
  }

  /**
   * This function fires when a user navigates to the profile page. It gets the conversation title from
   * the URL, finds the corresponding Conversation, and fetches the messages in that Conversation,
   * written by that specific user.
   * It then forwards to profile.jsp for rendering.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String username = (String) request.getSession().getAttribute("user");

    if(username == null) {
      response.sendRedirect("/register");
      return;
    }
	  
    User user = userStore.getUser(username);
    if (user == null) {
      // if user is not found, send a 404 code
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    request.setAttribute(user.getName(), user);

    String aboutMe = user.getAboutMe();
    request.setAttribute("aboutMe", aboutMe);

    List<Message> authorMessages = getAuthorMessages(user);
    request.setAttribute("authorMessages", authorMessages);

    request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
  }

  /** This function gets all messages that user sent in the conversations and add those messages to a list*/
  private List<Message> getAuthorMessages(User user){
    List<Message> authorMessages = new ArrayList<>();
    List<Message> messages = messageStore.getMessages();
    for(Message message: messages){
      if (message.getAuthorId().equals(user.getId()))
        authorMessages.add(message);
    }  
    return authorMessages;
  }


  /**
   * This function fires when a user submits the form on the profile page. It gets the logged-in
   * username from the session, the conversation title from the URL, and the chat message from the
   * submitted form data. 
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String currentUser = (String) request.getSession().getAttribute("user");
    if( currentUser == null || userStore.getUser(currentUser) == null){
      request.setAttribute("Error", "Please login correctly.");
      request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
      return;
    }

    User user = userStore.getUser(currentUser);
    user.setAboutMe((String) request.getParameter("aboutMe"));

    response.sendRedirect("/profile");
  }
}
