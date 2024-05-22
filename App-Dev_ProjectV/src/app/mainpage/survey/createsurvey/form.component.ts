import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Survey } from '../survey';
import { FormService } from './form.service';
import { Router } from '@angular/router';
import { catchError, tap, throwError } from 'rxjs';
import Swal from 'sweetalert2';
import { SurveyService } from '../survey.service';
import { Category } from '../category';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {
  public newSurvey:Survey = new Survey();
  categories!: Category[];
  titleString: string = this.newSurvey.title; // Additional string variable to store the title

  StudentArray : any[] = [];
  isResultLoaded = false;
  isUpdateFormActive = false;
  currentSurveyID = "";

  constructor(private formService : FormService, private surveyService: SurveyService, private router: Router, private http: HttpClient)
  {
    this.getAllSurvey();
  }
  


  ngOnInit(): void {
    this.getCategories();
  }
  getAllSurvey()
  { 
    this.http.get("http://localhost:7000/api/survey")
    .subscribe((resultData: any)=>
    {
        this.isResultLoaded = true;
        console.log(resultData.data);
        this.StudentArray = resultData.data;
    });
  }
  register()
  {
   
    let bodyData = {
      "titleString" : this.titleString
      
    };
    this.http.post("http://localhost:7000/api/survey/add",bodyData).subscribe((resultData: any)=>
    {
        console.log(resultData);

        this.getAllSurvey();
    
    });
  }
  public getCategories() {
    this.surveyService.getCategories().subscribe(
      (data) => {
        this.categories = data;
        if (this.categories && this.categories.length > 0) {
          this.newSurvey.category = this.categories[0]; // Asignar el primer registro a una propiedad
        }
      }
    );
  }

  UpdateRecords()
  {
    let bodyData = 
    {
      "titleString" : this.titleString
      
      
    };
    
    this.http.put("http://localhost:7000/api/survey/update"+ "/"+ this.currentSurveyID,bodyData).subscribe((resultData: any)=>
    {
        console.log(resultData);
        alert("Student Registered Updated")
        this.getAllSurvey();
      
    });
  }

  save()
  {
    if(this.currentSurveyID == '')
    {
        this.register();
    }
      else
      {
       this.UpdateRecords();
      }       
  }

  createSurvey(){
    this.titleString = this.newSurvey.title; // Update the string variable when creating the survey
    this.formService.createSurvey(this.newSurvey).pipe(
      tap((survey) => {
        this.http.post('http://localhost:7000/api/survey/add', { title: survey.title })
          .subscribe({
            next: (response) => console.log('Survey title saved:', response),
            error: (error) => console.error('Error saving survey title:', error)
          });

        Swal.fire(
          'Survey "' + this.newSurvey.title + '", has been saved',
          'Your survey was saved successfully.',
          'success'
        ).then(() => {
          this.router.navigate(['/survey']);
        })
      }),
      catchError((e: any) => {
        alert(e.error.messageInfo);
        return throwError(() => e);
      })
    )
    .subscribe();
  }

  close(){
    this.router.navigate(['/survey']);
  }

}
