import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ResponseSurveyService } from './responsesurvey.service';
import { Option, Question, Survey, UserResponse } from './responsesurvey';
import { NotificationEmailService } from './/notification-email.service';
import { HttpClient } from '@angular/common/http';


import { catchError, tap, throwError } from 'rxjs';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-responsesurvey',
  templateUrl: './responsesurvey.component.html',
  styleUrls: ['./responsesurvey.component.css']
})
export class ResponsesurveyComponent {
  codeSurvey: any;
  loadSurvey: Survey = new Survey();
  responseUser = new UserResponse();
  listOptions!:string[];

  StudentArray : any[] = [];
  isResultLoaded = false;
  isUpdateFormActive = false;

  
  surveyTitle: string = this.loadSurvey.title;
  respondentCourse: string = "";
  respondentOccupation: string = "";
  respondentStatus: string = "";
  respondentName: string = "";
  respondentEmail: string = "";
  currentStudentID = "";
  primaryUserId: string = '';

  //http://localhost:4200/surveyresponse/CSbh9g1kom

  constructor(private responseSurveyService : ResponseSurveyService ,private route: ActivatedRoute, private router: Router,  private notificationEmailService: NotificationEmailService, private http: HttpClient){
    this.getAllStudent();
  }
  
  ngOnInit(): void {
    this.codeSurvey = this.route.snapshot.paramMap.get('codeSurvey');
    this.findSurveyByCodeSurvey();
    this.fetchPrimaryUserId();  // Fetch the primary user ID on initialization
  }

  fetchPrimaryUserId() {
    this.http.get("http://localhost:7000/api/acc/primary").subscribe((result: any) => {
      if (result.status) {
        this.primaryUserId = result.data;
      } else {
        console.error('Failed to fetch primary user ID:', result.message);
      }
    }, error => {
      console.error('Error fetching primary user ID:', error);
    });
  }

  getAllStudent()
  { 
    this.http.get("http://localhost:7000/api/student")
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
      
      "respondentName" : this.respondentName,
      "respondentEmail" : this.respondentEmail,
      "respondentOccupation" : this.respondentOccupation,
      "respondentCourse" : this.respondentCourse,
      "respondentStatus" : this.respondentStatus,
      "surveyTitle" : this.loadSurvey.title,
      "userid": this.primaryUserId  // Include the fetched primary user ID
    };
    this.http.post("http://localhost:7000/api/student/add",bodyData).subscribe((resultData: any)=>
    {
        console.log(resultData);

        this.getAllStudent();
    
    });
  }
  
  setUpdate(data: any) 
  {
   this.respondentName = data.respondentName;
   this.respondentEmail = data.respondentEmail
   this.respondentCourse = data.respondentCourse
   this.respondentOccupation = data.respondentOccupation
   this.respondentStatus = data.respondentStatus
  
   this.currentStudentID = data.id;
 
  }
  UpdateRecords()
  {
    let bodyData = 
    {
      "respondentName" : this.respondentName,
      "respondentEmail" : this.respondentEmail,
      "respondentOccupation" : this.respondentOccupation,
      "respondentCourse" : this.respondentCourse,
      "respondentStatus" : this.respondentStatus
      
      
    };
    
    this.http.put("http://localhost:7000/api/student/update"+ "/"+ this.currentStudentID,bodyData).subscribe((resultData: any)=>
    {
        console.log(resultData);
        alert("Student Registered Updated")
        this.getAllStudent();
      
    });
  }
 
  save()
  {
    if(this.currentStudentID == '')
    {
        this.register();
    }
      else
      {
       this.UpdateRecords();
      }       
  }
  setDelete(data: any)
  {
    this.http.delete("http://localhost:7000/api/student/delete"+ "/"+ data.id).subscribe((resultData: any)=>
    {
        console.log(resultData);
        alert("Student Deleted")
        this.getAllStudent();
    });
  }


  findSurveyByCodeSurvey(){
    this.responseSurveyService.findSurveyByCodeSurvey(this.codeSurvey).subscribe(
      (data) => {
        this.loadSurvey = data;
      }
    );
  }

  updateSelectedOption(question: Question, selectedOption: Option) {
    question.options.forEach(option => {
      if (option !== selectedOption) {
        option.selectedOption = '';
      }
    });
  
    let selectedOptions: string[] = [];
  
    for (let question of this.loadSurvey.questions) {
      const selectedOption = question.options.find(option => option.selectedOption);
      if (selectedOption) {
        selectedOptions.push(selectedOption.selectedOption);
      }
    }
  
    this.listOptions = selectedOptions;
    console.log(JSON.stringify(this.listOptions));
  }

  saveResponses(){
    let allQuestionsAnswered = this.checkAllQuestionsAnswered();
    if (!allQuestionsAnswered) {
      alert('Please answer all questions before saving.');
      return;
    }

    if (!this.respondentEmail || !this.respondentName || !this.respondentCourse || !this.respondentOccupation || !this.respondentStatus) {
      alert('Please provide your name, email, occupation, Etc.');
      return;
    }

    this.responseUser.codeOption = this.listOptions;
    this.responseSurveyService.saveResponses(this.codeSurvey, this.responseUser).pipe(
      
      tap(() => {
        Swal.fire(
          'Survey saved!!',
          'The survey has been successfully saved!!',
          'success'
        ).then(() => {
          this.router.navigate(['/thank-you']);
          this.save()
          this.sendEmailAndNotification()
        })
      }),
      catchError((e: any) => {
        alert(e.error.messageInfo);
        return throwError(() => e);
      })
    )
    .subscribe();
  }

  sendEmailAndNotification(): void {
    if (!this.respondentEmail || !this.respondentName) {
      alert('Please provide your username and email.');
      return;
    }

    const subject = 'Formalytics';
    const body = 'Hey there!';
    const to = this.respondentEmail;
    
    // Send email
    this.notificationEmailService.sendEmail(subject, body, to)
      .then(() => {
        console.log('Email sent successfully');
      })
      .catch(error => {
        console.error('Failed to send email:', error);
      });
    
    // Send notification
    
   this.notificationEmailService.sendNotification('Formalytics', 'Response has been save!');
  }

  checkAllQuestionsAnswered(): boolean {
    for (let question of this.loadSurvey.questions) {
      if (!question.options.some(option => option.selectedOption)) {
        return false;
      }
    }
    return true;
  }
  
}
