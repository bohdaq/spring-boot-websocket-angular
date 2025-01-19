import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { WebsocketService } from './services/websocket.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import moment from 'moment';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  title = 'frontend';
  username: string = '';  // Stores the username entered by the user
  _message: string = '';  // Stores the message being typed by the user
  messages: any[] = [];  // Stores all the chat messages
  isConnected = false;  // Tracks whether the user is connected to the WebSocket
  connectingMessage = 'Connecting...';  // Message to show while connecting
  maxLength = 200;
  userTyping : string = 'no one is typing now';

  constructor( private websocketService: WebsocketService){
    console.log('AppComponent constructor called');
  }

  set message(value: string) {
    if (value != this._message) {
      this._message = value;
    }
    console.log('message changed:', value);
    this.websocketService.sendUserTyping(this.username)
  }

  ngOnInit(): void {
    console.log('AppComponent ngOnInit called');

     // Subscribe to messages observable to receive messages from the WebSocket service
     this.websocketService.messages$.subscribe(message => {
      if (message) {
        // Log and add the received message to the array of messages
        console.log(`Message received from ${message.username}: ${message.content} ${this.messages.length}`);
        message.timestamp = moment(message.timestamp).format('HH:mm');
        this.messages.push(message);
      }
    });

    // Subscribe to connection status observable to monitor connection status
    this.websocketService.connectionStatus$.subscribe(connected => {
      this.isConnected = connected;  // Update the connection status
      if (connected) {
        this.connectingMessage = '';  // Clear the connecting message once connected
        console.log('WebSocket connection established');
      }
    });

    this.websocketService.userTyping$.subscribe((message) => {
      this.userTyping = message;
    });

  }

  connect(){
    this.websocketService.connect(this.username);  // Call the WebSocket service to connect
  }

  sendMessage(){
    if (this._message && this._message.length <= this.maxLength) {
      this.websocketService.sendMessage(this.username, this._message);  // Send the message via WebSocket service
      this._message = '';  // Clear the message input after sending
    }
  }


  getAvatarColor(sender:string):string{

     // Array of colors to choose from
     const colors = ['#2196F3', '#32c787', '#00BCD4', '#ff5652', '#ffc107', '#ff85af', '#FF9800', '#39bbb0'];
     let hash = 0;
     for (let i = 0; i < sender.length; i++) {
       // Generate a hash from the sender's name
       hash = 31 * hash + sender.charCodeAt(i);  // Create a hash based on the username
     }
     // Return a color from the array based on the hash value
     return colors[Math.abs(hash % colors.length)];
   }


}
