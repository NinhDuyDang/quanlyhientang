package com.example.organdonationmanagement.service;

import com.example.organdonationmanagement.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public List<String> getActiveConversations(Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return chatRepository.findAll().stream()
                    .map(msg -> msg.getConversationId())
                    .distinct()
                    .collect(Collectors.toList());
        }
        return List.of(principal.getName());
    }
}
