import { Injectable } from '@angular/core';
import { Client, Frame, Stomp } from '@stomp/stompjs';
import { BehaviorSubject, Subject } from 'rxjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})

export class WebsocketService {

  private stompClient: Client | null = null;

  // Subject to manage the stream of incoming messages
  private messageSubject = new BehaviorSubject<any>(null);
  public messages$ = this.messageSubject.asObservable();  // Observable for components to subscribe to messages

  // Subject to track the connection status (connected/disconnected)
  private connectionSubject = new BehaviorSubject<boolean>(false);
  public connectionStatus$ = this.connectionSubject.asObservable();  // Observable for components to track connection status

  private userTypingSource = new Subject<string>();
  userTyping$ = this.userTypingSource.asObservable();


  connect (username:string){
    const url = 'http://localhost:8080/auth/login';

    // User credentials (username/password)
    const credentials = {
        username: 'your-username',
        password: 'your-password'
    };

    // Make the POST request using Fetch
    fetch(url, {
        method: 'POST',  // HTTP method
        headers: {
            'Content-Type': 'application/json'  // Set content type as JSON
        },
        body: JSON.stringify(credentials)  // Convert the credentials object to a JSON string
    })
    .then(response => {
        if (response.ok) {
            return response.text();  // Parse JSON response if successful
        }
        throw new Error('Login failed');
    })
    .then(data => {
        console.log('Login successful:', data);  // Handle successful login
        // Store the JWT token (if applicable)
        localStorage.setItem('jwtToken', data);

        const socket = new SockJS('http://localhost:8080/chat?username=' + encodeURIComponent(username)); // WebSocket endpoint of your backend
                this.stompClient = new Client({
                  webSocketFactory: () => socket,
                  connectHeaders: {
                    Authorization: 'Bearer ' + localStorage.getItem('jwtToken')  // Pass JWT token in the Authorization header
                  },
                  onConnect: (frame: Frame) => {
                    console.log('Connected: ', frame);
                    this.connectionSubject.next(true);
                    // After successful connection, subscribe to the topic

                    this.stompClient?.subscribe('/topic/history', (message: any) => {
                      JSON.parse(message.body).forEach((item : any) => {
                        this.messageSubject.next(item); // Push message to Subject
                      })
                      this.stompClient?.subscribe('/topic/public', (message: any) => {
                        this.messageSubject.next(JSON.parse(message.body)); // Push message to Subject
                      });
                      this.stompClient?.subscribe('/topic/userTyping', (message: any) => {
                        let msg = JSON.parse(message.body);
                        if (msg.username != username) {
                          console.log(msg);
                          this.userTypingSource.next(msg.content);

                          setTimeout(() => {
                            this.userTypingSource.next("no one is typing now");
                          }, 3000);
                        }
                      });
                    });

                    const chatMessage = { username: username, content: `${username} joined chat` };

                      this.stompClient?.publish({
                        destination: '/app/addUser',
                        body: JSON.stringify(chatMessage)
                      });
                  },
                  onDisconnect: () => {
                    console.log('Disconnected');
                  },
                  debug: (str) => {
                    console.log(str);
                  }
                });
                this.stompClient.activate();
    })
    .catch(error => {
        console.error('Error:', error);  // Handle errors (invalid credentials, server issues, etc.)
    });


  }


  sendMessage(username:string, content:string){
    if (this.stompClient && this.stompClient.connected) {
      // Create a chat message object
      const chatMessage = { username: username, content: content, type: 'CHAT' };

      // Log the message being sent and the sender
      console.log(`Message sent by ${username}: ${content}`);

      // Publish (send) the message to the '/app/chat.sendMessage' destination
      this.stompClient.publish({
        destination: '/app/sendMessage',
        body: JSON.stringify(chatMessage)  // Convert the message to JSON and send
      });
    } else {
      // Log an error if the WebSocket connection is not active
      console.error('WebSocket is not connected. Unable to send message.');
    }

  }

  sendUserTyping(username:string){
    if (this.stompClient && this.stompClient.connected) {
      // Create a chat message object
      const chatMessage = { username: username, content: `${username} is typing` };

      this.stompClient.publish({
        destination: '/app/userTyping',
        body: JSON.stringify(chatMessage)  // Convert the message to JSON and send
      });
    } else {
      // Log an error if the WebSocket connection is not active
      console.error('WebSocket is not connected. Unable to send message.');
    }

  }

  disconnect(){
    if (this.stompClient) {
      this.stompClient.deactivate();  // Deactivate the WebSocket connection
    }
  }

}
