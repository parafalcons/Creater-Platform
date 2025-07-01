package com.platform.chat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.chat.domain.ChatRoom;
import com.platform.chat.domain.Message;
import com.platform.chat.repository.ChatRoomRepository;
import com.platform.chat.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public ChatRoom createOrGetChatRoom(Long user1Id, Long user2Id) {
        // Check if chat room already exists
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByUser1IdAndUser2Id(user1Id, user2Id);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }
        
        existingRoom = chatRoomRepository.findByUser2IdAndUser1Id(user1Id, user2Id);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }
        
        // Create new chat room
        ChatRoom chatRoom = ChatRoom.builder()
                .user1Id(user1Id)
                .user2Id(user2Id)
                .build();
        
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    @Transactional
    public Message sendMessage(Long chatRoomId, Long senderId, Long recipientId, String content) {
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .recipientId(recipientId)
                .content(content)
                .isRead(false)
                .build();
        
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getChatMessages(Long chatRoomId) {
        return messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
    }

    @Override
    public List<ChatRoom> getUserChatRooms(Long userId) {
        // This is a simplified implementation
        // In a real app, you might want to join with user data and last message
        return chatRoomRepository.findAll().stream()
                .filter(room -> room.getUser1Id().equals(userId) || room.getUser2Id().equals(userId))
                .toList();
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long chatRoomId, Long userId) {
        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        messages.stream()
                .filter(message -> message.getRecipientId().equals(userId) && !message.isRead())
                .forEach(message -> {
                    message.setRead(true);
                    messageRepository.save(message);
                });
    }
} 