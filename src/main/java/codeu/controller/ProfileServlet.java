package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.store.basic.MessageStore;
import java.io.File;
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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/** Servlet class responsible for the profile page. */
public class ProfileServlet extends HttpServlet {

  /** Store class that gives access to Users. */
  private UserStore userStore;

  /** Store class that gives access to Messages. */
  private MessageStore messageStore;

   //location to store file uploaded
	private static final String UPLOAD_DIRECTORY = "upload";

	// upload settings
	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
	private static final String FILE_DOT_NAME = ".png";

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

    String username = (String) request.getParameter("value");
    if(username == null || username == "")
        username = (String) request.getSession().getAttribute("user");

    if(username == null) {
      response.sendRedirect("/register");
      return;
    }
	  
    User user = userStore.getUser(username);
    if (user == null) {
      // if user is not found, send a 404 code
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    request.setAttribute(user.getName(), user);
    System.out.println("DOGET user.getName() : "+user.getName());
    System.out.println("DOGET user : "+user);
    
    String aboutMe = user.getAboutMe();
    System.out.println("DOGET aboutMe : "+aboutMe+".");
    request.setAttribute("aboutMe", aboutMe);

    List<Message> authorMessages = getAuthorMessages(user);
    request.setAttribute("authorMessages", authorMessages);
  
    request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
  }

  /** This function gets all messages that user sent in the conversations and add those messages to a list*/
  private List<Message> getAuthorMessages(User user){
    List<Message> authorMessages = new ArrayList<>();
    List<Message> messages = messageStore.getMessages();
    for(int i = messages.size()-1; i >= 0; i--){
      if (messages.get(i).getAuthorId().equals(user.getId()))
        authorMessages.add(messages.get(i));
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
    String aboutMe ="";
    String profilePic="";
    String currentUser = (String) request.getParameter("value");
    String requestUrl = request.getRequestURI();
    System.out.println("requestUrl====="+requestUrl);
    if (requestUrl!=null){
	    aboutMe = requestUrl.substring("/profile/".length()).trim();
	    aboutMe = aboutMe.replace("%20", " ");
    }
    System.out.println("DOPOST aboutMe: "+aboutMe+".");
    if(currentUser == null || currentUser == "")
        currentUser = (String) request.getSession().getAttribute("user");

    if( currentUser == null || userStore.getUser(currentUser) == null){
      request.setAttribute("Error", "Please login correctly.");
      request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
      return;
    }

    User user = userStore.getUser(currentUser);
    user.setAboutMe(aboutMe);
    userStore.updateUser(user);
    response.sendRedirect("/profile");
  }
}
