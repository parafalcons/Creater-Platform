package com.platform.chat.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.platform.chat.domain.ChatRoom;
import com.platform.chat.domain.Message;
import com.platform.chat.service.ChatService;
import com.platform.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/room")
    public ResponseEntity<ChatRoom> createOrGetChatRoom(@RequestParam Long user2Id, 
                                                       HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long user1Id = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        ChatRoom chatRoom = chatService.createOrGetChatRoom(user1Id, user2Id);
        return ResponseEntity.ok(chatRoom);
    }

    @PostMapping("/{chatRoomId}/message")
    public ResponseEntity<Message> sendMessage(@PathVariable Long chatRoomId,
                                              @RequestParam Long recipientId,
                                              @RequestParam String content,
                                              HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long senderId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        Message message = chatService.sendMessage(chatRoomId, senderId, recipientId, content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<Message>> getChatMessages(@PathVariable Long chatRoomId) {
        List<Message> messages = chatService.getChatMessages(chatRoomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getUserChatRooms(HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        List<ChatRoom> chatRooms = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @PostMapping("/{chatRoomId}/read")
    public ResponseEntity<String> markMessagesAsRead(@PathVariable Long chatRoomId,
                                                    HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        Long userId = Long.parseLong(jwtTokenProvider.getUserId(token));
        
        chatService.markMessagesAsRead(chatRoomId, userId);
        return ResponseEntity.ok("Messages marked as read");
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Invalid token");
    }
} 