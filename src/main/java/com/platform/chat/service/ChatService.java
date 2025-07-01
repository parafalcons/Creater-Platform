package com.platform.chat.service;

import java.util.List;

import com.platform.chat.domain.ChatRoom;
import com.platform.chat.domain.Message;

public interface ChatService {
    
    ChatRoom createOrGetChatRoom(Long user1Id, Long user2Id);
    
    Message sendMessage(Long chatRoomId, Long senderId, Long recipientId, String content);
    
    List<Message> getChatMessages(Long chatRoomId);
    
    List<ChatRoom> getUserChatRooms(Long userId);
    
    void markMessagesAsRead(Long chatRoomId, Long userId);
} 