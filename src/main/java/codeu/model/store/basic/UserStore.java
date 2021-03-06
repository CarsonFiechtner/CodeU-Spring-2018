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

import codeu.model.data.User;
import codeu.model.data.Message;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.Instant;
import codeu.model.store.basic.MessageStore;

/**
 * Store class that uses in-memory data structures to hold values and automatically loads from and
 * saves to PersistentStorageAgent. It's a singleton so all servlet classes can access the same
 * instance.
 */
public class UserStore {

  /** Singleton instance of UserStore. */
  private static UserStore instance;

  /**
   * Returns the singleton instance of UserStore that should be shared between all servlet classes.
   * Do not call this function from a test; use getTestInstance() instead.
   */
  public static UserStore getInstance() {
    if (instance == null) {
      instance = new UserStore(PersistentStorageAgent.getInstance());
    }
    return instance;
  }

  /**
   * Instance getter function used for testing. Supply a mock for PersistentStorageAgent.
   *
   * @param persistentStorageAgent a mock used for testing
   */
  public static UserStore getTestInstance(PersistentStorageAgent persistentStorageAgent) {
    return new UserStore(persistentStorageAgent);
  }

  /**
   * The PersistentStorageAgent responsible for loading Users from and saving Users to Datastore.
   */
  private PersistentStorageAgent persistentStorageAgent;

  /** The in-memory list of Users. */
  private List<User> users;

  /** These track the oldest and newest users, respectively */
  private User oldestUser;
  private User newestUser;
  /** This class is a singleton, so its constructor is private. Call getInstance() instead. */
  private UserStore(PersistentStorageAgent persistentStorageAgent) {
    this.persistentStorageAgent = persistentStorageAgent;
    users = new ArrayList<>();
  }

  /** Load a set of randomly-generated Message objects. */
  public void loadTestData(int numUsers) {
    users.addAll(DefaultDataStore.getInstance().getNewUsers(numUsers));
  }

  /**
   * Access the User object with the given name.
   *
   * @return null if username does not match any existing User.
   */
  public User getUser(String username) {
    // This approach will be pretty slow if we have many users.
    for (User user : users) {
      if (user.getName().equals(username)) {
        return user;
      }
    }
    return null;
  }

  /**
   * Access the newest User object.
   *
   * @return The newest User's name.
   */
  public User getNewestUser() {
    if(newestUser == null){
      Instant testTime = Instant.EPOCH;
      for (User user : users) {
        if (user.getCreationTime().isAfter(testTime)) {
	  testTime = user.getCreationTime();
	  newestUser = user;
        }
      }
    }
    return newestUser;
  }

  /**
   * Access the oldest User object.
   *
   * @return The oldest User's name.
   */
  public User getOldestUser() {
    //This likely won't work if the oldest user is removed, but because we don't have that implemented yet, this works for now
    if(oldestUser == null){
      Instant testTime = Instant.now();
      for (User user : users) {
        if (user.getCreationTime().isBefore(testTime)) {
	  testTime = user.getCreationTime();
          oldestUser = user;
        }
      }
    }
    return oldestUser;
  }

  /**
   * Get the number of Users currently stored
   *
   * @return The current number of users stored
   */
  public int getNumUsers() {
        return users.size();
  }


  /**
   * Access the User object with the given UUID.
   *
   * @return null if the UUID does not match any existing User.
   */
  public User getUser(UUID id) {
    for (User user : users) {
      if (user.getId().equals(id)) {
        return user;
      }
    }
    return null;
  }

  /** Add a new user to the current set of users known to the application. */
  public void addUser(User user) {
    users.add(user);
    newestUser = user;
    persistentStorageAgent.writeThrough(user);
  }

  /** Update an existing user. */
  public void updateUser(User user) {
    persistentStorageAgent.writeThrough(user);
  }

  /** Remove an existing user. */
  public void removeUser(User user) {
    for(int i = 0; i < users.size(); i++){
	if(users.get(i) == user){
	    users.remove(i);
	    break;
	}
    }
    persistentStorageAgent.deleteThrough(user);
  }


  /** Return true if the given username is known to the application. */
  public boolean isUserRegistered(String username) {
    for (User user : users) {
      if (user.getName().equals(username)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Sets the List of Users stored by this UserStore. This should only be called once, when the data
   * is loaded from Datastore.
   */
  public void setUsers(List<User> users) {
    this.users = users;
  }
}
