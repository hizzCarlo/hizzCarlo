import { Component } from '@angular/core';
import { Router } from '@angular/router';



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'surveygenerator';
  responseUser: string = "";


  constructor(private _router: Router) { }

  get router() {
    return this._router;
  }
}
