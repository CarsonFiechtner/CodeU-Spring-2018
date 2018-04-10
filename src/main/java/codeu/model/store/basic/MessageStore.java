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
import codeu.model.store.basic.SortByCreationTime;
import codeu.model.store.basic.UserStore;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Collections;
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
  public boolean loadTestData() {
    boolean loaded = false;
    try {
      messages.addAll(DefaultDataStore.getInstance().getAllMessages());
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
      Collections.sort(messageList, new SortByCreationTime());
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
    }
    lastStatUpdate = Instant.now();
    //Since there's no guarantee this will be called every day, we must check when the latest messages were sent.
    if(!untrackedMessages.isEmpty()){
	int day = 0, count = 0;
	while(!untrackedMessages.isEmpty()){
            Instant checkTime = lastStatUpdate.plus(24*(day+1), ChronoUnit.HOURS);
	    while(untrackedMessages.get(0).getCreationTime().isBefore(checkTime)){
		count++;
		untrackedMessages.remove(0);
	    }
	    day++;
	    thirtyDayStats.add(0, count);
	}
	//Keep stats limited to 30 days
	while(thirtyDayStats.size() > 30)
	    thirtyDayStats.remove(thirtyDayStats.size()-1);
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
  public String getNewestMessage() {

    if(newestMessage == null){
      Instant newMessageTime = Instant.EPOCH;

      for (Message message : messages) {
        if (message.getCreationTime().isAfter(newMessageTime)) {
            newestMessage = message;
	    newMessageTime = message.getCreationTime();
        }
      }
    }
    Date newTime = Date.from(newestMessage.getCreationTime());
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    return formatter.format(newTime);
  }

  /** Sets the List of Messages stored by this MessageStore. */
  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}
