// notification-email.service.ts

import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationEmailService {

  constructor() { }

  sendEmail(subject: string, body: string, to: string): Promise<void> {
    // Here you would make an HTTP POST request to your backend to send the email
    // Replace the URL with your backend endpoint for sending emails
    return fetch('http://localhost:1000/send-email', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ subject, body, to })
    }).then(response => {
      if (!response.ok) {
        throw new Error('Failed to send email');
      }
    });
  }

  sendNotification(title: string, body: string): void {
    // Here you can implement your notification logic, such as using browser's native notifications
    // This example sends a browser notification
    if (!("Notification" in window)) {
      alert('This browser does not support desktop notification');
    } else if (Notification.permission === "granted") {
      new Notification(title, { body });
    } else if (Notification.permission !== 'denied') {
      Notification.requestPermission().then(permission => {
        if (permission === "granted") {
          new Notification(title, { body });
        }
      });
    }
  }
}
