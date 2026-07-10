package com.example.organdonationmanagement.controller;

import com.example.organdonationmanagement.dto.UserChatDTO;
import com.example.organdonationmanagement.entity.ChatMessage;
import com.example.organdonationmanagement.entity.User;
import com.example.organdonationmanagement.repository.ChatRepository;
import com.example.organdonationmanagement.service.ChatService;
import com.example.organdonationmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;
//    @MessageMapping("/chat.send")
//    public void processMessage(ChatMessage message, Principal principal) {
//        String username = principal.getName();
//        message.setSender(username);
//
//        // Tìm fullName từ database và gán vào message trước khi lưu
//        userService.findByUsername(username).ifPresent(user -> {
//            message.setSenderFullName(user.getFullName());
//        });
//
//        ChatMessage savedMsg = chatRepository.save(message);
//        messagingTemplate.convertAndSend("/topic/messages/" + message.getConversationId(), savedMsg);
//    }

    @MessageMapping("/chat.send")
    public void processMessage(ChatMessage message, Principal principal) {
        String username = principal.getName();
        message.setSender(username);

        // Gán fullName của người gửi
        userService.findByUsername(username).ifPresentOrElse(
                user -> message.setSenderFullName(user.getFullName()),
                () -> message.setSenderFullName(username)
        );
        if (message.getConversationId() == null) {
            message.setConversationId(username);
        }
        ChatMessage savedMsg = chatRepository.save(message);
        messagingTemplate.convertAndSend("/topic/messages/" + message.getConversationId(), savedMsg);
    }

//    @GetMapping("/api/chat/conversations")
//    @ResponseBody
//    public ResponseEntity<List<UserChatDTO>> getConversations(Principal principal) {
//        List<String> usernames = chatService.getActiveConversations(principal);
//
//        List<UserChatDTO> result = usernames.stream().map(username -> {
//            String fullName = userService.findByUsername(username)
//                    .map(User::getFullName)
//                    .orElse(username);
//            return new UserChatDTO(username, fullName);
//        }).toList();
//
//        return ResponseEntity.ok(result);
//    }

    @GetMapping("/api/chat/conversations")
    @ResponseBody
    public ResponseEntity<List<UserChatDTO>> getConversations(Principal principal) {
        String currentUsername = principal.getName();

        // Giả định: Admin có quyền admin hoặc role tương ứng
        boolean isAdmin = principal.getName().equals("admin");

        List<String> rawRooms = chatService.getActiveConversations(principal);

        List<UserChatDTO> result = rawRooms.stream().map(room -> {
            String targetUsername;

            if (isAdmin) {
                // Admin thấy danh sách các User (phòng chat là tên User)
                targetUsername = room;
            } else {
                // User thấy Admin
                targetUsername = "admin";
            }

            String displayName = userService.findByUsername(targetUsername)
                    .map(User::getFullName)
                    .orElse(targetUsername);

            return new UserChatDTO(targetUsername, displayName);
        }).distinct().toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/chat/history/{conversationId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String conversationId, Principal principal) {
        if (!principal.getName().equals(conversationId) && !isAdmin(principal)) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(chatRepository.findByConversationIdOrderByTimestampAsc(conversationId));
    }

    @DeleteMapping("/api/chat/delete/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id, Principal principal) {
        ChatMessage msg = chatRepository.findById(id).orElse(null);
        if (msg != null && (msg.getSender().equals(principal.getName()) || isAdmin(principal))) {
            chatRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }
    @DeleteMapping("/api/chat/delete-all/{conversationId}")
    @ResponseBody
    public ResponseEntity<Void> deleteAllMessages(@PathVariable String conversationId, Principal principal) {
        if (isAdmin(principal) || principal.getName().equals(conversationId)) {
            List<ChatMessage> msgs = chatRepository.findByConversationIdOrderByTimestampAsc(conversationId);
            chatRepository.deleteAll(msgs);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(403).build();
    }

    private boolean isAdmin(Principal principal) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    @GetMapping("/api/chat/avatar/{username}")
    public ResponseEntity<String> getAvatarPath(@PathVariable String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user.getAvatarUrl());
    }
}
