import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RespondentDataService {
  private respondentNameSource = new BehaviorSubject<string>('');
  respondentName$ = this.respondentNameSource.asObservable();

  private respondentEmailSource = new BehaviorSubject<string>('');
  respondentEmail$ = this.respondentEmailSource.asObservable();

  constructor() { }

  setRespondentData(name: string, email: string) {
    this.respondentNameSource.next(name);
    this.respondentEmailSource.next(email);
  }
}
