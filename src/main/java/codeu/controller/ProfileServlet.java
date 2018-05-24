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

	/* ADD END *
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

    String aboutMe = user.getAboutMe();
    request.setAttribute("aboutMe", aboutMe);
    String profilePic = request.getParameter("profilePic");// ADD
    String message = request.getParameter("message");// ADD
    
    System.out.println("DOGET message : "+message);
    request.setAttribute("message", message);
	//request.setAttribute("profilePic", user.getProfilePic());// ADD
	
	//TODO show my photo
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

    String currentUser = (String) request.getParameter("value");
    if(currentUser == null || currentUser == "")
        currentUser = (String) request.getSession().getAttribute("user");

    if( currentUser == null || userStore.getUser(currentUser) == null){
      request.setAttribute("Error", "Please login correctly.");
      request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
      return;
    }

    User user = userStore.getUser(currentUser);
    String aboutMe = (String) request.getParameter("aboutMe");
    user.setAboutMe(aboutMe);
    userStore.updateUser(user);
    
    /* ADD START */
    String profilePic = request.getParameter("profilePic");// ADD
	// update user profile
	//user.updateUser(user.getPassword(), aboutMe, profilePic);// ADD
	request.setAttribute("profilePic", profilePic);// ADD
	//request.setAttribute("aboutMe", aboutMe);// ADD
	// checks if the request actually contains upload file
	if (!ServletFileUpload.isMultipartContent(request)) {
		// if not, we stop here
		request.setAttribute("Error", "Error: Form must has enctype=multipart/form-data.");
		System.out.println("DOPOST Error: "+(String)request.getAttribute("Error"));
		
		response.sendRedirect("/profile");
		return;
	}

	System.out.println("upload start!!!!");
	// configures upload settings
	DiskFileItemFactory factory = new DiskFileItemFactory();
	// sets memory threshold - beyond which files are stored in disk
	factory.setSizeThreshold(MEMORY_THRESHOLD);
	// sets temporary location to store files
	factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

	ServletFileUpload upload = new ServletFileUpload(factory);

	// sets maximum size of upload file
	upload.setFileSizeMax(MAX_FILE_SIZE);

	// sets maximum size of request (include file + form data)
	upload.setSizeMax(MAX_REQUEST_SIZE);

	// constructs the directory path to store upload file
	// this path is relative to application's directory
	String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;

	// creates the directory if it does not exist
	File uploadDir = new File(uploadPath);
	if (!uploadDir.exists()) {
		uploadDir.mkdir();
	}
	//System.out.println("uploadPath:  " + uploadPath);
	try {
		// parses the request's content to extract file data
		//@SuppressWarnings("unchecked")
		List<FileItem> formItems = upload.parseRequest(request);

		if (formItems != null && formItems.size() > 0) {
			// iterates over form's fields
			for (FileItem item : formItems) {
				// processes only fields that are not form fields
				if (!item.isFormField()) {
					String fileName = new File(item.getName()).getName();
					//String filePath = uploadPath + File.separator + fileName;
					String filePath = uploadPath + File.separator + currentUser+".png";
					File storeFile = new File(filePath);
					System.out.println("filePath:  " + filePath);
					// saves the file on disk
					item.write(storeFile);
					request.setAttribute("message", "Upload has been done successfully!");
				}
			}
		}
	} catch (Exception ex) {
		request.setAttribute("Error", "There was an error: " + ex.getMessage());
		System.out.println("DOPOST Error: "+(String)request.getAttribute("Error"));
		response.sendRedirect("/profile");
		return;
	}
	/* ADD END */
	System.out.println("DOPOST message: "+(String)request.getAttribute("message"));
    response.sendRedirect("/profile/"+currentUser);
  }
}
