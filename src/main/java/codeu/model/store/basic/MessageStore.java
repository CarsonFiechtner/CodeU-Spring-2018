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

package codeu.model.store.basic;

import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.UserStore;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;
import java.time.Instant;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class MessageStore {

  /** Singleton instance of MessageStore. */
  private static MessageStore instance;

  /**
   * Returns the singleton instance of MessageStore that should be shared between all servlet
   * classes. Do not call this function from a test; use getTestInstance() instead.
   */
  public static MessageStore getInstance() {
    if (instance == null) {
      instance = new MessageStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static MessageStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new MessageStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Messages from and saving Messages to
   * Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Messages. */
  private List<Message> messages;
  private List<Message> untrackedMessages;
  private Message newestMessage;
  private List<Integer> thirtyDayStats;
  private Instant lastStatUpdate;
  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private MessageStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    messages = new ArrayList<>();
    untrackedMessages = new ArrayList<>();
  }

  /**
   * Get the number of Messages currently stored
   *
   * @return The current number of messages stored
   */
  public int getNumMessages() {
        return messages.size();
  }

  /**
   * Load a set of randomly-generated Message objects.
   *
   * @return false if an error occurs.
   */
  public boolean loadTestData(int numMessages) {
    boolean loaded = false;
    try {
      messages.addAll(DefaultDataStore.getInstance().getNewMessages(numMessages));
      loaded = true;
    } catch (Exception e) {
      loaded = false;
      System.out.println("ERROR: Unable to establish initial store (messages).");
    }
    return loaded;
  }

  /** Add a new message to the current set of messages known to the application. */
  public void addMessage(Message message) {
    messages.add(message);
    untrackedMessages.add(message);
    newestMessage = message;
    persistentStorageAgent.writeThrough(message);
  }

  /** Finds the number of messages sent in the last 30 days */
  public Integer [] activeUserInfo() {
    if(thirtyDayStats == null){
      List<Message> messageList = messages;

      messageList.sort(new Comparator<Message>() {
    	@Override
    	public int compare(Message m1, Message m2) {
	      boolean after = m1.getCreationTime().isAfter(m2.getCreationTime());
              if(after)
                  return -1;
              return 1;
        }
      });

      thirtyDayStats = new ArrayList<Integer>();
      int listPos = 0;
      for(int i = 0; i < 30; i++){
	Integer count = 0;
        Instant checkTime = Instant.now().minus(24*(i+1), ChronoUnit.HOURS);
        while(listPos < messageList.size() && messageList.get(listPos).getCreationTime().isAfter(checkTime)){
	  count++;
	  listPos++;
        }
	thirtyDayStats.add(count);
      }
      lastStatUpdate = Instant.now();
    }
    //Since there's no guarantee this will be called every day, we must check when the latest messages were sent.
    if(!untrackedMessages.isEmpty() || Instant.now().minus(24, ChronoUnit.HOURS).isAfter(lastStatUpdate)){
	int count = 0;
        Instant checkTime = lastStatUpdate.plus(24, ChronoUnit.HOURS);
	while(!untrackedMessages.isEmpty() && checkTime.isBefore(Instant.now())){
	    while(untrackedMessages.get(0).getCreationTime().isBefore(checkTime)){
		count++;
		untrackedMessages.remove(0);
	    }
	    thirtyDayStats.add(0, count);
	    checkTime = checkTime.plus(24, ChronoUnit.HOURS);
	}
	//Keep stats limited to 30 days
	while(thirtyDayStats.size() > 30){
	    thirtyDayStats.remove(thirtyDayStats.size()-1);
	}
        lastStatUpdate = Instant.now();
    }
    return thirtyDayStats.toArray(new Integer[thirtyDayStats.size()]);
  }

  /** Access the current set of Messages within the given Conversation. */
  public List<Message> getMessagesInConversation(UUID conversationId) {

    List<Message> messagesInConversation = new ArrayList<>();

    for (Message message : messages) {
      if (message.getConversationId().equals(conversationId)) {
        messagesInConversation.add(message);
      }
    }

    return messagesInConversation;
  }

  /** Return the date and time that the newest message was sent */
  public Message getNewestMessage() {

    if(newestMessage == null){
      Instant newMessageTime = Instant.EPOCH;

      for (Message message : messages) {
        if (message.getCreationTime().isAfter(newMessageTime)) {
            newestMessage = message;
	    newMessageTime = message.getCreationTime();
        }
      }
    }
    return newestMessage;
  }

  /** Sets the List of Messages stored by this MessageStore. */
  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  /** Removes messages sent by a given User */
  public void removeUserMessages(User user){
    List<Message> removedMessages = new ArrayList<>();
    for(int i = messages.size()-1; i >= 0; i--){
        if(messages.get(i).getAuthorId().toString().equals(user.getId().toString())){
	    removedMessages.add(messages.get(i));
            messages.remove(i);
        }
    }
    persistentStorageAgent.deleteThroughMessages(removedMessages);
  }

  public List<Message> getMessages(){
    return messages;
  }
}
