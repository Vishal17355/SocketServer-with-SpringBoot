package org.example.ubersocketserver.Controller;

import org.example.ubersocketserver.DTO.ChatRequest;
import org.example.ubersocketserver.DTO.ChatResponse;
import org.example.ubersocketserver.DTO.TestRequest;
import org.example.ubersocketserver.DTO.TestResponse;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class TestController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public TestController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/ping")       // client sends to /app/ping
    @SendTo("/topic/ping")         // response sent to /topic/ping
    public TestResponse pingCheck(TestRequest message) {
        System.out.println("Received from client: " + message.getData());
        return new TestResponse("Server received: " + message.getData());
    }

//    @Scheduled(fixedDelay = 2000)
//    public void sendPeriodicMessage() {
//        simpMessagingTemplate.convertAndSend(
//                "/topic/scheduled",
//                "Periodic message at " + System.currentTimeMillis()
//        );
   // }
    @MessageMapping("/chat/{room}")
    @SendTo("/topic/message/{room}")
    public ChatResponse chatMessage(@DestinationVariable String room, ChatRequest request){
        ChatResponse response = ChatResponse.builder()
        .name(request.getName())
                .message(request.getMessage())
                .timeStamp("" +System.currentTimeMillis())
                .build();
        return response;
    }
    // when u send something to someone personal
    @MessageMapping("/chat/{room}/{userId}")
    @SendTo("/topic/PrivateMessage/{room}/{userId}")
    public void privateChatMessage(@DestinationVariable String room, @DestinationVariable String userId,ChatRequest request){
        ChatResponse response = ChatResponse.builder()
                .name(request.getName())
                .message(request.getMessage())
                .timeStamp("" +System.currentTimeMillis())
                .build();
        simpMessagingTemplate.convertAndSendToUser(  userId,   "/queue/privateMessage/" + room,  response);

    }
}
