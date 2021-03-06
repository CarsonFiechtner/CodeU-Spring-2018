package codeu.model.store.basic;

import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class MessageStoreTest {

  private MessageStore messageStore;
  private PersistentStorageAgent mockPersistentStorageAgent;

  private final UUID CONVERSATION_ID_ONE = UUID.randomUUID();
  private final Message MESSAGE_ONE =
      new Message(
          UUID.randomUUID(),
          CONVERSATION_ID_ONE,
          UUID.randomUUID(),
          "message one",
          Instant.ofEpochMilli(1000));
  private final Message MESSAGE_TWO =
      new Message(
          UUID.randomUUID(),
          CONVERSATION_ID_ONE,
          UUID.randomUUID(),
          "message two",
          Instant.ofEpochMilli(2000));
  private final Message MESSAGE_THREE =
      new Message(
          UUID.randomUUID(),
          UUID.randomUUID(),
          UUID.randomUUID(),
          "message three",
          Instant.ofEpochMilli(3000));

  @Before
  public void setup() {
    mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);
    messageStore = MessageStore.getTestInstance(mockPersistentStorageAgent);

    final List<Message> messageList = new ArrayList<>();
    messageList.add(MESSAGE_ONE);
    messageList.add(MESSAGE_TWO);
    messageList.add(MESSAGE_THREE);
    messageStore.setMessages(messageList);
  }

  @Test
  public void testGetMessagesInConversation() {
    List<Message> resultMessages = messageStore.getMessagesInConversation(CONVERSATION_ID_ONE);

    Assert.assertEquals(2, resultMessages.size());
    assertEquals(MESSAGE_ONE, resultMessages.get(0));
    assertEquals(MESSAGE_TWO, resultMessages.get(1));
  }

   @Test
  public void testNumMessages() {
    Assert.assertEquals(messageStore.getNumMessages(), 3);
  }

  @Test
  public void testMessageData() {
    Integer [] test = new Integer[30];
    for(int i = 0; i < 30; i++){
	test[i] = 0;
    }
    Assert.assertEquals(messageStore.activeUserInfo(), test);
  }

  @Test
  public void testAddMessage() {
    UUID inputConversationId = UUID.randomUUID();
    Message inputMessage =
        new Message(
            UUID.randomUUID(),
            inputConversationId,
            UUID.randomUUID(),
            "test message",
            Instant.now());

    messageStore.addMessage(inputMessage);
    Message resultMessage = messageStore.getMessagesInConversation(inputConversationId).get(0);

    assertEquals(messageStore.getNewestMessage(), inputMessage);
    assertEquals(inputMessage, resultMessage);
    Mockito.verify(mockPersistentStorageAgent).writeThrough(inputMessage);
  }

@Test
  public void testRemoveUserMessages() {
    User inputUser = new User(UUID.randomUUID(), "test_username", "password", Instant.now());
    User inputUser2 = new User(UUID.randomUUID(), "test_username2", "password", Instant.now());
    Message inputMessage =
        new Message(
            UUID.randomUUID(),
            UUID.randomUUID(),
	    inputUser.getId(),
            "test message",
            Instant.now());
    Message inputMessage2 =
        new Message(
            UUID.randomUUID(),
            UUID.randomUUID(),
            inputUser2.getId(),
            "test message",
            Instant.now());

    messageStore.addMessage(inputMessage);
    messageStore.addMessage(inputMessage2);

    messageStore.removeUserMessages(inputUser2);
    List<Message> removedMessages = new ArrayList<>();
    removedMessages.add(inputMessage2);
    Assert.assertEquals(messageStore.getNumMessages(), 4);
    Mockito.verify(mockPersistentStorageAgent).deleteThroughMessages(removedMessages);
  }

  private void assertEquals(Message expectedMessage, Message actualMessage) {
    Assert.assertEquals(expectedMessage.getId(), actualMessage.getId());
    Assert.assertEquals(expectedMessage.getConversationId(), actualMessage.getConversationId());
    Assert.assertEquals(expectedMessage.getAuthorId(), actualMessage.getAuthorId());
    Assert.assertEquals(expectedMessage.getContent(), actualMessage.getContent());
    Assert.assertEquals(expectedMessage.getCreationTime(), actualMessage.getCreationTime());
  }
}
