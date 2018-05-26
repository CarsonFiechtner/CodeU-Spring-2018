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
   	// update user profile
	request.setAttribute("aboutMe", aboutMe);
    
	// checks if the request actually contains upload file
	if (!ServletFileUpload.isMultipartContent(request)) {
		// if not, we stop here
		request.setAttribute("Error", "Error: Form must has enctype=multipart/form-data.");
		System.out.println("DOPOST Error: "+(String)request.getAttribute("Error"));
		
		response.sendRedirect("/profile");
		return;
	}

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
	try {
		// parses the request's content to extract file data
		List<FileItem> formItems = upload.parseRequest(request);

		
		if (formItems != null && formItems.size() > 0) {
			// iterates over form's fields
			for (FileItem item : formItems) {
				 
			    if(item.isFormField() ){
			        if(item.getFieldName().equals("aboutMe")){
			        	aboutMe=new String(item.getString().getBytes("ISO-8859-1"), "UTF-8") ;
			        }
			        if(item.getFieldName().equals("profilePic")){   
			        	profilePic=item.getString();
			        }
			        user.setAboutMe(aboutMe);
			        userStore.updateUser(user);
			       // update user profile
			    	request.setAttribute("profilePic", profilePic);
			    	request.setAttribute("aboutMe", aboutMe);
			        System.out.println("DOPOST aboutMe: "+aboutMe+".");
			        System.out.println("DOPOST profilePic: "+profilePic);
			    }
				// processes only fields that are not form fields
				if (!item.isFormField()) {
					String fileName = new File(item.getName()).getName();
					System.out.println("DOPOST INFO: fileName=  " + fileName);
					
					if (fileName==null || fileName=="") {
						 response.sendRedirect("/profile");
						 return;
					}
					String oldFilePath = uploadPath + File.separator + fileName;
					//File oldFile = new File(oldFilePath);
					String newFilePath = uploadPath + File.separator + currentUser+FILE_DOT_NAME;
					File newFile = new File(newFilePath);
					
					System.out.println("DOPOST INFO: oldFilePath=  " + oldFilePath);
					System.out.println("DOPOST INFO: newFilePath=  " + newFilePath);
					if( newFile.exists()) {
						try {
						    newFile.delete();
						    System.out.println("DOPOST INFO: new file is  deleted .... ");
							
						} catch (Exception ex) {
							request.setAttribute("Error", "There was an error: " + ex.getMessage());
							System.out.println("DOPOST Error: "+(String)request.getAttribute("Error"));
							System.out.println("DOPOST INFO: file  exist ,delete failed ,Upload  Failed.... ");
							response.sendRedirect("/profile");
							return;
						}

					}
					
					// saves the old file on disk
					item.write(newFile); 
					request.setAttribute("message", "Upload has been done successfully!");
					System.out.println("DOPOST INFO:  file is uploaded .... ");
					
				}
			}
		}
	} catch (Exception ex) {
		request.setAttribute("Error", "There was an error: " + ex.getMessage());
		System.out.println("DOPOST Error: "+(String)request.getAttribute("Error"));
		response.sendRedirect("/profile");
		return;
	}

	System.out.println("DOPOST message: "+(String)request.getAttribute("message"));
	response.sendRedirect("/profile");
  }
}
